package com.bignerdranch.android.criminalintent;

import java.util.Calendar;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class CrimeFragment extends Fragment {
	
	public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
	public static final String EXTRA_IS_NEW_CRIME = "com.bignerdranch.android.criminalintent.is_new_crime";
	public static final String EXTRA_STARTING_ID = "com.bignerdranch.android.criminalintent.starting_id";
	
	private static final String DIALOG_DATE = "date";
	private static final int REQUEST_DATE = 0;
	private static final String DIALOG_TIME = "time";
	private static final int REQUEST_TIME = 1;
	
	private static final int REQUEST_CONTACT = 2;
	
	private static final String TAG = "CrimeFragment";
	
	private Crime mCrime;
	private UUID mStartingID; //result should only be updated for starting crime
	private boolean mIsBlankCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private Button mTimeButton;
	private CheckBox mSolvedCheckBox;
	private Button mSuspectButton;
	private Button mCallButton;
	private Callbacks mCallbacks;
	
	public interface Callbacks {
		
		/**
		 * Called when user clicks the delete menu item
		 * @param crime the Crime to be deleted
		 */
		void onCrimeDeleted(Crime crime);
		
		void onCrimeUpdated(Crime crime);
	}
	
	public static CrimeFragment newInstance(UUID crimeID, boolean isNewCrime, UUID startingID) {
		Bundle args = new Bundle(); 
		args.putSerializable(EXTRA_CRIME_ID, crimeID);
		args.putBoolean(EXTRA_IS_NEW_CRIME, isNewCrime);
		args.putSerializable(EXTRA_STARTING_ID, startingID);
		
		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	public static CrimeFragment newInstance(UUID crimeID) {
		return newInstance(crimeID, false, null);
	}
	
	private void updateDate() {
		mDateButton.setText(mCrime.getFormattedDate());
	}
	
	private void updateTime() {
		mTimeButton.setText(mCrime.getFormattedTime());
	}
	
	private void updateSuspectButtons() {
		String suspect = mCrime.getSuspect();
		if(suspect != null) {
			mSuspectButton.setText(getString(R.string.crime_report_suspect, suspect));
		}
		else {
			mSuspectButton.setText(R.string.crime_suspect_text);
		}
		
		mCallButton.setEnabled(suspect != null);
		String callButtonText = suspect == null ?
			getString(R.string.crime_call_suspect_text, "suspect") :
			getString(R.string.crime_call_suspect_text, suspect);
		mCallButton.setText(callButtonText);
	}
	
	/**
	 * Called when activity is destroyed in order to send information back to parent activity:
	 * CrimeListFragment. Used to delete newly created, but blank, crimes.
	 */
	private void updateResult() {
		if(mCrime.getID().equals(mStartingID)) {
			Intent data = new Intent();
			data.putExtra(EXTRA_CRIME_ID, mCrime.getID());
			getActivity().setResult(mIsBlankCrime ? Activity.RESULT_CANCELED : Activity.RESULT_OK, data);
		}
	}
	
	private String getCrimeReport() {
		String solved = mCrime.isSolved() ? 
			getString(R.string.crime_report_solved) :
			getString(R.string.crime_report_unsolved);
		String date = mCrime.getFormattedDateTime();
		String suspect = mCrime.getSuspect() == null ? 
			getString(R.string.crime_report_no_suspect) :
			getString(R.string.crime_report_suspect, mCrime.getSuspect());
		String title = mCrime.getTitle().length() == 0 ?
			getString(R.string.crime_report_placeholder_title) :
			mCrime.getTitle();
		
		String report = getString(R.string.crime_report,
				title, date, solved, suspect);
		
		return report;
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (Callbacks) activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		UUID crimeID = (UUID) getActivity().getIntent().getSerializableExtra(EXTRA_CRIME_ID);
		Bundle args = getArguments();
		UUID crimeID = (UUID) args.getSerializable(EXTRA_CRIME_ID);
		mStartingID = (UUID) args.getSerializable(EXTRA_STARTING_ID);
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeID);
		
		if(savedInstanceState != null) {
			mIsBlankCrime = savedInstanceState.getBoolean(EXTRA_IS_NEW_CRIME);
		}
		else {
			mIsBlankCrime = args.getBoolean(EXTRA_IS_NEW_CRIME);
		}
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home) {
			if(NavUtils.getParentActivityName(getActivity()) != null) {
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;
		}
		else if(item.getItemId() == R.id.menu_item_delete) {
			mCallbacks.onCrimeDeleted(mCrime);
			return true;
		}
		else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK) {
			if(requestCode == REQUEST_CONTACT) {
				updateSuspectButtons();
			}
			return; 
		}
		switch(requestCode) {
		case REQUEST_DATE:
			Calendar date = (Calendar) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			mCrime.setDate(date);
			updateDate();
			mCallbacks.onCrimeUpdated(mCrime);
			break;
		case REQUEST_TIME:
			Calendar time = (Calendar) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
			mCrime.setTime(time);
			updateTime();
			mCallbacks.onCrimeUpdated(mCrime);
			break;
		case REQUEST_CONTACT:
			Uri contactUri = data.getData();
			
			//Specify which fields you want your query to return values for
			String[] queryFields = new String[] {
					ContactsContract.Contacts.DISPLAY_NAME, 
					ContactsContract.Contacts._ID
//					ContactsContract.CommonDataKinds.Phone.NUMBER
			};
			
			//Perform your query - the contactUri is like a "where" clause here
			Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
			
			//Double-check that you actually got results
			if(c.getCount() == 0) {
				Log.d(TAG, "no results");
				c.close();
				return;
			}
			
			//Pull out the first column of the first row of data - suspect's name
			c.moveToFirst();
			String suspect = c.getString(0);
			String suspectID = c.getString(1);
			Log.d(TAG, "Suspect = " + suspect);
			Log.d(TAG, "SuspectID = " + suspectID);
			mCrime.setSuspect(suspect, suspectID);
			mCallbacks.onCrimeUpdated(mCrime);
			updateSuspectButtons();
			c.close();
		}

	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, parent, false);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
		   NavUtils.getParentActivityName(getActivity()) != null) {
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		mTitleField = (EditText) v.findViewById(R.id.crime_title_edit_text);
		mTitleField.setText(mCrime.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable c) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence c, int start, int count, int after) {
				// TODO Auto-generated method stub	
			}

			@Override
			public void onTextChanged(CharSequence c, int start, int before, int count) {
				mCrime.setTitle(c.toString());
				getActivity().setTitle(c.toString());
				mCallbacks.onCrimeUpdated(mCrime);
				mIsBlankCrime = c.toString().isEmpty();
				updateResult();
			}
			
		});
		
		mDateButton = (Button) v.findViewById(R.id.crime_date_button);
		updateDate();
		mDateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
//				DatePickerFragment dialog = new DatePickerFragment();
				DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				dialog.show(fm, DIALOG_DATE);
			}
		});
		
		mTimeButton = (Button) v.findViewById(R.id.crime_time_button);
		updateTime();
		mTimeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
				dialog.show(fm, DIALOG_TIME);
			}
		});
		
		mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved_check_box);
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//Set the crime's solved property
				mCrime.setSolved(isChecked);
				mCallbacks.onCrimeUpdated(mCrime);
			}
		});
		
		mSuspectButton = (Button) v.findViewById(R.id.crime_suspect_button);
		mSuspectButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCrime.setSuspect(null, null);
				Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(i, REQUEST_CONTACT);
			}
		});
		
		mCallButton = (Button) v.findViewById(R.id.crime_call_suspect_button);
		mCallButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String[] queryFields = new String[] { 
						ContactsContract.CommonDataKinds.Phone.NUMBER
				};
				String selection = ContactsContract.CommonDataKinds.Phone._ID + " = ?";
				String[] selectionArgs = new String[] { mCrime.getSuspectID() };
				Cursor c = getActivity().getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
						queryFields, selection, selectionArgs, null);
				
				Log.d(TAG, "cursor = " + c);
				if(c.getCount() == 0) {
					Log.d(TAG, "no phone numbers");
					c.close();
					return;
				}
				
				c.moveToFirst();
				String phoneNumber = c.getString(0); //only one column
				Uri number = Uri.parse("tel" + phoneNumber);
				Intent i = new Intent(Intent.ACTION_DIAL, number);
				startActivity(i);
				c.close();
			}
		});
		
		updateSuspectButtons();
		
		Button reportButton = (Button) v.findViewById(R.id.crime_report_button);
		reportButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain")
				 .putExtra(Intent.EXTRA_TEXT, getCrimeReport())
				 .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
				
				startActivity(Intent.createChooser(i, getString(R.string.send_report)));
			}
		});
						
		updateResult();
		return v;
	}
	
	@Override 
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(EXTRA_IS_NEW_CRIME, mIsBlankCrime);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		updateResult();
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}
}
