package com.mandapaty.nishant.stockup;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.NumberPicker;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class NumberPickerFragment extends DialogFragment {

    private static final String ARG_MAX = "max value";
    private static final String ARG_STOCK = "stock";
    private static final String TAG = "NumberPickerFragment";

    private NumberPicker mDialog;

    private int mMax;
    private int mStock;

    public static NumberPickerFragment newInstance(int stock, int maxValue) {
        Bundle args = new Bundle();
        args.putInt(ARG_MAX, maxValue);
        args.putInt(ARG_STOCK, stock);

        NumberPickerFragment fragment = new NumberPickerFragment();
        fragment.setArguments(args);

        return fragment;
    }


    public NumberPickerFragment() {
        // Required empty public constructor
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mMax = getArguments().getInt(ARG_MAX);
        mStock = getArguments().getInt(ARG_STOCK);

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_number_picker, null);

        mDialog = (NumberPicker) v.findViewById(R.id.dialog_numberPicker);
        mDialog.setMaxValue(mMax);
        mDialog.setMinValue(0);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.number_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode) {
        if(getTargetFragment() == null) {
            return;
        }
        Intent i = new Intent();
        i.putExtra(TradeFragment.EXTRA_AMOUNT, mDialog.getValue());
        i.putExtra(TradeFragment.EXTRA_STOCK, mStock);

        //Log.d(TAG, "request code = " + getTargetRequestCode());

        getTargetFragment().onActivityResult(getTargetRequestCode(),
                resultCode, i);
    }



}
