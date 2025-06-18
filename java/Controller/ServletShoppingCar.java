package Controller;

import Logic.ShoppingCarWatcher;
import Model.User;
import Model.secondary.ShoppingCarProduct;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/servletShoppingCar")
public class ServletShoppingCar extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        HttpSession session = request.getSession();

        // 从session获取查看购物车的userId
        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        // 通过userId返回该用户购物车中的所有 商品数量 + 商品具体信息
        ShoppingCarWatcher shoppingCarWatcher = new ShoppingCarWatcher();
        ArrayList<ShoppingCarProduct> shoppingCarProductList = shoppingCarWatcher.showShoppingCar(userId);
        request.setAttribute("shoppingCarProductList", shoppingCarProductList);
        request.getRequestDispatcher("ShoppingCar.jsp").forward(request, response);
    }
}
