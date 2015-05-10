package com.bignerdranch.android.criminalintent;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
//import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment {
	
	public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";
	
	private Calendar mDate;
	
	public static DatePickerFragment newInstance(Calendar date) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DATE, date);
		
		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	private void sendResult(int resultCode) {
		if(getTargetFragment() == null) {
			return;
		}
		Intent i = new Intent();
		i.putExtra(EXTRA_DATE, mDate);
		
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mDate = (Calendar) getArguments().getSerializable(EXTRA_DATE);
		int year = mDate.get(Calendar.YEAR);
		int monthOfYear = mDate.get(Calendar.MONTH);
		int dayOfMonth = mDate.get(Calendar.DAY_OF_MONTH);
		/*
		 * Apparently, DatePickerDialog is buggy, so it's unsafe to use it
		return new DatePickerDialog(getActivity(), this, 
									c.get(Calendar.YEAR), 
									c.get(Calendar.MONTH), 
									c.get(Calendar.DAY_OF_MONTH));
		*/
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date_picker, null);
		
		DatePicker datePicker = (DatePicker) v.findViewById(R.id.dialog_date_datePicker);
		datePicker.init(year, monthOfYear, dayOfMonth, new DatePicker.OnDateChangedListener() {
			
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				mDate = new GregorianCalendar(year, monthOfYear, dayOfMonth);
				
				getArguments().putSerializable(EXTRA_DATE, mDate);				
			}
		});
		return new AlertDialog.Builder(getActivity())
			.setView(v)
			.setTitle(R.string.date_picker_title)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					sendResult(Activity.RESULT_OK);
				}
			})
			.create();
	}
	

}
