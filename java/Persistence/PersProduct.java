package Persistence;

import Model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersProduct {
    private static final Logger logger = Logger.getLogger(PersProduct.class.getName());

    // 集中管理SQL语句
    private static final String SQL_GET_ALL_PRODUCTS =
            "SELECT * FROM ProductInfo ";
    private static final String SQL_GET_BY_NAME =
            "SELECT * FROM ProductInfo WHERE productName LIKE ?  ";
    private static final String SQL_GET_BY_CATEGORY =
            "SELECT * FROM ProductInfo WHERE sortId = ? ";


    private static final String SQL_UPDATE_PRICE =
            "UPDATE ProductInfo SET price = ? WHERE productId = ?";
    private static final String SQL_DELETE_PRODUCT =
            "DELETE FROM ProductInfo  WHERE productId = ?"; // 硬删除
    private static final String SQL_ADD_PRODUCT =
            "INSERT INTO ProductInfo(productId, productName, price, introduction, picture, stock, sortId, sales) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_SOLD_PRODUCT =
            "UPDATE ProductInfo SET stock = stock - ?, sales = sales + ? WHERE productId = ?";
    private static final String SQL_UPDATE_STOCK =
            "UPDATE ProductInfo SET stock = stock + ? WHERE productId = ?";
    private static final String SQL_GET_PRODUCT =
            "SELECT * FROM ProductInfo WHERE productId = ?";
    private static final String SQL_CHECK_STOCK =
            "SELECT stock FROM ProductInfo WHERE productId = ?";

    // 订单方法常量
    private static final String ORDER_BY_SALES = "ORDER BY sales DESC";
    private static final String ORDER_BY_PRICE = "ORDER BY price DESC";

    // 获取排序子句
    private String getOrderClause(int flag) {
        switch (flag) {
            case 2: return ORDER_BY_SALES;
            case 3: return ORDER_BY_PRICE;
            default: return "";
        }
    }

    // 查找所有商品（带分页和排序）
    public ArrayList<Product> getProductsAll(int page, int flag) {
        String sql = SQL_GET_ALL_PRODUCTS + " " + getOrderClause(flag) + " LIMIT ?, ?";

        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(sql)) {

            pstmt.setInt(1, (page - 1) * 14);
            pstmt.setInt(2, 14);

            return executeProductQuery(pstmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "获取所有商品失败", e);
            throw new PersistenceException("查询商品失败", e);
        }
    }

    // 商品名检索
    public ArrayList<Product> getProductsFromProductname(String productname, int page, int flag) {
        String sql = SQL_GET_BY_NAME + " " + getOrderClause(flag) +  " LIMIT ?, ?";

        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(sql)) {

            pstmt.setString(1, "%" + productname + "%");
            pstmt.setInt(2, (page - 1) * 14);
            pstmt.setInt(3, 14);

            return executeProductQuery(pstmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "按名称查询商品失败: " + productname, e);
            throw new PersistenceException("商品名称查询失败", e);
        }
    }

    // 商品名检索(没有分页)
    public ArrayList<Product> getProductsFromProductname(String productname,int flag) {
        String sql = SQL_GET_BY_NAME + " " + getOrderClause(flag);

        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(sql)) {

            pstmt.setString(1, "%" + productname + "%");

            return executeProductQuery(pstmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "按名称查询商品失败: " + productname, e);
            throw new PersistenceException("商品名称查询失败", e);
        }
    }

    // 商品类型检索
    public ArrayList<Product> getProductsFromSortName(String sortName, int page, int flag) {
        String sql = SQL_GET_BY_CATEGORY + " " + getOrderClause(flag) +  " LIMIT ?, ?";

        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(sql)) {

            int sortId = new PersProductType().getSortId(sortName);
            if (sortId < 0) {
                logger.warning("无效的商品类别: " + sortName);
                return new ArrayList<>();
            }

            pstmt.setInt(1, sortId);
            pstmt.setInt(2, (page - 1) * 14);
            pstmt.setInt(3, 14);

            return executeProductQuery(pstmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "按类别查询商品失败: " + sortName, e);
            throw new PersistenceException("商品类别查询失败", e);
        }
    }

    //将检索结果写入链表
    private ArrayList<Product> executeProductQuery(PreparedStatement pstmt) throws SQLException {
        ArrayList<Product> products = new ArrayList<>();
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }

    // 插入链表时，对链表对象设值
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("productId"));
        product.setProductName(rs.getString("productName"));
        product.setPrice(rs.getDouble("price"));
        product.setIntroduction(rs.getString("introduction"));
        product.setPicture(rs.getString("picture"));
        product.setStock(rs.getInt("stock"));
        product.setSortId(rs.getInt("sortId"));
        product.setSales(rs.getInt("sales"));
        return product;
    }

    // 修改商品价格
    public boolean updatePrice(int productId, double price) {
        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(SQL_UPDATE_PRICE)) {

            pstmt.setDouble(1, price);
            pstmt.setInt(2, productId);

            int updated = pstmt.executeUpdate();
            if (updated == 1) {
                logger.info("商品价格更新成功: ID=" + productId + ", 新价格=" + price);
                return true;
            }
            logger.warning("商品价格更新失败: 未找到商品 ID=" + productId);
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "更新商品价格失败: ID=" + productId, e);
            throw new PersistenceException("价格更新失败", e);
        }
    }

    // 下架商品（软删除）
    public boolean deleteProduct(int productId) {
        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(SQL_DELETE_PRODUCT)) {

            pstmt.setInt(1, productId);
            int updated = pstmt.executeUpdate();

            if (updated == 1) {
                logger.info("商品下架成功: ID=" + productId);
                return true;
            }
            logger.warning("商品下架失败: 未找到商品 ID=" + productId);
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "下架商品失败: ID=" + productId, e);
            throw new PersistenceException("商品下架失败", e);
        }
    }

    // 上架商品
    public boolean addProduct(int productId, String productName, double price,
                              String introduction, String picture, int stock, int sortId) {
        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(SQL_ADD_PRODUCT)) {

            pstmt.setInt(1, productId);
            pstmt.setString(2, sanitizeInput(productName)); // 输入净化
            pstmt.setDouble(3, price);
            pstmt.setString(4, sanitizeInput(introduction));
            pstmt.setString(5, picture);
            pstmt.setInt(6, stock);
            pstmt.setInt(7, sortId);
            pstmt.setInt(8, 0); // 初始销量

            int inserted = pstmt.executeUpdate();
            if (inserted == 1) {
                logger.info("商品上架成功: ID=" + productId + ", 名称=" + productName);
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "上架商品失败: ID=" + productId, e);
            throw new PersistenceException("商品上架失败", e);
        }
    }

    // 输入净化（防止XSS）
    private String sanitizeInput(String input) {
        if (input == null) return null;
        return input.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#39;");
    }
    //检查库存
    public boolean checkStock(int productId, int number){
        Connection ct = null;
        try {
            ct=ConnectionPool.getConnection();

            // 检查库存是否充足
            try (PreparedStatement checkStmt = ct.prepareStatement(SQL_CHECK_STOCK)) {
                checkStmt.setInt(1, productId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        int currentStock = rs.getInt("stock");
                        if (currentStock < number) {
                            logger.warning("库存不足: 商品ID=" + productId +
                                    " 请求数量=" + number +
                                    " 当前库存=" + currentStock);
                            return false;
                        }
                    } else {
                        logger.warning("商品不存在: ID=" + productId);
                        return false;
                    }
                }
                return true;
            }
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "库存检查失败: productID=" + productId, e);
            throw new PersistenceException("检查库存失败", e);
        }
    }

    // 商品售出（带事务和库存检查）
    public boolean sold(int productId, int number) {
        Connection ct = null;
        try {
            ct = ConnectionPool.getConnection();
            // 更新库存和销量（原子操作）
            try(PreparedStatement checkStmt = ct.prepareStatement(SQL_CHECK_STOCK)) {
                checkStmt.setInt(1, productId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        int currentStock = rs.getInt("stock");
                        if (currentStock >= number) {
                            try (PreparedStatement updateStmt = ct.prepareStatement(SQL_SOLD_PRODUCT)) {
                                updateStmt.setInt(1, number);
                                updateStmt.setInt(2, number);
                                updateStmt.setInt(3, productId);

                                int updated = updateStmt.executeUpdate();
                                if (updated == 1) {
                                    logger.info("商品售出成功: ID=" + productId + " 数量=" + number);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            logger.warning("商品售出失败: ID=" + productId);
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "售出更新失败: productID=" + productId, e);
            throw new PersistenceException("售出更新失败", e);
        }
    }

    // 补充库存
    public boolean updateStock(int productId, int stock) {
        if (stock <= 0) {
            logger.warning("无效的库存补充数量: " + stock);
            return false;
        }

        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(SQL_UPDATE_STOCK)) {

            pstmt.setInt(1, stock);
            pstmt.setInt(2, productId);

            int updated = pstmt.executeUpdate();
            if (updated == 1) {
                logger.info("库存补充成功: 商品ID=" + productId + " 数量=" + stock);
                return true;
            }
            logger.warning("库存补充失败: 未找到商品 ID=" + productId);
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "库存补充失败: ID=" + productId, e);
            throw new PersistenceException("库存更新失败", e);
        }
    }

    // 获取单个商品信息
    public Product getProduct(int productId) {
        try (Connection ct = ConnectionPool.getConnection();
             PreparedStatement pstmt = ct.prepareStatement(SQL_GET_PRODUCT)) {

            pstmt.setInt(1, productId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
                logger.warning("未找到商品: ID=" + productId);
                return null;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "获取商品信息失败: ID=" + productId, e);
            throw new PersistenceException("商品查询失败", e);
        }
    }

    // 自定义异常类
    public static class PersistenceException extends RuntimeException {
        public PersistenceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}