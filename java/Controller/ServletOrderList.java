package Controller;

import Model.Order;
import Model.secondary.OrderDetail;
import Model.OrderInfo;
import Model.User;
import Persistence.PersOrder;
import Persistence.PersOrderInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/servletOrderList")
public class ServletOrderList extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        // 获取用户信息
        User user = (User)session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        // 通过用户id查找订单
        PersOrder order = new PersOrder();
        PersOrderInfo orderInfo = new PersOrderInfo();
        ArrayList<Order> orderList =  order.getOrdersFromId(user.getId());

        // 通过订单号查询订单信息,并且将Order和OrderInfo包装成订单详情OrderDetail
        ArrayList<OrderDetail> orderDetailList = new ArrayList<>();
        for (Order orderX : orderList) {
            ArrayList<OrderInfo> orderInfoList = orderInfo.showOrder(orderX.getOrderId());
            orderDetailList.add(new OrderDetail(orderX,orderInfoList));
        }

        // 不进行分页,直接传输给前端
        request.setAttribute("orderDetailList", orderDetailList);
        request.getRequestDispatcher("OrderCenter.jsp").forward(request, response);
    }
}
