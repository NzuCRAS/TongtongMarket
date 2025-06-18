package Persistence;

import org.mindrot.jbcrypt.BCrypt;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import Model.*;

public class PersUser {
    private static final Logger logger = Logger.getLogger(PersUser.class.getName());

    // AES加密配置 - 使用明确的16字节密钥
    private static final String AES_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final byte[] SECRET_KEY_BYTES = {
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
            0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10
    };// 修正为16字节密钥

    // SQL语句集中管理
    private static final String SQL_CHECK_USER =
            "SELECT * FROM User WHERE username = ?";
    private static final String SQL_ADD_USER =
            "INSERT INTO User (username, password, realname, idNumber, vipId, address, phone) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_CHECK_ID_EXISTS =
            "SELECT COUNT(*) AS count FROM User WHERE idNumber = ?";
    private static final String SQL_GET_MAX_VIPID =
            "SELECT MAX(vipId) AS maxVipId FROM User WHERE vipId IS NOT NULL";
    private static final String SQL_UPDATE_VIPID =
            "UPDATE User SET vipId = ? WHERE id = ?";

    // 确定性AES加密
    private String deterministicEncrypt(String data) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY_BYTES, "AES");
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "身份证号加密失败: " + e.getMessage(), e);
            throw new RuntimeException("加密处理失败: " + e.getMessage(), e);
        }
    }

    // 检查是否一个身份证绑定多个账户（参数不变）
    public boolean isAllowToRegister(String idNumber) {
        // 1. 加密身份证号
        String encryptedId = deterministicEncrypt(idNumber);

        // 2. 使用加密后的值查询
        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(SQL_CHECK_ID_EXISTS)) {

            pstmt.setString(1, encryptedId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count > 0; // 存在记录表示该身份证已绑定账户
                }
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "身份证重复性检查失败", e);
            throw new RuntimeException("数据库查询失败", e);
        }
    }

    // 添加用户（包含身份证加密）
    public boolean addUser(User user) {
        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(SQL_ADD_USER)) {

            // 加密敏感数据
            String encryptedId = deterministicEncrypt(user.getIdNumber());
            String hashedPwd = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));

            // 设置参数
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashedPwd);
            pstmt.setString(3, user.getRealname());
            pstmt.setString(4, encryptedId); // 存储加密后的身份证
            pstmt.setInt(5, user.getVipId());
            pstmt.setString(6, user.getAddress());
            pstmt.setString(7, user.getPhone());

            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "添加用户失败", e);
            return false;
        }
    }

    // 用户验证（带密码哈希验证）
    public User checkUser(String username, String inputPassword) {
        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(SQL_CHECK_USER)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");

                    // 验证密码哈希
                    if (BCrypt.checkpw(inputPassword, storedHash)) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setUsername(rs.getString("username"));
                        user.setPassword(""); // 清除敏感数据
                        user.setRealname(rs.getString("realname"));
                        // 身份证号不解密返回
                        user.setIdNumber("");
                        user.setVipId(rs.getInt("vipId"));
                        user.setAddress(rs.getString("address"));
                        user.setPhone(rs.getString("phone"));
                        return user;
                    }
                }
                return null; // 认证失败
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "用户验证失败", e);
            throw new RuntimeException("数据库查询失败", e);
        }
    }

    // 升级VIP（带事务处理）
    public boolean updateUser(int id) {
        Connection ct = null;
        try {
            ct = ConnectionPool.getConnection();
            ct.setAutoCommit(false); // 开启事务

            // 查询当前最大VIP ID
            int newVipId;
            try (PreparedStatement pstmt = ct.prepareStatement(SQL_GET_MAX_VIPID);
                 ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    newVipId = rs.getInt("maxVipId") + 1;
                } else {
                    newVipId = 1000; // 起始VIP ID
                }
            }

            // 更新用户VIP状态
            try (PreparedStatement pstmt = ct.prepareStatement(SQL_UPDATE_VIPID)) {
                pstmt.setInt(1, newVipId);
                pstmt.setInt(2, id);

                int updated = pstmt.executeUpdate();
                if (updated == 1) {
                    ct.commit(); // 提交事务
                    return true;
                }
            }

            ct.rollback(); // 失败回滚
            return false;
        } catch (SQLException e) {
            if (ct != null) {
                try {
                    ct.rollback(); // 异常回滚
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "事务回滚失败", ex);
                }
            }
            logger.log(Level.SEVERE, "VIP升级失败", e);
            return false;
        } finally {
            if (ct != null) {
                try {
                    ct.setAutoCommit(true); // 重置自动提交
                    ct.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "连接关闭失败", e);
                }
            }
        }
    }
}