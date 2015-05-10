package com.mandapaty.nishant.stockup;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class StartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_HIGHSCORE = "highscore";

    // TODO: Rename and change types of parameters
    private double mHighscore;

    private TextView mHighscore_textView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param highscore The highest score achieved so far
     * @return A new instance of fragment StartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartFragment newInstance(double highscore) {
        StartFragment fragment = new StartFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_HIGHSCORE, highscore);
        fragment.setArguments(args);
        return fragment;
    }
    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHighscore = getArguments().getDouble(ARG_HIGHSCORE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start, container, false);

        mHighscore_textView = (TextView) rootView.findViewById(
                R.id.fragment_start_high_score_textView);
        resetHSView();

        Button startGame_button = (Button) rootView.findViewById(
                R.id.fragment_start_startGame_button);
        startGame_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HomePagerActivity.class);
                intent.putExtra(HomePagerActivity.EXTRA_OLD_HIGHSCORE, mHighscore);
                startActivityForResult(intent, StartActivity.REQUEST_CODE);
            }
        });

        return rootView;
    }

    public void setHighscore(double newHighscore) {
        mHighscore = newHighscore;
        resetHSView();
    }

    private void resetHSView() {
        mHighscore_textView.setText("High Score = " + mHighscore);
    }


}
