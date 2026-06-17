<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.EventDAO.EventDTO" %>
<%@ page import="dao.EventDAO.TicketTypeDTO" %>
<%
    if (session == null || session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    EventDTO event = (EventDTO) request.getAttribute("event");
    List<TicketTypeDTO> ticketTypes = (List<TicketTypeDTO>) request.getAttribute("ticketTypes");
    if (event == null) { response.sendRedirect("events"); return; }
%>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= event.title %> - Eventify</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="css/eventdetail.css">
</head>
<body>

<!-- Hero -->
<div class="hero-section">
    <img src="<%= event.imageUrl != null ? event.imageUrl : "images/event1.jpg" %>"
         alt="<%= event.title %>"
         onerror="this.src='images/event1.jpg'">
    <div class="hero-overlay"></div>

    <div class="hero-top-bar">
        <a href="events" class="btn-circle"><i class="bi bi-arrow-left fs-5"></i></a>
        <button class="btn-circle" onclick="shareEvent()">
            <i class="bi bi-share fs-5"></i>
        </button>
    </div>

    <div class="hero-bottom">
        <span class="badge-category"><%= event.category %></span>
        <h1 class="fw-bold fs-2 mb-0"><%= event.title %></h1>
    </div>
</div>

<!-- Content -->
<div class="container" style="max-width:860px; padding:32px 16px;">

    <!-- Event Details -->
    <div class="card-section">
        <h2 class="fw-semibold mb-4">Event Details</h2>

        <div class="d-flex gap-3 mb-4 align-items-start">
            <i class="bi bi-calendar3 detail-icon"></i>
            <div>
                <p class="fw-medium mb-0 text-dark"><%= event.date %></p>
                <p class="text-muted mb-0"><%= event.time %> WIB</p>
            </div>
        </div>

        <div class="d-flex gap-3 mb-4 align-items-start">
            <i class="bi bi-geo-alt detail-icon"></i>
            <div>
                <p class="fw-medium mb-0 text-dark"><%= event.location %></p>
            </div>
        </div>

        <div class="d-flex gap-3 align-items-start">
            <i class="bi bi-people detail-icon"></i>
            <div>
                <p class="fw-medium mb-0 text-dark">Kapasitas</p>
                <p class="text-muted mb-0"><%= String.format("%,d", event.capacity) %> orang</p>
            </div>
        </div>
    </div>

    <!-- About -->
    <div class="card-section">
        <h2 class="fw-semibold mb-3">Tentang Event</h2>
        <p class="text-secondary lh-lg mb-0"><%= event.description %></p>
    </div>

    <!-- Ticket Types -->
    <div class="card-section">
        <h2 class="fw-semibold mb-3">Tiket Tersedia</h2>
        <% if (ticketTypes != null) { for (TicketTypeDTO tt : ticketTypes) { %>
        <div class="d-flex justify-content-between align-items-center border-bottom py-2">
            <div>
                <strong><%= tt.typeName %></strong>
                <br>
                <small class="text-muted">Sisa: <%= tt.available %> tiket</small>
            </div>
            <div class="text-end">
                <strong>Rp <%= String.format("%,.0f", tt.price) %></strong>
            </div>
        </div>
        <% } } %>
    </div>

    <!-- Book Button -->
    <div class="card-section d-flex align-items-center justify-content-between flex-wrap gap-3">
        <div>
            <p class="price-label mb-0">Harga mulai dari</p>
            <% double minP = ticketTypes != null && !ticketTypes.isEmpty() ? ticketTypes.stream().mapToDouble(t -> t.price).min().orElse(0) : 0; %>
            <p class="price-value mb-0">Rp <%= String.format("%,.0f", minP) %></p>
        </div>
        <a href="booking?eventId=<%= event.id %>" class="btn-book">Book Ticket</a>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
function shareEvent() {
    if (navigator.share) {
        navigator.share({ title: '<%= event.title %>', url: window.location.href });
    } else {
        navigator.clipboard.writeText(window.location.href);
        alert('Link disalin!');
    }
}
</script>
</body>
</html>
