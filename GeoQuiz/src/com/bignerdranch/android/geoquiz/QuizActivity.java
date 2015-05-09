package com.bignerdranch.android.geoquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends ActionBarActivity {
	
	private static final String KEY_INDEX = "index";
	private static final String KEY_CHEATED_ARRAY = "cheated array";
	
	private Button mTrueButton;
	private Button mFalseButton;
	private Button mNextButton;
	private Button mPrevButton;
	private Button mCheatButton;
	private TextView mQuestionTextView;
	
	private int mEndText;
	
	private static TrueFalse[] sQuestionBank = new TrueFalse[] {
		new TrueFalse(R.string.oceans, true),
		new TrueFalse(R.string.mideast, false), 
		new TrueFalse(R.string.africa, false), 
		new TrueFalse(R.string.americas, true),
		new TrueFalse(R.string.asia, true)
	};
	
	private int mCurrentIndex;
	
	private boolean[] mCheatedOnQuestion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);
		
		if(savedInstanceState != null) {
			mCheatedOnQuestion = savedInstanceState.getBooleanArray(KEY_CHEATED_ARRAY);
			mCurrentIndex = savedInstanceState.getInt(KEY_INDEX);
		}
		else {
			mCheatedOnQuestion = new boolean[sQuestionBank.length];
			mCurrentIndex = (int) (Math.random() * sQuestionBank.length);
		}		
		
		mEndText = R.string.end_text;
		
		mQuestionTextView = (TextView) this.findViewById(R.id.question_text_view);
		this.showQuestion();

		mTrueButton = (Button) this.findViewById(R.id.true_button);
		mTrueButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showAnswer(true);
			}
		});
		mFalseButton = (Button) this.findViewById(R.id.false_button);
		mFalseButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showAnswer(false);
			}
		});
		
		mNextButton = (Button) this.findViewById(R.id.next_button);
		mNextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCurrentIndex = (mCurrentIndex + 1) % sQuestionBank.length;
				showQuestion();
				mCheatButton.setEnabled(! mCheatedOnQuestion[mCurrentIndex]);
			}
		});
		
		mPrevButton = (Button) this.findViewById(R.id.prev_button);
		mPrevButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCurrentIndex = (mCurrentIndex + sQuestionBank.length - 1) % sQuestionBank.length;		
				showQuestion();
				mCheatButton.setEnabled(! mCheatedOnQuestion[mCurrentIndex]);
			}
		});

		mCheatButton = (Button) this.findViewById(R.id.cheat_button);
		mCheatButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent cheat = new Intent(QuizActivity.this, CheatActivity.class);
				cheat.putExtra(CheatActivity.EXTRA_ANSWER, sQuestionBank[mCurrentIndex].isTrue());
				startActivityForResult(cheat, 0);		
			}
		});
		mCheatButton.setEnabled(! mCheatedOnQuestion[mCurrentIndex]);
	}
	
	/**
	 * Gets the question from sQuestionBank using mCurrentIndex, 
	 * and then sets mQuestionTextView's text. 
	 */
	private void showQuestion() {
		if(mCurrentIndex < sQuestionBank.length) {
			int question = sQuestionBank[mCurrentIndex].getQuestion();
			mQuestionTextView.setText(question);
		}
		else {
			mQuestionTextView.setText(mEndText);
		}
	}
	
	private void showAnswer(boolean userPressedTrue) {
		boolean answerIsTrue = sQuestionBank[mCurrentIndex].isTrue();
		boolean userIsCorrect = userPressedTrue == answerIsTrue;
		/*
		if(userIsCorrect && mIsCheater) {
			Toast.makeText(this, R.string.warning_text, Toast.LENGTH_SHORT)
				.show();
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		*/
		int messageResId = userIsCorrect ? 
					(mCheatedOnQuestion[mCurrentIndex] ? R.string.judgement_toast : R.string.correct_toast) :						
					R.string.incorrect_toast;
		Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
			.show();
		
		if(userIsCorrect) { //increment counter
			mCurrentIndex = (mCurrentIndex + 1) % sQuestionBank.length;	
			mCheatButton.setEnabled(! mCheatedOnQuestion[mCurrentIndex]);
			showQuestion();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data == null) {
			return;
		}
		mCheatedOnQuestion[mCurrentIndex] = data.getBooleanExtra(CheatActivity.EXTRA_CHEATED, false);
		if(mCheatedOnQuestion[mCurrentIndex]) {
			mCheatButton.setEnabled(false);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_INDEX, mCurrentIndex);
		outState.putBooleanArray(KEY_CHEATED_ARRAY, mCheatedOnQuestion);
	}
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quiz, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
