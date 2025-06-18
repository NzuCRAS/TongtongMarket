package Controller;

import Logic.ProductSearcher;
import Model.Product;
import Persistence.PersProduct;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/servletProductSearch")
public class ServletProductSearch extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        // 从前端获取当前页面的筛选规则  flag -> 排序特征(1无 2销量 3价格)(均降序) + 无要求 / 商品名 / 商品类型
        String name = (request.getParameter("name") == null) ? "noName" : request.getParameter("name");
        System.out.println("name:|" + name + "|end");

        // 搜索商品并生成商品数组
        PersProduct persProduct = new PersProduct();
        ArrayList<Product> productList = persProduct.getProductsFromProductname(name, 1);
        System.out.println("productListSize: " + productList.size());

        // 返回商品数组
        request.setAttribute("name", name);
        request.setAttribute("productList", productList);
        request.setAttribute("size", productList.size());
        request.getRequestDispatcher("Search.jsp").forward(request, response);
    }
}
