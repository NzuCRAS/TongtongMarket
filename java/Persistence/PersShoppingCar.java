package Persistence;

import Model.ShoppingCar;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersShoppingCar {
    private static final Logger logger = Logger.getLogger(PersShoppingCar.class.getName());

    // 集中管理SQL语句
    private static final String SQL_DELETE_ITEM =
            "DELETE FROM ShoppingCar WHERE id = ? AND productId = ?";
    private static final String SQL_ADD_ITEM =
            "INSERT INTO ShoppingCar(id, productId, productAmount) VALUES(?, ?, ?)";
    private static final String SQL_DECREMENT_ITEM =
            "UPDATE ShoppingCar SET productAmount = productAmount - 1 WHERE id = ? AND productId = ?";
    private static final String SQL_INCREMENT_ITEM =
            "UPDATE ShoppingCar SET productAmount = productAmount + 1 WHERE id = ? AND productId = ?";
    private static final String SQL_DELETE_USER_CART =
            "DELETE FROM ShoppingCar WHERE id = ?";
    private static final String SQL_GET_CART_ITEMS =
            "SELECT * FROM ShoppingCar WHERE id = ?";
    private static final String SQL_CHECK_ITEM_EXISTS =
            "SELECT productAmount FROM ShoppingCar WHERE id = ? AND productId = ?";
    private static final String SQL_GET_PRODUCT_STOCK =
            "SELECT stock FROM ProductInfo WHERE productId = ?";
    private static final String SQL_UPDATE_AMOUNT =
            "UPDATE ShoppingCar SET productAmount = productAmount + ? WHERE id = ? AND productId = ?";

    // 删除购物车商品项
    public boolean deleteProduct(int userId, int productId) {
        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(SQL_DELETE_ITEM)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);

            int deleted = pstmt.executeUpdate();
            if (deleted == 1) {
                logger.info("删除购物车商品: 用户ID=" + userId + ", 商品ID=" + productId);
                return true;
            }
            logger.warning("删除购物车商品失败: 未找到商品 用户ID=" + userId + ", 商品ID=" + productId);
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "删除购物车商品失败", e);
            throw new PersistenceException("购物车商品删除失败", e);
        }
    }

    /*// 添加商品到购物车（带库存检查和存在性判断）
    public boolean addProduct(int userId, int productId, int amount) {
        if (amount <= 0) {
            logger.warning("无效的添加数量: " + amount);
            return false;
        }

        try (Connection ct = ConnectionPool.getConnection();
             // 检查库存
             PreparedStatement stockStmt = ct.prepareStatement(SQL_GET_PRODUCT_STOCK);
             // 检查购物车中是否已存在该商品
             PreparedStatement checkStmt = ct.prepareStatement(SQL_CHECK_ITEM_EXISTS);
             // 添加商品到购物车（插入）
             PreparedStatement addStmt = ct.prepareStatement(SQL_ADD_ITEM);
             // 更新购物车中商品数量
             PreparedStatement updateStmt = ct.prepareStatement(SQL_UPDATE_AMOUNT);) {

            // 1. 检查商品库存
            stockStmt.setInt(1, productId);
            try (ResultSet rs = stockStmt.executeQuery()) {
                if (!rs.next()) {
                    logger.warning("商品不存在: ID=" + productId);
                    return false;
                }
                int stock = rs.getInt("stock");
                if (stock < amount) {
                    logger.warning("库存不足: 商品ID=" + productId +
                            " 请求数量=" + amount + " 当前库存=" + stock);
                    return false;
                }
            }

            // 2. 检查购物车中是否已存在该商品
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, productId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // 购物车中已存在该商品，更新数量
                    int currentAmount = rs.getInt("productAmount");
                    int newAmount = currentAmount + amount;

                    // 再次检查库存是否足够
                    if (newAmount > getStock(ct, productId)) {
                        logger.warning("库存不足: 商品ID=" + productId +
                                " 新请求数量=" + newAmount + " 当前库存=" + getStock(ct, productId));
                        return false;
                    }

                    // 执行更新操作
                    updateStmt.setInt(1, amount);
                    updateStmt.setInt(2, userId);
                    updateStmt.setInt(3, productId);
                    int updated = updateStmt.executeUpdate();
                    if (updated == 1) {
                        logger.info("更新购物车商品数量: 用户ID=" + userId +
                                ", 商品ID=" + productId + ", 新增数量=" + amount + ", 新数量=" + newAmount);
                        return true;
                    }
                    logger.warning("更新购物车商品数量失败: 用户ID=" + userId + ", 商品ID=" + productId);
                    return false;
                } else {
                    // 购物车中不存在该商品，执行插入操作
                    addStmt.setInt(1, userId);
                    addStmt.setInt(2, productId);
                    addStmt.setInt(3, amount);
                    int added = addStmt.executeUpdate();
                    if (added == 1) {
                        logger.info("添加购物车商品: 用户ID=" + userId +
                                ", 商品ID=" + productId + ", 数量=" + amount);
                        return true;
                    }
                    logger.warning("添加购物车商品失败: 用户ID=" + userId + ", 商品ID=" + productId);
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "添加购物车商品失败", e);
            throw new PersistenceException("购物车商品添加失败", e);
        }
    }*/
    // 添加商品到购物车（带库存检查和存在性判断）
    public boolean addProduct(int userId, int productId, int amount) {
        if (amount <= 0) {
            logger.warning("无效的添加数量: " + amount);
            return false;
        }

        try (Connection ct = ConnectionPool.getConnection();
             // 检查库存
             PreparedStatement stockStmt = ct.prepareStatement(SQL_GET_PRODUCT_STOCK);
             // 检查购物车中是否已存在该商品
             PreparedStatement checkStmt = ct.prepareStatement(SQL_CHECK_ITEM_EXISTS);
             // 添加商品到购物车（插入）
             PreparedStatement addStmt = ct.prepareStatement(SQL_ADD_ITEM);
             // 更新购物车中商品数量
             PreparedStatement updateStmt = ct.prepareStatement(SQL_UPDATE_AMOUNT);) {

            // 1. 检查商品库存
            stockStmt.setInt(1, productId);
            try (ResultSet rs = stockStmt.executeQuery()) {
                if (!rs.next()) {
                    logger.warning("商品不存在: ID=" + productId);
                    return false;
                }
                int stock = rs.getInt("stock");
                if (stock < amount) {
                    logger.warning("库存不足: 商品ID=" + productId +
                            " 请求数量=" + amount + " 当前库存=" + stock);
                    return false;
                }
            }

            // 2. 检查购物车中是否已存在该商品
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, productId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // 购物车中已存在该商品，更新数量
                    int currentAmount = rs.getInt("productAmount");
                    int newAmount = currentAmount + amount;

                    // 再次检查库存是否足够
                    if (newAmount > getStock(ct, productId)) {
                        logger.warning("库存不足: 商品ID=" + productId +
                                " 新请求数量=" + newAmount + " 当前库存=" + getStock(ct, productId));
                        return false;
                    }

                    // 执行更新操作
                    updateStmt.setInt(1, amount);
                    updateStmt.setInt(2, userId);
                    updateStmt.setInt(3, productId);
                    int updated = updateStmt.executeUpdate();
                    if (updated == 1) {
                        logger.info("更新购物车商品数量: 用户ID=" + userId +
                                ", 商品ID=" + productId + ", 新增数量=" + amount + ", 新数量=" + newAmount);
                        return true;
                    }
                    logger.warning("更新购物车商品数量失败: 用户ID=" + userId + ", 商品ID=" + productId);
                    return false;
                } else {
                    // 购物车中不存在该商品，执行插入操作
                    addStmt.setInt(1, userId);
                    addStmt.setInt(2, productId);
                    addStmt.setInt(3, amount);
                    try {
                        int added = addStmt.executeUpdate();
                        if (added == 1) {
                            logger.info("添加购物车商品: 用户ID=" + userId +
                                    ", 商品ID=" + productId + ", 数量=" + amount);
                            return true;
                        }
                    } catch (SQLIntegrityConstraintViolationException e) {
                        // 处理主键冲突异常，尝试更新数量
                        logger.warning("尝试插入重复记录，尝试更新数量: 用户ID=" + userId + ", 商品ID=" + productId);
                        updateStmt.setInt(1, amount);
                        updateStmt.setInt(2, userId);
                        updateStmt.setInt(3, productId);
                        int updated = updateStmt.executeUpdate();
                        if (updated == 1) {
                            logger.info("更新购物车商品数量: 用户ID=" + userId +
                                    ", 商品ID=" + productId + ", 新增数量=" + amount);
                            return true;
                        }
                    }
                    logger.warning("添加购物车商品失败: 用户ID=" + userId + ", 商品ID=" + productId);
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "添加购物车商品失败", e);
            throw new PersistenceException("购物车商品添加失败", e);
        }
    }

    // 获取商品库存的辅助方法
    private int getStock(Connection ct, int productId) throws SQLException {
        try (PreparedStatement stmt = ct.prepareStatement(SQL_GET_PRODUCT_STOCK)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stock");
                }
            }
        }
        return 0;
    }

    /*// 添加商品到购物车（带库存检查）
    public boolean addProduct(int userId, int productId, int amount) {
        if (amount <= 0) {
            logger.warning("无效的添加数量: " + amount);
            return false;
        }

        try (Connection ct = ConnectionPool.getConnection();
             // 检查库存
             PreparedStatement stockStmt = ct.prepareStatement(SQL_GET_PRODUCT_STOCK);
             // 添加商品
             PreparedStatement addStmt = ct.prepareStatement(SQL_ADD_ITEM)) {

            // 1. 检查商品库存
            stockStmt.setInt(1, productId);
            try (ResultSet rs = stockStmt.executeQuery()) {
                if (rs.next()) {
                    int stock = rs.getInt("stock");
                    if (stock < amount) {
                        logger.warning("库存不足: 商品ID=" + productId +
                                " 请求数量=" + amount + " 当前库存=" + stock);
                        return false;
                    }
                } else {
                    logger.warning("商品不存在: ID=" + productId);
                    return false;
                }
            }

            // 2. 添加商品到购物车
            addStmt.setInt(1, userId);
            addStmt.setInt(2, productId);
            addStmt.setInt(3, amount);

            int added = addStmt.executeUpdate();
            if (added == 1) {
                logger.info("添加购物车商品: 用户ID=" + userId +
                        ", 商品ID=" + productId + ", 数量=" + amount);
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "添加购物车商品失败", e);
            throw new PersistenceException("购物车商品添加失败", e);
        }
    }*/

    // 减少购物车商品数量
    public boolean subOneProduct(int userId, int productId) {
        try (Connection ct = ConnectionPool.getConnection()) {
            // 1. 检查当前数量
            try (PreparedStatement checkStmt = ct.prepareStatement(SQL_CHECK_ITEM_EXISTS);
                 PreparedStatement updateStmt = ct.prepareStatement(SQL_DECREMENT_ITEM);
                 PreparedStatement deleteStmt = ct.prepareStatement(SQL_DELETE_ITEM)) {

                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, productId);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        int currentAmount = rs.getInt("productAmount");

                        if (currentAmount > 1) {
                            // 减少数量
                            updateStmt.setInt(1, userId);
                            updateStmt.setInt(2, productId);

                            int updated = updateStmt.executeUpdate();
                            if (updated == 1) {
                                logger.info("减少购物车商品数量: 用户ID=" + userId +
                                        ", 商品ID=" + productId);
                                return true;
                            }
                        } else {
                            // 删除商品项
                            deleteStmt.setInt(1, userId);
                            deleteStmt.setInt(2, productId);

                            int deleted = deleteStmt.executeUpdate();
                            if (deleted == 1) {
                                logger.info("删除购物车商品(数量为1): 用户ID=" + userId +
                                        ", 商品ID=" + productId);
                                return true;
                            }
                        }
                    }
                }
            }
            logger.warning("减少购物车商品失败: 未找到商品 用户ID=" + userId + ", 商品ID=" + productId);
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "减少购物车商品失败", e);
            throw new PersistenceException("购物车商品更新失败", e);
        }
    }

    // 增加购物车商品数量（带库存检查）
    public boolean addOneProduct(int userId, int productId) {
        try (Connection ct = ConnectionPool.getConnection();
             // 检查库存
             PreparedStatement stockStmt = ct.prepareStatement(SQL_GET_PRODUCT_STOCK);
             // 增加数量
             PreparedStatement updateStmt = ct.prepareStatement(SQL_INCREMENT_ITEM)) {

            // 1. 检查商品库存
            stockStmt.setInt(1, productId);
            try (ResultSet rs = stockStmt.executeQuery()) {
                if (rs.next()) {
                    int stock = rs.getInt("stock");
                    if (stock < 1) {
                        logger.warning("库存不足: 商品ID=" + productId);
                        return false;
                    }
                } else {
                    logger.warning("商品不存在: ID=" + productId);
                    return false;
                }
            }

            // 2. 增加购物车商品数量
            updateStmt.setInt(1, userId);
            updateStmt.setInt(2, productId);

            int updated = updateStmt.executeUpdate();
            if (updated == 1) {
                logger.info("增加购物车商品数量: 用户ID=" + userId + ", 商品ID=" + productId);
                return true;
            }
            logger.warning("增加购物车商品失败: 未找到商品 用户ID=" + userId + ", 商品ID=" + productId);
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "增加购物车商品失败", e);
            throw new PersistenceException("购物车商品更新失败", e);
        }
    }

    // 清空用户购物车（结算后调用）
    public boolean deleteOneOrderFromCar(int userId) {
        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(SQL_DELETE_USER_CART)) {

            pstmt.setInt(1, userId);
            int deleted = pstmt.executeUpdate();

            if (deleted > 0) {
                logger.info("清空购物车: 用户ID=" + userId + ", 删除项数=" + deleted);
                return true;
            }
            logger.warning("清空购物车失败: 用户ID=" + userId + " 购物车为空");
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "清空购物车失败", e);
            throw new PersistenceException("购物车清空失败", e);
        }
    }

    // 获取用户购物车内容
    public ArrayList<ShoppingCar> showShoppingCar(int userId) {
        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(SQL_GET_CART_ITEMS)) {

            pstmt.setInt(1, userId);

            return executeCartQuery(pstmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "获取购物车失败: 用户ID=" + userId, e);
            throw new PersistenceException("购物车查询失败", e);
        }
    }

    // 执行购物车查询的通用方法
    private ArrayList<ShoppingCar> executeCartQuery(PreparedStatement pstmt) throws SQLException {
        ArrayList<ShoppingCar> cartItems = new ArrayList<>();
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ShoppingCar item = new ShoppingCar();
                item.setId(rs.getInt("id"));
                item.setProductId(rs.getInt("productId"));
                item.setProductAmount(rs.getInt("productAmount"));
                cartItems.add(item);
            }
        }
        return cartItems;
    }

    // 自定义异常类
    public static class PersistenceException extends RuntimeException {
        public PersistenceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}