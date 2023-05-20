package com.example.trackabook;

public class RegularBook extends Book{
    private static int regularCostForOneWeek = 20 * 7;
    RegularBook(int num_of_days_borrowed) {
        super(num_of_days_borrowed);
    }

    @Override
    public void computeCost() {
        if(getNum_of_days_borrowed() > 7){
            int extraDays = (getNum_of_days_borrowed() - 7) * 45;
            setTotalCost(regularCostForOneWeek + extraDays);
        }
        else{
            setTotalCost(getNum_of_days_borrowed() * 20);
        }
    }
}
