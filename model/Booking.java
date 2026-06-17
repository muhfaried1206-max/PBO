package model;

public class Booking {

    private int bookingId;
    private int quantity;
    private double totalPrice;

    public Booking() {
    }

    public Booking(int bookingId, int quantity, double totalPrice) {
        this.bookingId = bookingId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public double calculateTotal() {
        return totalPrice;
    }
}