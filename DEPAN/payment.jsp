<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment - Summer Music Festival</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="css/payment.css">
</head>
<body>

    <!-- Success Overlay -->
    <div class="success-overlay" id="successOverlay">
        <div class="success-card">
            <div class="success-icon">
                <i class="bi bi-check-circle-fill"></i>
            </div>
            <h2 class="fw-bold text-dark mb-2">Payment Successful!</h2>
            <p class="text-muted mb-2">Your tickets have been booked successfully</p>
            <p class="text-muted small">Redirecting to My Tickets...</p>
        </div>
    </div>

    <!-- Header -->
    <header class="top-header">
        <div class="container d-flex align-items-center gap-3 py-3" style="max-width: 860px;">
            <button class="btn-back" onclick="history.back()">
                <i class="bi bi-arrow-left fs-5"></i>
            </button>
            <h1 class="mb-0 fs-5 fw-semibold text-dark">Payment</h1>
        </div>
    </header>

    <!-- Main Content -->
    <main class="container py-4" style="max-width: 860px;">

        <!-- Order Summary Card -->
        <div class="card-section">
            <h2 class="fs-6 fw-semibold text-dark mb-4">Order Summary</h2>

            <div class="pb-3 mb-3 border-bottom">
                <div class="d-flex justify-content-between mb-2">
                    <span class="text-muted">Event</span>
                    <span class="fw-medium text-dark">Summer Music Festival</span>
                </div>
                <div class="d-flex justify-content-between mb-2 small">
                    <span class="text-muted">Regular x 2</span>
                    <span class="text-dark">Rp 500.000</span>
                </div>
                <div class="d-flex justify-content-between mb-2 small">
                    <span class="text-muted">VIP x 1</span>
                    <span class="text-dark">Rp 500.000</span>
                </div>
            </div>

            <div class="d-flex justify-content-between fs-5">
                <span class="fw-semibold text-dark">Total</span>
                <span class="fw-bold text-primary">Rp 1.000.000</span>
            </div>
        </div>

        <!-- Payment Method Card -->
        <div class="card-section">
            <h2 class="fs-6 fw-semibold text-dark mb-4">Payment Method</h2>

            <!-- Credit/Debit Card -->
            <button class="method-btn" id="method-credit-card" onclick="selectMethod('credit-card')">
                <div class="method-icon">
                    <i class="bi bi-credit-card"></i>
                </div>
                <div class="method-info">
                    <p class="method-name">Credit/Debit Card</p>
                    <p class="method-desc">Visa, Mastercard, JCB</p>
                </div>
                <div class="radio-circle">
                    <div class="radio-dot"></div>
                </div>
            </button>

            <!-- E-Wallet -->
            <button class="method-btn" id="method-e-wallet" onclick="selectMethod('e-wallet')">
                <div class="method-icon">
                    <i class="bi bi-wallet2"></i>
                </div>
                <div class="method-info">
                    <p class="method-name">E-Wallet</p>
                    <p class="method-desc">GoPay, OVO, DANA</p>
                </div>
                <div class="radio-circle">
                    <div class="radio-dot"></div>
                </div>
            </button>

            <!-- Bank Transfer -->
            <button class="method-btn" id="method-bank-transfer" onclick="selectMethod('bank-transfer')">
                <div class="method-icon">
                    <i class="bi bi-bank"></i>
                </div>
                <div class="method-info">
                    <p class="method-name">Bank Transfer</p>
                    <p class="method-desc">BCA, Mandiri, BNI, BRI, Bank Lain</p>
                </div>
                <div class="radio-circle">
                    <div class="radio-dot"></div>
                </div>
            </button>
        </div>

        <!-- Pay Button -->
        <button class="btn-pay" id="btn-pay" disabled onclick="processPayment()">
            Pay Rp 1.000.000
        </button>

    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        let selectedMethod = '';

        function selectMethod(id) {
            // Remove selected dari semua
            document.querySelectorAll('.method-btn').forEach(btn => btn.classList.remove('selected'));

            // Set selected ke yang dipilih
            document.getElementById('method-' + id).classList.add('selected');
            selectedMethod = id;

            // Aktifkan tombol pay
            document.getElementById('btn-pay').disabled = false;
        }

        function processPayment() {
            if (!selectedMethod) return;

            const btn = document.getElementById('btn-pay');
            btn.disabled = true;
            btn.textContent = 'Processing...';

            setTimeout(() => {
                document.getElementById('successOverlay').classList.add('show');
                setTimeout(() => {
                    window.location.href = 'myticket.jsp';
                }, 2000);
            }, 2000);
        }
    </script>
</body>
</html>
