package controller;

import dao.BookingDAO;
import dao.EventDAO;
import dao.EventDAO.EventDTO;
import dao.EventDAO.TicketTypeDTO;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * Menangani dua hal:
 *   GET  /booking?eventId=1          → tampilkan form booking
 *   POST /booking                     → proses booking & payment
 */
@WebServlet("/booking")
public class BookingServlet extends HttpServlet {

    private final EventDAO   eventDAO   = new EventDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    // ── GET: tampilkan form booking ───────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idParam = request.getParameter("eventId");
        if (idParam == null) { response.sendRedirect("events"); return; }

        int eventId = Integer.parseInt(idParam);
        EventDTO            event       = eventDAO.getEventById(eventId);
        List<TicketTypeDTO> ticketTypes = eventDAO.getTicketTypes(eventId);

        if (event == null) { response.sendRedirect("events"); return; }

        request.setAttribute("event",       event);
        request.setAttribute("ticketTypes", ticketTypes);
        request.getRequestDispatcher("booking.jsp").forward(request, response);
    }

    // ── POST: simpan booking ke DB ────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        try {
            int    eventId      = Integer.parseInt(request.getParameter("eventId"));
            int    ticketTypeId = Integer.parseInt(request.getParameter("ticketTypeId"));
            int    quantity     = Integer.parseInt(request.getParameter("quantity"));
            double totalPrice   = Double.parseDouble(request.getParameter("totalPrice"));
            String paymentMethod = request.getParameter("paymentMethod"); // credit_card / e_wallet / bank_transfer

            // Validasi kuota
            List<TicketTypeDTO> types = eventDAO.getTicketTypes(eventId);
            TicketTypeDTO chosen = types.stream()
                    .filter(t -> t.id == ticketTypeId)
                    .findFirst().orElse(null);

            if (chosen == null || chosen.available < quantity) {
                request.setAttribute("error", "Maaf, tiket tidak tersedia atau kuota tidak cukup.");
                request.setAttribute("event", eventDAO.getEventById(eventId));
                request.setAttribute("ticketTypes", types);
                request.getRequestDispatcher("booking.jsp").forward(request, response);
                return;
            }

            // Buat booking
            int bookingId = bookingDAO.createBooking(userId, eventId, ticketTypeId,
                                                     quantity, totalPrice, paymentMethod);

            if (bookingId < 0) {
                request.setAttribute("error", "Terjadi kesalahan saat memproses booking. Coba lagi.");
                request.getRequestDispatcher("booking.jsp").forward(request, response);
                return;
            }

            // Redirect ke halaman sukses
            response.sendRedirect("success.jsp?bookingId=" + bookingId);

        } catch (NumberFormatException e) {
            response.sendRedirect("events");
        }
    }
}