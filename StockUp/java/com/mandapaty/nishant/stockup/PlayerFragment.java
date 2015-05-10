package com.mandapaty.nishant.stockup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class PlayerFragment extends Fragment {


    public PlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_player, container, false);

        double money = Trader.get().getWallet();
        TextView wallet = (TextView) v.findViewById(R.id.player_wallet_textView);
        wallet.setText("Money: " + StockRoom.formatMoney(money));


        TextView day = (TextView) v.findViewById(R.id.player_day_textView);
        day.setText("Day: " + StockRoom.get().getDay());

        return v;
    }


}
