package com.mandapaty.nishant.stockup;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PortfolioFragment extends ListFragment {

    private static final int label_index = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new PortfolioAdapter());
    }

    private static ArrayList<Integer> getIndices() {
        ArrayList<Integer> indices = StockRoom.getStockIndexes();
        indices.add(0, label_index);

        return indices;
    }

    private class PortfolioAdapter extends ArrayAdapter<Integer> {

        public PortfolioAdapter() {
            super(getActivity(), 0, getIndices());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int stock = getItem(position);

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.portfolio_list_item, null);
            }

            TextView name = (TextView) convertView.findViewById(R.id.stock_name);
            TextView amount = (TextView) convertView.findViewById(R.id.stock_amount);
            TextView price = (TextView) convertView.findViewById(R.id.stock_price);


            if(stock == label_index) {
                name.setText("");
                amount.setText("Amount");
                price.setText("Current Value");
            }
            else {
                name.setText(StockRoom.getName(stock));
                amount.setText(Trader.get().getStocks(stock) + "");
                price.setText(StockRoom.formatMoney(Trader.get().getStocks(stock) * StockRoom.get()
                        .getPrice(stock)));
            }
            return convertView;
        }

    }

}
