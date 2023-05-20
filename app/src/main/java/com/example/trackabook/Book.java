package com.example.trackabook;

public abstract class Book {
    private int num_of_days_borrowed;
    private int totalCost;

    Book(int num_of_days_borrowed){
        this.num_of_days_borrowed = num_of_days_borrowed;
    }

    public int getNum_of_days_borrowed() {
        return num_of_days_borrowed;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public abstract void computeCost();

    public int displayTotalCost(){
        return totalCost;
    }
}
