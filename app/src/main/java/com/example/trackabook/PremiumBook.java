package com.example.trackabook;

public class PremiumBook extends Book{
    private static int premiumCostForOneWeek = 50 * 7;
    PremiumBook(int num_of_days_borrowed) {
        super(num_of_days_borrowed);
    }

    @Override
    public void computeCost() {
        if(getNum_of_days_borrowed() > 7){
            int extraDays = (getNum_of_days_borrowed() - 7) * 75;
            setTotalCost(premiumCostForOneWeek + extraDays);
        }
        else{
            setTotalCost(getNum_of_days_borrowed() * 50);
        }
    }
}
