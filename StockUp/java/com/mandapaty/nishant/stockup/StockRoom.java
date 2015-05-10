package com.mandapaty.nishant.stockup;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class StockRoom {
    public final static int NUM_STOCKS = 5;
	public final static int COKE = 0;
	public final static int GOOGLE = 1;
	public final static int NIKE = 2;
	public final static int VOLVO = 3;
	public final static int COSTCO = 4;
	
	private static StockRoom sStockRoom;

	private double[] mPrices;
    private int mDay;
    private NewsItem[] mNewsItems;
	
	private StockRoom() {
		mPrices = new double[NUM_STOCKS];
        mDay = 0;

        mNewsItems = new NewsItem[5];
        mNewsItems[0] = new NewsItem("Google announces conversion to a non-profit charitable. All" +
                " assets will be used in the creation of soup kitchens. Google stock plummets.", new Runnable() {

            @Override
            public void run() {
                mPrices[GOOGLE] = Math.random()*100 + 150;
            }
        });
        mNewsItems[1] = new NewsItem("Volvo CEO announces that new cars will use Source engine to" +
                " run onboard OS.",
                new Runnable() {

            @Override
            public void run() {
                mPrices[VOLVO] = Math.random()*50 + 50;
            }
        });
        mNewsItems[2] = new NewsItem("Coke CEO caught in drug scandal.",
                new Runnable() {

            @Override
            public void run() {
                mPrices[COKE] = Math.random()*5 + 5;
            }

        });
        mNewsItems[3] = new NewsItem("Nothing in the news today", new Runnable() {

            @Override
            public void run() {
                mPrices[COSTCO] = Math.random()*200 + 150;
            }
        });
        mNewsItems[4] = new NewsItem("Nike CFO 'just doing it' in a leaked sex tape.",
                new Runnable() {

            @Override
            public void run() {
                mPrices[NIKE] = Math.random()*20+20;
            }
        });

        this.incrementDay(null);
	}
	
	public static StockRoom get() {
		if(sStockRoom == null) {
			sStockRoom = new StockRoom();
		}
		
		return sStockRoom;
	}
	
	public double getPrice(int stock) {
		return round(mPrices[stock]);
	}

    public static double round(double d) {
        return Math.round(d * 100) / 100.0;
    }

    public static String formatMoney(double price) {
        String formatted = "$";
        String p = Double.toString(round(price));
        String decimal = p.substring(p.indexOf(".") + 1);
        if(decimal.length() == 2) {
            formatted += p;
        }
        else if(decimal.length() == 1) {
            formatted += p + "0";
        }
        else {

        }

        return formatted;
    }

    public static String getName(int stock) {
        switch(stock) {
            case COKE:
                return "Coke";
            case GOOGLE:
                return "Google";
            case NIKE:
                return "Nike";
            case VOLVO:
                return "Volvo";
            case COSTCO:
                return "Costco";
            default:
                return null;
        }
    }

    public int getDay() {
        return mDay;
    }
	
	public int incrementDay(Context context) {
		mPrices[GOOGLE] = Math.random()*200 + 500;
        mPrices[COKE] = Math.random()*20 + 40;
        mPrices[COSTCO] = Math.random()*100 +100;
        mPrices[VOLVO] = Math.random()*10 + 10;
        mPrices[NIKE] = Math.random()*50 + 60;

        mDay++;

        if(mDay > 1) {
            Toast.makeText(context, "Day " + mDay,
                    Toast.LENGTH_SHORT).show();
            NewsItem item = getNews();
            if (item != null) {
                item.run();
                Toast.makeText(context, item.mHeadLine, Toast.LENGTH_LONG).show();
            }
        }

        return mDay;
	}

    static ArrayList<Integer> getStockIndexes() {
        ArrayList<Integer> arr =  new ArrayList<Integer>();
        arr.add(GOOGLE);
        arr.add(COKE);
        arr.add(COSTCO);
        arr.add(VOLVO);
        arr.add(NIKE);

        return arr;
    }

    private NewsItem getNews() {
        double rand = Math.random();

        if(rand < 0.05) {
            return mNewsItems[0];
        }
        else if(rand < 0.15){
            return mNewsItems[1];
        }
        else if(rand < 0.25){
            return mNewsItems[2];
        }
        else if(rand < 0.35){
            return mNewsItems[4];
        }
        else if(rand < 0.5){
            return mNewsItems[3];
        }
        else {
            return null;
        }

    }


    public void reset() {
        sStockRoom = null;
    }

    private static class NewsItem {

        private String mHeadLine;
        private Runnable mAftermath;

        private NewsItem(String headline, Runnable aftermath) {
            mHeadLine = headline;
            mAftermath = aftermath;
        }

        private void run() {
            mAftermath.run();
        }

    }

}
