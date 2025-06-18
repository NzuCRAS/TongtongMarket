package Controller;

import Model.User;
import Persistence.PersUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/servletLogin")
public class ServletLogin extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        PersUser persuser = new PersUser();
        HttpSession session = request.getSession();
        User user = new User();
        if (request.getSession().getAttribute("user") != null) {
            user = (User) request.getSession().getAttribute("user");
        }

        user = persuser.checkUser(username, password);
        if (user == null) {
            System.out.println("User not Register");
            request.getRequestDispatcher("LoginRegister.jsp").forward(request, response);
        } else {
            System.out.println("Login Successful");
            session.setAttribute("user", user);
            session.setAttribute("flag", 1);
            session.setAttribute("type", "全部");
            request.getRequestDispatcher("servletMain").forward(request, response);
        }
    }
}
