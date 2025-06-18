package Controller;

import Logic.OrderGenerator;
import Model.User;
import Persistence.PersOrder;
import Persistence.PersShoppingCar;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/servletOrderGenerate")
public class ServletOrderGenerate extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        // 从session获取userid,直接找到对应的购物车
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        // 将购物车中所有商品放入订单,生成一份新订单
        PersOrder persOrder = new PersOrder();
        OrderGenerator orderGenerator = new OrderGenerator();
        try {
            orderGenerator.showOrder(userId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // 清空购物车
        PersShoppingCar persShoppingCar = new PersShoppingCar();
        persShoppingCar.deleteOneOrderFromCar(userId);

        // 跳转到main页面的servlet
        System.out.println("---------------- Order Generated ---------------------");
        session.setAttribute("flag", 1);
        session.setAttribute("type", "全部");
        request.getRequestDispatcher("servletMain").forward(request, response);
    }
}
