package fats.com.pocketmonitor;


public class User {
    private String email;
    private String name;
    private double balance;

    public User(){}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {

        return name;
    }

    public User(String email, String name, double balance){
        this.email = email;
        this.name = name;

        this.balance = balance;
    }

    public String getEmail() {
        return this.email;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getBalance() {

        return balance;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
