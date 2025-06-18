package Controller;

import Model.OrderInfo;
import Persistence.PersOrder;
import Persistence.PersOrderInfo;
import Persistence.PersProduct;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/servletPayOrder")
public class ServletPayOrder extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        HttpSession session = request.getSession();

        // 从前端获取准备支付的订单id
        String orderID = request.getParameter("orderID");
        System.out.println("showOrder --------------------- " + orderID);

        /*
        *
                 支付页面
        *
        */

        // 从数据库中查询订单中包含的商品信息 + 数量
        PersOrderInfo persOrderInfo = new PersOrderInfo();
        PersProduct persProduct = new PersProduct();
        PersOrder persOrder = new PersOrder();
        // 对相应的商品进行卖出 (更新数据库中销量+库存)
        ArrayList<OrderInfo> orderInfoList = persOrderInfo.showOrder(orderID);

        boolean canPay = true;
        if (!persOrder.checkIsPaid(orderID)) {
            System.out.println(" PAID | " + orderID);
        } else {
            canPay = false;
            System.out.println(" PAID FAILED | " + orderID);
        }

        for (OrderInfo orderInfo : orderInfoList) {
            if(!persProduct.checkStock(orderInfo.getProductId(), orderInfo.getProductAmount())) {
            System.out.println("NO STOCK | "+ orderInfo.getProductName());
                canPay = false;
                break;
            }
        }

        if(canPay){
            for (OrderInfo orderInfo : orderInfoList) {
                if (persProduct.sold(orderInfo.getProductId(), orderInfo.getProductAmount())) {
                    System.out.println("name|" + orderInfo.getProductName());
                }
            }
            persOrder.settlement(orderID);
            System.out.println("------------------------- OrderPayed Successfully ------------------------");

        }
        session.setAttribute("flag", 1);
        session.setAttribute("type", "全部");
        request.getRequestDispatcher("servletMain").forward(request, response);
    }
}
