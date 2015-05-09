package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends Activity {
	
	public static final String EXTRA_ANSWER = "com.bignerdranch.android.geoquiz.answer";
	public static final String EXTRA_CHEATED = "com.bignerdranch.android.geoquiz.cheated";

	private boolean mAnswer;
	private boolean cheated;
	
	private TextView mAnswerTextView;
	private Button mShowAnswer;
	
	private void setCheatedResult() {
		Intent data = new Intent();
		data.putExtra(EXTRA_CHEATED, cheated);
		setResult(RESULT_OK, data);
	}
	
	private void showAnswer() {
		if(mAnswer) {
			mAnswerTextView.setText(R.string.true_button);
		}
		else {
			mAnswerTextView.setText(R.string.false_button);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cheat);
		
		cheated = savedInstanceState != null ?
				savedInstanceState.getBoolean(EXTRA_CHEATED) :
				false;

		setCheatedResult(); //Answer will not be shown the answer until user clicks the button
		
		mAnswer = this.getIntent().getBooleanExtra(CheatActivity.EXTRA_ANSWER, false);
		
		mAnswerTextView = (TextView) this.findViewById(R.id.answer_text_view);
		
		mShowAnswer = (Button) this.findViewById(R.id.show_answer_button);
		mShowAnswer.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showAnswer();
				cheated = true;
				setCheatedResult();
			}
						
		});
		
		if(cheated) {
			showAnswer();
		}
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(EXTRA_CHEATED, cheated);
	}
}
 