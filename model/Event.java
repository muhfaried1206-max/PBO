package model;

public class Event {

    private int id;
    private String title;
    private String location;
    private String date;
    private double price;

    public Event() {
    }

    public Event(int id, String title,
                 String location,
                 String date,
                 double price) {

        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }
}