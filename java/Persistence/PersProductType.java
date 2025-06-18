package Persistence;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PersProductType {
    private static final Logger logger = Logger.getLogger(PersProductType.class.getName());

    // 集中管理 SQL 语句
    private static final String SQL_GET_SORT_ID =
        "SELECT sortId FROM ProductType WHERE sortName = ?";

    // 类型名字得到类型 id
    public int getSortId(String sortName) {
        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(SQL_GET_SORT_ID)) {

            pstmt.setString(1, sortName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("sortId");
                }
                return -1; // 未找到
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "获取商品类别ID失败: " + sortName, e);
            throw new PersistenceException("数据库查询失败", e);
        }
    }

    // 自定义异常类
    public static class PersistenceException extends RuntimeException {
        public PersistenceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}