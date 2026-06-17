package dao;

import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk tabel events dan ticket_types.
 */
public class EventDAO {

    // ── Inner DTO ─────────────────────────────────────────────────────
    public static class EventDTO {
        public int    id;
        public String title;
        public String description;
        public String date;          // format YYYY-MM-DD dari DB
        public String time;
        public String location;
        public int    capacity;
        public String category;
        public String imageUrl;
        public double minPrice;      // harga tiket terendah untuk display
    }

    public static class TicketTypeDTO {
        public int    id;
        public int    eventId;
        public String typeName;
        public double price;
        public int    quota;
        public int    sold;          // calculated: total quantity booked + paid
        public int    available;     // quota - sold
    }

    // ─────────────────────────────────────────────────────────────────
    //  READ
    // ─────────────────────────────────────────────────────────────────

    /** Ambil semua event, urut tanggal ASC. */
    public List<EventDTO> getAllEvents() {
        List<EventDTO> list = new ArrayList<>();
        String sql = "SELECT e.*, "
                   + "  (SELECT MIN(tt.price) FROM ticket_types tt WHERE tt.event_id = e.id) AS min_price "
                   + "FROM events e ORDER BY e.date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapEvent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Ambil satu event berdasarkan ID. */
    public EventDTO getEventById(int id) {
        String sql = "SELECT e.*, "
                   + "  (SELECT MIN(tt.price) FROM ticket_types tt WHERE tt.event_id = e.id) AS min_price "
                   + "FROM events e WHERE e.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapEvent(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Ambil daftar jenis tiket untuk sebuah event, lengkap dengan sisa quota. */
    public List<TicketTypeDTO> getTicketTypes(int eventId) {
        List<TicketTypeDTO> list = new ArrayList<>();
        String sql = "SELECT tt.*, "
                   + "  COALESCE(SUM(b.quantity), 0) AS sold "
                   + "FROM ticket_types tt "
                   + "LEFT JOIN bookings b ON b.ticket_type_id = tt.id "
                   + "  AND b.payment_status IN ('pending','paid') "
                   + "WHERE tt.event_id = ? "
                   + "GROUP BY tt.id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TicketTypeDTO t = new TicketTypeDTO();
                    t.id        = rs.getInt("id");
                    t.eventId   = eventId;
                    t.typeName  = rs.getString("type_name");
                    t.price     = rs.getDouble("price");
                    t.quota     = rs.getInt("quota");
                    t.sold      = rs.getInt("sold");
                    t.available = t.quota - t.sold;
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────────────
    //  ADMIN: CREATE / UPDATE / DELETE
    // ─────────────────────────────────────────────────────────────────

    /**
     * Buat event baru (oleh Admin).
     * @return ID event baru, atau -1 jika gagal.
     */
    public int createEvent(String title, String description, String date,
                           String time, String location, int capacity,
                           String category, String imageUrl, int createdBy) {
        String sql = "INSERT INTO events "
                   + "(title,description,date,time,location,capacity,category,image_url,created_by) "
                   + "VALUES (?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, date);
            ps.setString(4, time);
            ps.setString(5, location);
            ps.setInt   (6, capacity);
            ps.setString(7, category);
            ps.setString(8, imageUrl);
            ps.setInt   (9, createdBy);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /** Tambah satu jenis tiket ke event. */
    public boolean addTicketType(int eventId, String typeName, double price, int quota) {
        String sql = "INSERT INTO ticket_types (event_id, type_name, price, quota) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt   (1, eventId);
            ps.setString(2, typeName);
            ps.setDouble(3, price);
            ps.setInt   (4, quota);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Update event oleh Admin. */
    public boolean updateEvent(int id, String title, String description, String date,
                               String time, String location, int capacity,
                               String category, String imageUrl) {
        String sql = "UPDATE events SET title=?, description=?, date=?, time=?, "
                   + "location=?, capacity=?, category=?, image_url=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, date);
            ps.setString(4, time);
            ps.setString(5, location);
            ps.setInt   (6, capacity);
            ps.setString(7, category);
            ps.setString(8, imageUrl);
            ps.setInt   (9, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Hapus event (cascade menghapus ticket_types & bookings & tickets). */
    public boolean deleteEvent(int id) {
        String sql = "DELETE FROM events WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  HELPER
    // ─────────────────────────────────────────────────────────────────
    private EventDTO mapEvent(ResultSet rs) throws SQLException {
        EventDTO e = new EventDTO();
        e.id          = rs.getInt("id");
        e.title       = rs.getString("title");
        e.description = rs.getString("description");
        e.date        = rs.getString("date");
        e.time        = rs.getString("time");
        e.location    = rs.getString("location");
        e.capacity    = rs.getInt("capacity");
        e.category    = rs.getString("category");
        e.imageUrl    = rs.getString("image_url");
        try { e.minPrice = rs.getDouble("min_price"); } catch (SQLException ignored) {}
        return e;
    }
}