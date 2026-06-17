package controller;

import dao.EventDAO;
import dao.EventDAO.EventDTO;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * Servlet yang mengambil daftar event dari DB lalu forward ke dashboard.jsp.
 * URL: /events  atau  /dashboard  (sesuaikan di web.xml / annotation)
 */
@WebServlet("/events")
public class EventListServlet extends HttpServlet {

    private final EventDAO eventDAO = new EventDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Cek session – jika belum login, redirect ke login
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        List<EventDTO> events = eventDAO.getAllEvents();
        request.setAttribute("events", events);
        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
}