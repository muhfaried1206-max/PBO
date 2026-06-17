package controller;

import dao.BookingDAO;
import dao.BookingDAO.BookingDTO;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** URL: /my-tickets */
@WebServlet("/my-tickets")
public class MyTicketServlet extends HttpServlet {

    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        List<BookingDTO> tickets = bookingDAO.getTicketsByUser(userId);

        request.setAttribute("tickets", tickets);
        request.getRequestDispatcher("myticket.jsp").forward(request, response);
    }
}