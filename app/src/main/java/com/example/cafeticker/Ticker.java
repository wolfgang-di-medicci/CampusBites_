package com.example.cafeticker;

public class Ticker {
    private String ticker, date, ticked;

    public Ticker() {}

    public Ticker(String ticker, String date, String ticked) {
        this.ticker = ticker;
        this.date = date;
        this.ticked = ticked;
    }

    public String getUsername() { return ticker; }

    public String getDate() { return date; }

    public String getTicked() { return ticked; }

}
