package com.mandapaty.nishant.stockup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;



public class StartActivity extends ActionBarActivity {

    public static final int DEFAULT_HIGHSCORE_VALUE = 0;
    public static final String PREF_HIGH_SCORE = "pref highscore";

    public static final int REQUEST_CODE = 0;

    private StartFragment mStartFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        SharedPreferences pref = this.getPreferences(Context.MODE_PRIVATE);
        double highscore = pref.getFloat(PREF_HIGH_SCORE, this.DEFAULT_HIGHSCORE_VALUE);
        mStartFragment = StartFragment.newInstance(highscore);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mStartFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            double newHighscore = data.getDoubleExtra(PREF_HIGH_SCORE, DEFAULT_HIGHSCORE_VALUE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putFloat(PREF_HIGH_SCORE, (float) newHighscore);
            editor.apply();
            mStartFragment.setHighscore(newHighscore);
        }
    }
}
