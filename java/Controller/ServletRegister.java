package Controller;

import Model.User;
import Persistence.PersUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/servletRegister")
public class ServletRegister extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=GB2312");

        // 从前端获取注册信息
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String idNumber = request.getParameter("idNumber");
        String realname = request.getParameter("realname");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        // 在数据库中通过idNumber确定是否已被注册
        PersUser persUser = new PersUser();

        if (!persUser.isAllowToRegister(idNumber)) {
            // 检查出来发现不能注册 转到注册页面
            System.out.println("persUser is not allow to register idNumber");
            request.getRequestDispatcher("servletLogin").forward(request, response);
        }

        // 创建用户对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setIdNumber(idNumber);
        user.setRealname(realname);
        user.setPhone(phone);
        user.setAddress(address);

        // 在数据库中注册用户
        if(persUser.addUser(user)){
            System.out.println("-------------------- Register Successfully --------------------");
        }
        request.getRequestDispatcher("servletLogin").forward(request, response);
    }
}
