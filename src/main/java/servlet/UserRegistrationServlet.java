package servlet;

import dto.UserDto;
import dto.UserRegistrationDto;
import entity.Role;
import exceptions.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.UserServiceImpl;
import services.interfaces.UserService;

import java.io.IOException;

@WebServlet("/registration")
public class UserRegistrationServlet extends HttpServlet {

    private UserService userService = UserServiceImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("roles", Role.values());
        req.getRequestDispatcher("WEB-INF/jsp/userRegistration.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var name = req.getParameter("name");
        var email = req.getParameter("email");
        var password = req.getParameter("password");
        var role = req.getParameter("role");
        var createUserDto = new UserRegistrationDto(name, email, password, role);
        try {
            Long id = userService.save(createUserDto);
            req.getSession().setAttribute("user", new UserDto(id, name, email, role));
            resp.sendRedirect("movies");
        }catch (ValidationException e){
            req.setAttribute("errors", e.getErrors());
            req.getRequestDispatcher("WEB-INF/jsp/userRegistration.jsp").forward(req, resp);
        }
    }
}
