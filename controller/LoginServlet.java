package controller;

import dao.UserDAO;
import dao.UserDAO.UserDTO;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email    = request.getParameter("email");
        String password = request.getParameter("password");

        // Validasi input kosong
        if (email == null || email.trim().isEmpty()
         || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Email dan password wajib diisi.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // Cek ke database
        UserDTO user = userDAO.login(email.trim(), password.trim());

        if (user == null) {
            // Login gagal
            request.setAttribute("error", "Email atau password salah.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // Login berhasil → simpan ke session
        HttpSession session = request.getSession(true);
        session.setAttribute("userId",    user.id);
        session.setAttribute("userName",  user.name);
        session.setAttribute("userEmail", user.email);
        session.setAttribute("userRole",  user.role);
        session.setMaxInactiveInterval(60 * 60); // 1 jam

        // Redirect berdasarkan role
        if ("admin".equals(user.role)) {
            response.sendRedirect("admin-dashboard.jsp");
        } else {
            response.sendRedirect("dashboard.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }
}