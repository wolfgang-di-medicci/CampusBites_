package com.example.cafeticker;

public class Meal {
    private String username, date, meal, ticker;

    public Meal() {}

    public Meal(String username, String date, String meal, String ticker) {
        this.username = username;
        this.date = date;
        this.meal = meal;
        this.ticker = ticker;
    }

    public String getUsername() { return username; }

    public String getDate() { return date; }

    public String getMeal() { return meal; }

    public String getTicker() { return ticker; }

}
