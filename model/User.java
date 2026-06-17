package model;

public abstract class User {

    protected int id;
    protected String name;
    protected String email;
    protected String password;

    public User(int id, String name,
                String email,
                String password) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public abstract boolean login();

    public abstract void register();

    public abstract void logout();
}