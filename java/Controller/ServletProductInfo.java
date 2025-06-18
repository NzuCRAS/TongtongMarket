package Controller;

import Model.Product;
import Persistence.PersProduct;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

//接收参数id，然后再找出对应id的具体商品对象，再传给ProductInfo.jsp
@WebServlet("/servletProductInfo")
public class ServletProductInfo extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        request.setCharacterEncoding("utf-8");

        // 从前端获取商品id
        int productId= Integer.parseInt(request.getParameter("id"));

        // 通过商品id获取商品信息并返回
        PersProduct persProduct = new PersProduct();
        Product product = persProduct.getProduct(productId);
        System.out.println("productId: " + productId);
        request.setAttribute("product", product);
        System.out.println("productId: " + productId);

        // 前往商品详细信息页面
        request.getRequestDispatcher("ProductInfo.jsp").forward(request,response);
    }
}
