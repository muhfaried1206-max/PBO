package dao;

import database.DBConnection;
import java.sql.*;

/**
 * DAO (Data Access Object) untuk tabel users.
 * Handles login, register, dan cek email unik.
 */
public class UserDAO {

    /**
     * Validasi login.
     * @return ResultSet row user jika valid, null jika tidak.
     *         Caller wajib menutup ResultSet dan Connection-nya sendiri.
     *         Di servlet, kita pakai helper getLoginUser() di bawah.
     */
    public UserDTO login(String email, String password) {
        String sql = "SELECT id, name, email, role FROM users "
                   + "WHERE email = ? AND password = MD5(?) LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserDTO u = new UserDTO();
                    u.id    = rs.getInt("id");
                    u.name  = rs.getString("name");
                    u.email = rs.getString("email");
                    u.role  = rs.getString("role");
                    return u;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mendaftarkan user baru.
     * @return true jika berhasil, false jika email sudah terdaftar / error.
     */
    public boolean register(String name, String email, String password) {
        // cek duplikat email
        if (emailExists(email)) return false;

        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, MD5(?), 'participant')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Cek apakah email sudah terdaftar. */
    public boolean emailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── Inner DTO ─────────────────────────────────────────────────────
    public static class UserDTO {
        public int    id;
        public String name;
        public String email;
        public String role;
    }
}