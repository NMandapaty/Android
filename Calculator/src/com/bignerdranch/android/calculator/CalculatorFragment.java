package com.bignerdranch.android.calculator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CalculatorFragment extends Fragment {
	
	private static final String TAG = "CalculatorFragment";
	
	private TextView mSelectedTextView;
	private TextView mWorkingTextView;
	
	
	private void resetSelected() {
		mSelectedTextView.setText("0");
	}
	
	private void resetWorking() {
		mWorkingTextView.setText("0");
	}
	
	private boolean isOperation(char c) {
		return c == '+' ||
			   c == '-' ||
			   c == '*' || 
			   c == '/';
	}
	
	private int performOperation(String arg1, String arg2, char operation) throws IllegalArgumentException {
		int num1 = Integer.parseInt(arg1);
		int num2 = Integer.parseInt(arg2);
		switch(operation) {
		case '+': 
			return num1 + num2;
		case '-':
			return num1 - num2;
		case '*':
			return num1 * num2;
		case '/':
			return num1 / num2;
		default:
			throw new IllegalArgumentException("given char is not an operation");
		}
	}
	
	private void evaluate() {
		String selectedText = mSelectedTextView.getText().toString();
		char c = selectedText.charAt(selectedText.length() - 1);
		if(isOperation(c)) {
			int ans = performOperation(selectedText.substring(0, selectedText.length() - 1), //arg1
										mWorkingTextView.getText().toString(), //arg2
										c); //operation
			mSelectedTextView.setText("" + ans);
		}
		else {
			mSelectedTextView.setText(mWorkingTextView.getText());
		}
		resetWorking();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		super.onCreateView(inflater, parent, savedInstanceState);
		
		View v = inflater.inflate(R.layout.fragment_calculator, parent, false);
		
		mSelectedTextView = (TextView) v.findViewById(R.id.fragment_calculator_selectedTextView);
		mWorkingTextView = (TextView) v.findViewById(R.id.fragment_calculator_workingTextView);
		
		TableLayout table = (TableLayout) v.findViewById(R.id.fragment_calculator_tableLayout);
		int number = 1;
		for(int i = 2; i < table.getChildCount() - 1; i++) {
			TableRow row = (TableRow)table.getChildAt(i);
			//set up operation buttons
			Button op = (Button) row.getChildAt(row.getChildCount() - 1);
			switch(i) {
			case 2: //ADD button
				op.setText(R.string.add_string);
				op.setOnClickListener(new operationButtonListener("+"));
				break;
			case 3: //SUBTRACT button
				op.setText(R.string.subtract_string);
				op.setOnClickListener(new operationButtonListener("-"));
				break;
			case 4: //MULTIPLY button
				op.setText(R.string.multiply_string);
				op.setOnClickListener(new operationButtonListener("*"));
				break;
			}
			for(int j = 0; j < row.getChildCount() - 1; j++) {
				Button button = (Button) row.getChildAt(j);
				button.setText("" + number);
				button.setOnClickListener(new numberButtonListener("" + number));
				number++;
			}
		}
		
		TableRow bottomRow = (TableRow) table.getChildAt(table.getChildCount() - 1);
		
		Button deleteButton = (Button) bottomRow.getChildAt(0);
		deleteButton.setText(R.string.delete_string);
		deleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(mWorkingTextView.getText().toString().equals("0")) {
					resetSelected();
				}
				else {
					resetWorking();
				}
			}
		});
		
		Button zeroButton = (Button) bottomRow.getChildAt(1);
		zeroButton.setText(R.string.zero_string);
		zeroButton.setOnClickListener(new numberButtonListener("0"));
		
		Button enterButton = (Button) bottomRow.getChildAt(2);
		enterButton.setText(R.string.enter_string);
		enterButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(mWorkingTextView.getText().toString().equals("0")) {
					return;
				}
				evaluate();
			}
		});
		
		Button divideButton = (Button) bottomRow.getChildAt(3);
		divideButton.setText(R.string.divide_string);
		divideButton.setOnClickListener(new operationButtonListener("/"));
		
		return v;
	}
	
	private class numberButtonListener implements View.OnClickListener {
		
		private String mNumber;
		
		public numberButtonListener(String num) {
			mNumber = num;
		}
		
		@Override
		public void onClick(View v) {
			String workingText = mWorkingTextView.getText().toString();
			if(workingText.equals("0")) {
				mWorkingTextView.setText(mNumber);
			}
			else {
				mWorkingTextView.setText(workingText + mNumber);
			}
		}
	}
	
	private class operationButtonListener implements View.OnClickListener {
		
		private String mOperation;
		
		public operationButtonListener(String op) {
			mOperation = op;
		}

		@Override
		public void onClick(View arg0) {
            String selectedText = mSelectedTextView.getText().toString();
            if(isOperation(selectedText.charAt(selectedText.length() - 1))) {

            }
			if(! mWorkingTextView.getText().toString().equals("0")) {
				evaluate();
			}
			mSelectedTextView.setText(selectedText + mOperation);
		}

	}

}
