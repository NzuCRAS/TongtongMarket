package Persistence;

import Model.*;
import Model.OrderInfo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersOrder {
    private static final Logger logger = Logger.getLogger(PersOrder.class.getName());

    // 集中管理SQL语句
    private static final String SQL_INSERT_ORDER_INFO =
            "INSERT INTO OrderInfo(orderId, productId, productName, price, productAmount) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_INSERT_ORDER =
            "INSERT INTO `Order` (orderId, id, date, isPay, sumPrice) " +  // 使用反引号避免关键字冲突
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_SETTLE_ORDER =
            "UPDATE `Order` SET isPay = true WHERE orderId = ?";

    private static final String SQL_DELETE_ORDER =
            "DELETE FROM `Order` WHERE orderId = ?";

    private static final String SQL_GET_ORDERS_BY_USER =
            "SELECT * FROM `Order` WHERE id = ?";

    private static final String SQL_GET_ALL_ORDERS =
            "SELECT * FROM `Order`";

    private static final String SQL_GET_ORDER_DETAILS =
            "SELECT * FROM `OrderInfo` WHERE orderId = ?";

    private static final String SQL_CHECK_ISPAID =
            "SELECT isPay FROM `Order` WHERE orderId = ?";

    // 生成订单（完整事务处理）
    public Order generateOrder(List<OrderInfo> orderInfos, int userId,String orderId){
        Connection ct = null;
        try {
            ct = ConnectionPool.getConnection();
            ct.setAutoCommit(false);  // 开启事务

            if (orderInfos.isEmpty()||orderInfos == null) {
                logger.warning("生成订单失败: 购物车为空, 用户ID=" + userId);
                return null;
            }

            // 1. 生成唯一订单ID
            String orderDate = RandomStringGenerator.getReadableCurrentTime();


            // 3. 设置所有订单详情的orderId
            for (OrderInfo item : orderInfos) {
                item.setOrderId(orderId); // 关键修复：设置相同的订单ID
            }

            // 3. 计算总金额并创建订单详情
            double totalAmount = 0.0;
            for (OrderInfo item : orderInfos) {
                Product product = getProduct(ct, item.getProductId());
                if (product == null) {
                    logger.warning("商品不存在: ID=" + item.getProductId());
                    ct.rollback();
                    return null;
                }

                // 检查库存
                if (product.getStock() < item.getProductAmount()) {
                    logger.warning("库存不足: 商品ID=" + item.getProductId() +
                            " 库存=" + product.getStock() +
                            " 需求=" + item.getProductName());
                    ct.rollback();
                    return null;
                }

                totalAmount += product.getPrice() * item.getProductAmount();
            }

            //创建主订单
            Order order = new Order();
            order.setOrderId(orderId);
            order.setId(userId);
            order.setDate(orderDate);
            order.setIsPay(false);
            order.setSumPrice(totalAmount);

            //保存订单到数据库
            insertOrder(order);

            //保存订单详情
            for (OrderInfo info : orderInfos) {
                saveOrderInfo(ct, info);
            }

            ct.commit();  // 提交事务
            logger.info("订单生成成功: 订单ID=" + orderId + ", 用户ID=" + userId);
            return order;
        } catch (SQLException e) {
            if (ct != null) {
                try {
                    ct.rollback();  // 异常回滚
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "事务回滚失败", ex);
                }
            }
            logger.log(Level.SEVERE, "生成订单失败: 用户ID=" + userId, e);
            throw new PersistenceException("订单生成失败", e);
        } finally {
            if (ct != null) {
                try {
                    ct.setAutoCommit(true);  // 重置自动提交
                    ct.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "连接关闭失败", e);
                }
            }
        }
    }

    // 获取商品信息,计算总价用
    private Product getProduct(Connection ct, int productId) throws SQLException {
        try (PreparedStatement pstmt = ct.prepareStatement(
                "SELECT * FROM ProductInfo WHERE productId = ?")) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("productId"));
                    product.setProductName(rs.getString("productName"));
                    product.setPrice(rs.getDouble("price"));
                    product.setStock(rs.getInt("stock"));
                    return product;
                }
                return null;
            }
        }
    }

    // 保存订单详情
    public void saveOrderInfo(Connection ct, OrderInfo orderInfo) throws SQLException {
        try (PreparedStatement pstmt = ct.prepareStatement(SQL_INSERT_ORDER_INFO)) {
            pstmt.setString(1, orderInfo.getOrderId());
            pstmt.setInt(2, orderInfo.getProductId());
            pstmt.setString(3, sanitizeInput(orderInfo.getProductName()));
            pstmt.setDouble(4, orderInfo.getPrice());
            pstmt.setInt(5, orderInfo.getProductAmount());

            int inserted = pstmt.executeUpdate();
            if (inserted != 1) {
                throw new SQLException("订单详情保存失败");
            }
        }
    }

    // 保存主订单,入库
    private void insertOrder(Order order) throws SQLException {
        Connection ct = ConnectionPool.getConnection();
        try (PreparedStatement pstmt = ct.prepareStatement(SQL_INSERT_ORDER)) {
            pstmt.setString(1, order.getOrderId());
            pstmt.setInt(2, order.getId());
            pstmt.setString(3, order.getDate());
            pstmt.setBoolean(4, order.isPay());
            pstmt.setDouble(5, order.getSumPrice());

            int inserted = pstmt.executeUpdate();
            if (inserted != 1) {
                throw new SQLException("主订单保存失败");
            }
        }
    }
    //检查支付状态
    public  boolean checkIsPaid(String orderId){
        Connection ct = null;
        try {
            ct = ConnectionPool.getConnection();

            try (PreparedStatement pstmt = ct.prepareStatement(SQL_CHECK_ISPAID)) {
                pstmt.setString(1, orderId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getBoolean("isPay");
                }
            }
            return  false;
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "支付状态检查失败: productID=", e);
            throw new PersProduct.PersistenceException("支付状态库存失败", e);
        }
    }

    // 结算订单（完整事务处理）
    public boolean settlement(String orderId) {
        Connection ct = null;
        try {
            ct = ConnectionPool.getConnection();

            try (PreparedStatement pstmtcheck = ct.prepareStatement(SQL_SETTLE_ORDER)) {
                pstmtcheck.setString(1, orderId);
                int updated = pstmtcheck.executeUpdate();
                if (updated != 1) {
                    throw new SQLException("支付状态更新失败");
                }
            }
            logger.info("订单结算成功: 订单ID=" + orderId);
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "结算失败: productID=", e);
            throw new PersProduct.PersistenceException("结算失败", e);
        }
    }

    // 删除订单（级联删除）
    public boolean deleteOneOrder(String orderId) {
        Connection ct = null;
        try {
            ct = ConnectionPool.getConnection();
            ct.setAutoCommit(false);  // 开启事务

            // 1. 删除订单详情
            try (PreparedStatement pstmt = ct.prepareStatement(
                    "DELETE FROM OrderInfo WHERE orderId = ?")) {
                pstmt.setString(1, orderId);
                pstmt.executeUpdate();
            }

            // 2. 删除主订单
            try (PreparedStatement pstmt = ct.prepareStatement(SQL_DELETE_ORDER)) {
                pstmt.setString(1, orderId);
                int deleted = pstmt.executeUpdate();
                if (deleted != 1) {
                    throw new SQLException("主订单删除失败");
                }
            }

            ct.commit();  // 提交事务
            logger.info("订单删除成功: 订单ID=" + orderId);
            return true;
        } catch (SQLException e) {
            if (ct != null) {
                try {
                    ct.rollback();  // 异常回滚
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "事务回滚失败", ex);
                }
            }
            logger.log(Level.SEVERE, "订单删除失败: 订单ID=" + orderId, e);
            throw new PersistenceException("订单删除失败", e);
        } finally {
            if (ct != null) {
                try {
                    ct.setAutoCommit(true);  // 重置自动提交
                    ct.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "连接关闭失败", e);
                }
            }
        }
    }

    // 获取用户订单
    public ArrayList<Order> getOrdersFromId(int userId) {
        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(SQL_GET_ORDERS_BY_USER)) {

            pstmt.setInt(1, userId);
            return executeOrderQuery(pstmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "获取用户订单失败: 用户ID=" + userId, e);
            throw new PersistenceException("订单查询失败", e);
        }
    }

    // 获取所有订单
    public ArrayList<Order> getOrdersAll() {
        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(SQL_GET_ALL_ORDERS)) {

            return executeOrderQuery(pstmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "获取所有订单失败", e);
            throw new PersistenceException("订单查询失败", e);
        }
    }

    // 执行订单查询（得到某个用户/全部订单）
    private ArrayList<Order> executeOrderQuery(PreparedStatement pstmt) throws SQLException {
        ArrayList<Order> orders = new ArrayList<>();
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        }
        return orders;
    }

    // 获取订单详情,结算订单的时候找到需要更新销量和库存的那些订单详情
    private List<OrderInfo> getOrderDetails(Connection ct, String orderId) throws SQLException {
        List<OrderInfo> details = new ArrayList<>();
        try (PreparedStatement pstmt = ct.prepareStatement(SQL_GET_ORDER_DETAILS)) {
            pstmt.setString(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderInfo info = new OrderInfo();
                    info.setOrderId(rs.getString("orderId"));
                    info.setProductId(rs.getInt("productId"));
                    info.setProductName(rs.getString("productName"));
                    info.setPrice(rs.getDouble("price"));
                    info.setProductAmount(rs.getInt("productAmount"));
                    details.add(info);
                }
            }
        }
        return details;
    }

    // 映射ResultSet到Order对象
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getString("orderId"));
        order.setId(rs.getInt("id"));
        order.setDate(rs.getString("date"));
        order.setIsPay(rs.getBoolean("isPay"));
        order.setSumPrice(rs.getDouble("sumPrice"));
        return order;
    }

    // 自定义异常类
    public static class PersistenceException extends RuntimeException {
        public PersistenceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // 输入净化（防止XSS）
    private String sanitizeInput(String input) {
        if (input == null) return null;
        return input.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }
}