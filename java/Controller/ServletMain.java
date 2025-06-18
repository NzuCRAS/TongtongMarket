package Controller;

import Logic.ProductSearcher;
import Model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/servletMain")
public class ServletMain extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        HttpSession session = request.getSession();

        // 从前端获取当前页面的筛选规则  flag -> 排序特征(1无 2销量 3价格)(均降序) + 无要求 / 商品名 / 商品类型
        // 如果前端的请求不包括该筛选规则 到session里面寻找之前的内容
        int page = (request.getParameter("page") == null) ? 1 : Integer.parseInt(request.getParameter("page"));
        int flag = (request.getParameter("flag") == null) ? (int) session.getAttribute("flag") : Integer.parseInt(request.getParameter("flag"));
        String type = (request.getParameter("type") == null) ? (String) session.getAttribute("type") : (String) request.getParameter("type");

        // 将当前的筛选规则放到session里 给前端使用
        session.setAttribute("flag", flag);
        session.setAttribute("type", type);

        // 搜索商品并生成商品数组
        ProductSearcher productSearcher = new ProductSearcher();
        ArrayList<Product> productList = productSearcher.searchProduct(flag, "noName", type, page);

        // 返回商品数组
        System.out.println("productListSize: " + productList.size());
        request.setAttribute("productList", productList);
        request.setAttribute("page", page);
        request.getRequestDispatcher("Main.jsp").forward(request, response);
    }
}
