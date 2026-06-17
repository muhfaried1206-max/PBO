<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dao.EventDAO.EventDTO" %>
<%
    if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
        response.sendRedirect("login.jsp");
        return;
    }
    EventDTO event = (EventDTO) request.getAttribute("event");
    boolean isEdit = (event != null);
    String formAction = isEdit ? "update" : "create";
%>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= isEdit ? "Edit Event" : "Tambah Event" %> - Eventify Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container py-5" style="max-width:640px;">
    <h3 class="fw-bold mb-4"><%= isEdit ? "Edit Event" : "Tambah Event Baru" %></h3>

    <div class="card p-4 shadow-sm">
        <form action="admin/events" method="post">
            <input type="hidden" name="action" value="<%= formAction %>">
            <% if (isEdit) { %>
            <input type="hidden" name="id" value="<%= event.id %>">
            <% } %>

            <div class="mb-3">
                <label class="form-label fw-medium">Judul Event</label>
                <input type="text" name="title" class="form-control"
                       value="<%= isEdit ? event.title : "" %>" required>
            </div>

            <div class="mb-3">
                <label class="form-label fw-medium">Deskripsi</label>
                <textarea name="description" class="form-control" rows="3"><%= isEdit ? event.description : "" %></textarea>
            </div>

            <div class="row">
                <div class="col mb-3">
                    <label class="form-label fw-medium">Tanggal</label>
                    <input type="date" name="date" class="form-control"
                           value="<%= isEdit ? event.date : "" %>" required>
                </div>
                <div class="col mb-3">
                    <label class="form-label fw-medium">Jam Mulai</label>
                    <input type="time" name="time" class="form-control"
                           value="<%= isEdit ? event.time : "" %>" required>
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label fw-medium">Lokasi</label>
                <input type="text" name="location" class="form-control"
                       value="<%= isEdit ? event.location : "" %>" required>
            </div>

            <div class="row">
                <div class="col mb-3">
                    <label class="form-label fw-medium">Kapasitas</label>
                    <input type="number" name="capacity" class="form-control"
                           value="<%= isEdit ? event.capacity : 100 %>" min="1" required>
                </div>
                <div class="col mb-3">
                    <label class="form-label fw-medium">Kategori</label>
                    <select name="category" class="form-select">
                        <% String[] cats = {"Music","Technology","Food","Sport","Art","Education","Other"};
                           for (String c : cats) { %>
                        <option value="<%= c %>" <%= (isEdit && c.equals(event.category)) ? "selected" : "" %>><%= c %></option>
                        <% } %>
                    </select>
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label fw-medium">URL Gambar (opsional)</label>
                <input type="url" name="imageUrl" class="form-control"
                       value="<%= isEdit && event.imageUrl != null ? event.imageUrl : "" %>"
                       placeholder="https://...">
            </div>

            <% if (!isEdit) { %>
            <!-- Ticket Types (hanya saat create) -->
            <hr>
            <h5 class="fw-semibold mb-3">Jenis Tiket</h5>
            <div id="ticket-types">
                <div class="row mb-2 ticket-type-row">
                    <div class="col-4">
                        <input type="text" name="ticketTypeName" class="form-control form-control-sm"
                               placeholder="Nama (mis. Regular)" required>
                    </div>
                    <div class="col-4">
                        <input type="number" name="ticketTypePrice" class="form-control form-control-sm"
                               placeholder="Harga (Rp)" min="0" required>
                    </div>
                    <div class="col-3">
                        <input type="number" name="ticketTypeQuota" class="form-control form-control-sm"
                               placeholder="Kuota" min="1" required>
                    </div>
                </div>
            </div>
            <button type="button" class="btn btn-sm btn-outline-secondary mt-2"
                    onclick="addTicketType()">+ Tambah Jenis Tiket</button>
            <% } %>

            <div class="d-flex gap-2 mt-4">
                <button type="submit" class="btn btn-primary">
                    <%= isEdit ? "Simpan Perubahan" : "Buat Event" %>
                </button>
                <a href="admin/events" class="btn btn-outline-secondary">Batal</a>
            </div>
        </form>
    </div>
</div>

<script>
function addTicketType() {
    const container = document.getElementById('ticket-types');
    const row = document.createElement('div');
    row.className = 'row mb-2 ticket-type-row';
    row.innerHTML = `
        <div class="col-4"><input type="text" name="ticketTypeName" class="form-control form-control-sm" placeholder="Nama"></div>
        <div class="col-4"><input type="number" name="ticketTypePrice" class="form-control form-control-sm" placeholder="Harga (Rp)" min="0"></div>
        <div class="col-3"><input type="number" name="ticketTypeQuota" class="form-control form-control-sm" placeholder="Kuota" min="1"></div>
        <div class="col-1"><button type="button" class="btn btn-sm btn-danger" onclick="this.closest('.ticket-type-row').remove()">✕</button></div>
    `;
    container.appendChild(row);
}
</script>
</body>
</html>
