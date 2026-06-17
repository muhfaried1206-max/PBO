package controller;

import dao.EventDAO;
import dao.EventDAO.EventDTO;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * Admin CRUD event.
 *   GET  /admin/events             → list semua event
 *   GET  /admin/events?action=new  → form tambah
 *   GET  /admin/events?action=edit&id=1  → form edit
 *   POST /admin/events             → create / update / delete (param: action)
 */
@WebServlet("/admin/events")
public class AdminEventServlet extends HttpServlet {

    private final EventDAO eventDAO = new EventDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Hanya admin yang boleh masuk
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("new".equals(action)) {
            request.getRequestDispatcher("/admin-event-form.jsp").forward(request, response);
            return;
        }

        if ("edit".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            EventDTO event = eventDAO.getEventById(id);
            request.setAttribute("event", event);
            request.setAttribute("ticketTypes", eventDAO.getTicketTypes(id));
            request.getRequestDispatcher("/admin-event-form.jsp").forward(request, response);
            return;
        }

        // default: list
        List<EventDTO> events = eventDAO.getAllEvents();
        request.setAttribute("events", events);
        request.getRequestDispatcher("/admin-dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            eventDAO.deleteEvent(id);
            response.sendRedirect(request.getContextPath() + "/admin/events?deleted=1");
            return;
        }

        // Ambil field umum
        String title       = request.getParameter("title");
        String description = request.getParameter("description");
        String date        = request.getParameter("date");
        String time        = request.getParameter("time");
        String location    = request.getParameter("location");
        int    capacity    = Integer.parseInt(request.getParameter("capacity"));
        String category    = request.getParameter("category");
        String imageUrl    = request.getParameter("imageUrl");

        HttpSession session = request.getSession(false);
        int adminId = (int) session.getAttribute("userId");

        if ("create".equals(action)) {
            int newId = eventDAO.createEvent(title, description, date, time,
                                             location, capacity, category, imageUrl, adminId);
            // Tambah ticket types jika ada
            addTicketTypesFromRequest(request, newId);
            response.sendRedirect(request.getContextPath() + "/admin/events?created=1");

        } else if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            eventDAO.updateEvent(id, title, description, date, time,
                                 location, capacity, category, imageUrl);
            response.sendRedirect(request.getContextPath() + "/admin/events?updated=1");
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null
            && "admin".equals(session.getAttribute("userRole"));
    }

    /**
     * Membaca parameter ticketTypeName[], ticketTypePrice[], ticketTypeQuota[]
     * dari form dan menyimpannya ke DB.
     */
    private void addTicketTypesFromRequest(HttpServletRequest request, int eventId) {
        String[] names  = request.getParameterValues("ticketTypeName");
        String[] prices = request.getParameterValues("ticketTypePrice");
        String[] quotas = request.getParameterValues("ticketTypeQuota");

        if (names == null) return;
        for (int i = 0; i < names.length; i++) {
            if (!names[i].isEmpty()) {
                eventDAO.addTicketType(eventId, names[i],
                        Double.parseDouble(prices[i]),
                        Integer.parseInt(quotas[i]));
            }
        }
    }
}