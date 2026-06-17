<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.EventDAO.EventDTO" %>
<%
    // Guard: harus login
    if (session == null || session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String userName = (String) session.getAttribute("userName");
    List<EventDTO> events = (List<EventDTO>) request.getAttribute("events");
%>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Eventify</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="css/dashboard.css">
</head>
<body>

<!-- Navbar -->
<div class="navbar">
    <div class="nav-left">
        <img src="images/logoclear.png" class="logo" alt="Eventify">
        <h2>Eventify</h2>
    </div>
    <div class="nav-center">
        <a href="my-tickets" class="ticket-btn">My Tickets</a>
    </div>
    <div class="nav-right">
        <span class="me-3 text-muted small">Halo, <%= userName %>!</span>
        <a href="LogoutServlet" class="logout-btn">Logout</a>
    </div>
</div>

<div class="container mt-4">
    <h1>Discover Events</h1>

    <% if (events == null || events.isEmpty()) { %>
        <div class="alert alert-info">Belum ada event tersedia.</div>
    <% } else { %>
        <% for (EventDTO ev : events) { %>
        <div class="card">
            <img src="<%= ev.imageUrl != null ? ev.imageUrl : "images/event1.jpg" %>"
                 alt="<%= ev.title %>"
                 onerror="this.src='images/event1.jpg'">
            <div class="card-body">
                <span class="badge bg-secondary mb-1"><%= ev.category %></span>
                <h3><%= ev.title %></h3>
                <p><i class="bi bi-geo-alt"></i> <%= ev.location %></p>
                <p><i class="bi bi-calendar3"></i> <%= ev.date %></p>
                <p class="fw-bold">
                    Mulai dari Rp <%= String.format("%,.0f", ev.minPrice) %>
                </p>
                <a class="btn" href="event-detail?id=<%= ev.id %>">Details</a>
            </div>
        </div>
        <% } %>
    <% } %>
</div>

</body>
</html>
