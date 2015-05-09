package com.bignerdranch.android.geoquiz;

public class TrueFalse {
	
	/**
	 * Refers to the resource ID for the question string.
	 */
	private int mQuestion;

	/**
	 * Whether the question is true.
	 */
	private boolean mTrue;
	
	public TrueFalse(int question, boolean isTrue) {
		mQuestion = question;
		mTrue = isTrue;
	}
	
	public int getQuestion() {
		return mQuestion;
	}

	public void setQuestion(int question) {
		mQuestion = question;
	}

	public boolean isTrue() {
		return mTrue;
	}

	public void setTrue(boolean isTrue) {
		mTrue = isTrue;
	}

}
