package Persistence;

import Model.*;
import Model.OrderInfo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersOrderInfo {
    private static final Logger logger = Logger.getLogger(PersOrder.class.getName());
    public PersOrderInfo(){}

    // 集中管理SQL语句
    private static final String SQL_INSERT_ORDER_INFO =
            "INSERT INTO OrderInfo(orderId, productId, productName, price, productAmount) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_GET_ORDER_DETAILS =
            "SELECT * FROM OrderInfo WHERE orderId = ?";

    // 插入订单详情（带事务）,插入数据库
    public boolean insertOrderInfo(OrderInfo orderInfo) {
        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(SQL_INSERT_ORDER_INFO)) {

            pstmt.setString(1, orderInfo.getOrderId());
            pstmt.setInt(2, orderInfo.getProductId());
            pstmt.setString(3, sanitizeInput(orderInfo.getProductName()));  // 输入净化
            pstmt.setDouble(4, orderInfo.getPrice());
            pstmt.setInt(5, orderInfo.getProductAmount());

            int inserted = pstmt.executeUpdate();
            if (inserted == 1) {
                logger.info("订单详情添加成功: 订单ID=" + orderInfo.getOrderId() +
                        ", 商品ID=" + orderInfo.getProductId());
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "添加订单详情失败: 订单ID=" + orderInfo.getOrderId(), e);
            throw new PersOrder.PersistenceException("订单详情添加失败", e);
        }
    }

    // 执行订单详情查询
    private ArrayList<OrderInfo> executeOrderInfoQuery(PreparedStatement pstmt) throws SQLException {
        ArrayList<OrderInfo> orderInfos = new ArrayList<>();
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                orderInfos.add(mapResultSetToOrderInfo(rs));
            }
        }
        return orderInfos;
    }

    // 映射ResultSet到OrderInfo对象
    private OrderInfo mapResultSetToOrderInfo(ResultSet rs) throws SQLException {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderId(rs.getString("orderId"));
        orderInfo.setProductId(rs.getInt("productId"));
        orderInfo.setProductName(rs.getString("productName"));
        orderInfo.setPrice(rs.getDouble("price"));
        orderInfo.setProductAmount(rs.getInt("productAmount"));
        return orderInfo;
    }

    // 显示订单详情
    public ArrayList<OrderInfo> showOrder(String orderId) {
        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(SQL_GET_ORDER_DETAILS)) {
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            ArrayList<OrderInfo> orderInfos = new ArrayList<>();
            while (rs.next()) {
                OrderInfo orderInfo = new OrderInfo();
                orderInfo.setOrderId(rs.getString("orderId"));
                orderInfo.setProductId(rs.getInt("productId"));
                orderInfo.setProductName(rs.getString("productName"));
                orderInfo.setPrice(rs.getDouble("price"));
                orderInfo.setProductAmount(rs.getInt("productAmount"));
                orderInfos.add(orderInfo);
            }
            return orderInfos;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "获取订单详情失败: 订单ID=" + orderId, e);
            throw new PersOrder.PersistenceException("订单查询失败", e);
        }
    }

    // 输入净化（防止XSS）
    private String sanitizeInput(String input) {
        if (input == null) return null;
        return input.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }
}
