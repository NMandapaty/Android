package com.mandapaty.nishant.stockup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class TradeFragment extends ListFragment {

    public static final String EXTRA_AMOUNT = "com.mandapaty.android.stockup.amount";
    public static final String EXTRA_STOCK = "com.mandapaty.android.stockup.stock";

    private static final int REQUEST_BUY = 0;
    private static final String DIALOG_BUY = "buy";
    private static final int REQUEST_SELL = 1;
    private static final String DIALOG_SELL = "sell";

    private static final String TAG = "TradeFragment";

    private TradeCallbacks mCallbacks;

    /**
     * Hosting activities must implement this interface
     */
    public static interface TradeCallbacks {
        public void buy(int stock, int amount);
        public void sell(int stock, int amount);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new StockAdapter());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (TradeCallbacks) activity;
        } catch (ClassCastException e) {
            throw new IllegalStateException("Hosting activity must implement TradeFragment" +
                    ".TradeCallbacks", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        int stock = data.getIntExtra(EXTRA_STOCK, -1);
        int amount = data.getIntExtra(EXTRA_AMOUNT, 0);

        switch (requestCode) {
            case REQUEST_BUY:
                mCallbacks.buy(stock, amount);
                break;
            case REQUEST_SELL:
                mCallbacks.sell(stock, amount);
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private class StockAdapter extends ArrayAdapter<Integer> {

        public StockAdapter() {
            super(getActivity(), 0, StockRoom.getStockIndexes());
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent) {
            final int stock = getItem(position);

            if(convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.trade_list_item,
                        null);
            }

            TextView name = (TextView) convertView.findViewById(R.id.trade_stock_name);
            name.setText(StockRoom.getName(stock));

            TextView price = (TextView) convertView.findViewById(R.id.trade_stock_price);
            price.setText(StockRoom.formatMoney(StockRoom.get().getPrice(stock)));

            Button buyButton = (Button) convertView.findViewById(R.id.buy_button);
            buyButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();

                    int max = (int) (Trader.get().getWallet() / StockRoom.get().getPrice(stock));
                    NumberPickerFragment dialog = NumberPickerFragment.newInstance(stock, max);

                    dialog.setTargetFragment(TradeFragment.this, REQUEST_BUY);
                    dialog.show(fm, DIALOG_BUY);
                }
            });

            Button sellButton = (Button) convertView.findViewById(R.id.sell_button);
            sellButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();

                    int max = Trader.get().getStocks(stock);
                    NumberPickerFragment dialog = NumberPickerFragment.newInstance(stock, max);

                    dialog.setTargetFragment(TradeFragment.this, REQUEST_SELL);
                    dialog.show(fm, DIALOG_SELL);
                }
            });

            return convertView;
        }
    }

}
