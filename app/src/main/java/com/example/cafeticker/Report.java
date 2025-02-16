package com.example.cafeticker;

public class Report {
    private String ticker, date, username;

    public Report() {}

    public Report(String ticker, String date, String username) {
        this.ticker = ticker;
        this.date = date;
        this.username = username;
    }

    public String getTicker() { return ticker; }

    public String getDate() { return date; }

    public String getUsername() { return username; }

}
