package com.roshanadke.materialwallet.model;

public class PreviousData {

    private String date;
    private String amount;

    public  PreviousData(){

    }

    public PreviousData(String date, String amount) {
        this.date = date;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
