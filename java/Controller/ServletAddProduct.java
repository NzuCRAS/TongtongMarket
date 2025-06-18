package Controller;

import Model.ShoppingCar;
import Model.User;
import Persistence.PersShoppingCar;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/servletAddProduct")
public class ServletAddProduct extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // 从前端获取添加 n 个商品id为 productId 的商品
        int productId = Integer.parseInt(request.getParameter("productId"));
        int num = Integer.parseInt(request.getParameter("num"));


        // 将amount个该商品放入购物车
        PersShoppingCar persShoppingCar = new PersShoppingCar();
        persShoppingCar.addProduct(user.getId(), productId, num);
        request.getRequestDispatcher("servletMain").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}