package fats.com.pocketmonitor;

public class Transaction {

    private String date;
    private int type;
    private String category;
    private String info;
    private double amount;

    public Transaction(){

    }

    public Transaction(double amount, String date, String category, String info, int type) {
        this.amount = amount;
        this.date = date;
        this.info = info;
        this.type = type;
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getInfo() {
        return info;
    }

    public int getType() {
        return type;
    }

    public String getCategory() {

        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setType(int type) {
        this.type = type;
    }
}
