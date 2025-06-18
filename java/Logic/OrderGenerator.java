package Logic;

import Model.Order;
import Model.OrderInfo;
import Model.secondary.ShoppingCarProduct;
import Persistence.PersOrder;
import Persistence.PersOrderInfo;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderGenerator {

    // 创建订单号 14位日期 + 10位随机字符
    public static String generateOrderId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dataStr = sdf.format(new Date());

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }

        return dataStr + sb.toString();
    }

    // 通过购物车信息创建订单
    public void showOrder(int userId) throws SQLException {


        // 首先获取该用户的购物车的所有 商品数量 + 商品信息
        ShoppingCarWatcher shoppingCarWatcher = new ShoppingCarWatcher();
        ArrayList<ShoppingCarProduct> shoppingCarProductList = shoppingCarWatcher.showShoppingCar(userId);

        // 创建订单
        double sumPrice = 0;
        String orderId = OrderGenerator.generateOrderId();

        ArrayList<OrderInfo> orderInfoList = new ArrayList<>();
        for (ShoppingCarProduct shoppingCarProduct : shoppingCarProductList) {
            // 创建订单详情
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setOrderId(orderId);
            orderInfo.setProductId(shoppingCarProduct.getProduct().getProductId());
            orderInfo.setPrice(shoppingCarProduct.getProduct().getPrice());
            orderInfo.setProductName(shoppingCarProduct.getProduct().getProductName());
            orderInfo.setProductAmount(shoppingCarProduct.getNumber());

            // 将订单详情加入arrayList
            orderInfoList.add(orderInfo);
        }

        PersOrder persOrder = new PersOrder();
        persOrder.generateOrder(orderInfoList, userId, orderId);

        return;
    }
}
