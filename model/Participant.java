package model;

public class Participant extends User {

    public Participant(int id,
                       String name,
                       String email,
                       String password) {

        super(id, name, email, password);
    }

    @Override
    public boolean login() {
        return true;
    }

    @Override
    public void register() {
        System.out.println("Participant Registered");
    }

    @Override
    public void logout() {
        System.out.println("Participant Logout");
    }
}