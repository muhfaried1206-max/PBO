package controller;

import dao.UserDAO;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name     = request.getParameter("name");
        String email    = request.getParameter("email");
        String password = request.getParameter("password");

        // Validasi input
        if (name == null || name.trim().isEmpty()
         || email == null || email.trim().isEmpty()
         || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Semua field wajib diisi.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        if (password.length() < 6) {
            request.setAttribute("error", "Password minimal 6 karakter.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // Daftarkan ke DB
        boolean success = userDAO.register(name.trim(), email.trim(), password.trim());

        if (!success) {
            request.setAttribute("error", "Email sudah terdaftar. Silakan login.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // Registrasi berhasil → redirect ke login dengan pesan sukses
        response.sendRedirect("login.jsp?registered=1");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("register.jsp");
    }
}