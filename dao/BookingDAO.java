package dao;

import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO untuk tabel bookings dan tickets.
 */
public class BookingDAO {

    // ── Inner DTO ─────────────────────────────────────────────────────
    public static class BookingDTO {
        public int    id;
        public int    userId;
        public int    eventId;
        public int    ticketTypeId;
        public int    quantity;
        public double totalPrice;
        public String paymentMethod;
        public String paymentStatus;
        public String bookedAt;
        // join fields
        public String eventTitle;
        public String eventDate;
        public String eventTime;
        public String eventLocation;
        public String typeName;
        public String ticketCode;
        public String ticketStatus;
        public String eventImageUrl;
    }

    // ─────────────────────────────────────────────────────────────────
    //  CREATE BOOKING
    // ─────────────────────────────────────────────────────────────────

    /**
     * Membuat booking dan (jika payment berhasil) menerbitkan tiket.
     * @return booking ID, atau -1 jika gagal.
     */
    public int createBooking(int userId, int eventId, int ticketTypeId,
                             int quantity, double totalPrice,
                             String paymentMethod) {

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);   // mulai transaksi

            // 1. Insert ke bookings
            String sqlBooking = "INSERT INTO bookings "
                    + "(user_id, event_id, ticket_type_id, quantity, total_price, payment_method, payment_status) "
                    + "VALUES (?,?,?,?,?,?,'paid')";   // langsung 'paid' (simulasi)

            int bookingId;
            try (PreparedStatement ps = conn.prepareStatement(sqlBooking, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt   (1, userId);
                ps.setInt   (2, eventId);
                ps.setInt   (3, ticketTypeId);
                ps.setInt   (4, quantity);
                ps.setDouble(5, totalPrice);
                ps.setString(6, paymentMethod);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) { conn.rollback(); return -1; }
                    bookingId = keys.getInt(1);
                }
            }

            // 2. Generate tiket (satu tiket per quantity)
            String sqlTicket = "INSERT INTO tickets (booking_id, ticket_code) VALUES (?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlTicket)) {
                for (int i = 0; i < quantity; i++) {
                    String code = generateTicketCode();
                    ps.setInt   (1, bookingId);
                    ps.setString(2, code);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return bookingId;

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
            return -1;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close();              } catch (SQLException ignored) {}
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  READ
    // ─────────────────────────────────────────────────────────────────

    /** Semua tiket milik satu user (untuk halaman My Tickets). */
    public List<BookingDTO> getTicketsByUser(int userId) {
        List<BookingDTO> list = new ArrayList<>();
        String sql =
            "SELECT b.*, "
          + "  e.title AS event_title, e.date AS event_date, "
          + "  e.time AS event_time,   e.location AS event_location, "
          + "  e.image_url AS event_image_url, "
          + "  tt.type_name, "
          + "  t.ticket_code, t.status AS ticket_status "
          + "FROM bookings b "
          + "JOIN events       e  ON e.id  = b.event_id "
          + "JOIN ticket_types tt ON tt.id = b.ticket_type_id "
          + "JOIN tickets      t  ON t.booking_id = b.id "
          + "WHERE b.user_id = ? "
          + "ORDER BY b.booked_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapBooking(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Satu booking berdasarkan ID (untuk halaman sukses). */
    public BookingDTO getBookingById(int bookingId) {
        String sql =
            "SELECT b.*, "
          + "  e.title AS event_title, e.date AS event_date, "
          + "  e.time AS event_time,   e.location AS event_location, "
          + "  e.image_url AS event_image_url, "
          + "  tt.type_name, "
          + "  t.ticket_code, t.status AS ticket_status "
          + "FROM bookings b "
          + "JOIN events       e  ON e.id  = b.event_id "
          + "JOIN ticket_types tt ON tt.id = b.ticket_type_id "
          + "JOIN tickets      t  ON t.booking_id = b.id "
          + "WHERE b.id = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapBooking(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────────
    //  HELPER
    // ─────────────────────────────────────────────────────────────────

    private BookingDTO mapBooking(ResultSet rs) throws SQLException {
        BookingDTO b = new BookingDTO();
        b.id            = rs.getInt("id");
        b.userId        = rs.getInt("user_id");
        b.eventId       = rs.getInt("event_id");
        b.ticketTypeId  = rs.getInt("ticket_type_id");
        b.quantity      = rs.getInt("quantity");
        b.totalPrice    = rs.getDouble("total_price");
        b.paymentMethod = rs.getString("payment_method");
        b.paymentStatus = rs.getString("payment_status");
        b.bookedAt      = rs.getString("booked_at");
        b.eventTitle    = rs.getString("event_title");
        b.eventDate     = rs.getString("event_date");
        b.eventTime     = rs.getString("event_time");
        b.eventLocation = rs.getString("event_location");
        b.eventImageUrl = rs.getString("event_image_url");
        b.typeName      = rs.getString("type_name");
        b.ticketCode    = rs.getString("ticket_code");
        b.ticketStatus  = rs.getString("ticket_status");
        return b;
    }

    /**
     * Membuat kode tiket unik, contoh: EVT-A3F2-X9K1
     */
    private String generateTicketCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return "EVT-" + uuid.substring(0,4) + "-" + uuid.substring(4,8);
    }
}