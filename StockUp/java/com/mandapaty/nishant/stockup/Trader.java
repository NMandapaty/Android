package com.mandapaty.nishant.stockup;

public class Trader {

private int[] mStocks;
private double mWallet;
private static Trader  sTrader;

private Trader(){
	mWallet = 0;
	mStocks = new int[StockRoom.NUM_STOCKS];
}
public static Trader get() {
	if (sTrader == null) {
		sTrader = new Trader();
	}
	
	return sTrader;
}
public double getWallet(){
	return mWallet;
}

public int getStocks(int index){
	return mStocks[index];
}

public void buyStocks(int index, int amount){
	mStocks[index] += amount;
}

public void sellStocks(int index, int amount){
	mStocks[index] -= amount;
}

public void withdraw(int amount){
	mWallet -= amount;
}

public void deposit(int amount){
	mWallet += amount;
}

public double getWorth() {
    double worth = sTrader.getWallet();
    for(int i = 0; i < StockRoom.NUM_STOCKS; i++) {
        worth += sTrader.getStocks(i) * StockRoom.get().getPrice(i);
    }

    return worth;
}

public void reset() {
    sTrader = null;
}

}
