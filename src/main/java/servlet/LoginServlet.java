package servlet;

import dto.UserDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.UserServiceImpl;
import services.interfaces.UserService;


import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserService userService = UserServiceImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("WEB-INF/jsp/userLogin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException  {
        var userDtoOptional = userService.login(req.getParameter("email"), req.getParameter("password"));
        if (userDtoOptional.isPresent()){
            onLoginSuccess(userDtoOptional.get(), req, resp);
        }else {
            onLoginFail(req, resp);
        }
    }

    private void onLoginSuccess(UserDto userDto, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.getSession().setAttribute("user", userDto);
        resp.sendRedirect("movies");
    }

    private void onLoginFail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect("login?error&email=" + req.getParameter("email"));
    }
}
