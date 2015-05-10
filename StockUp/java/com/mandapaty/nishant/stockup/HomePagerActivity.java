package com.mandapaty.nishant.stockup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class HomePagerActivity extends ActionBarActivity
        implements ActionBar.TabListener, TradeFragment.TradeCallbacks {

    public static final String EXTRA_OLD_HIGHSCORE = "old highscore";
    private static final String TAG = "HomePagerActivity";

    private double mOldHighscore;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private static final int initial_deposit = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_pager);

        mOldHighscore = getIntent().getDoubleExtra(EXTRA_OLD_HIGHSCORE,
                StartActivity.DEFAULT_HIGHSCORE_VALUE);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        setTitle(mSectionsPagerAdapter.getPageTitle(0));

        Trader.get().deposit(initial_deposit);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fragment_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_item_next_day) {
            int day = StockRoom.get().incrementDay(this);
            refreshAdapter();

            if(day == 30) {
                double score = Trader.get().getWorth();

                if(score  > mOldHighscore) {
                    Intent data = new Intent();
                    data.putExtra(StartActivity.PREF_HIGH_SCORE, score);
                    setResult(Activity.RESULT_OK, data);

                    Toast.makeText(this, "NEW HIGHSCORE!", Toast.LENGTH_LONG).show();
                }
                else {
                    setResult(Activity.RESULT_CANCELED);
                }
            }
            return true;
        }
        else if(id == R.id.menu_item_reset) {
            StockRoom.get().reset();
            Trader.get().reset();

            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshAdapter() {

//            mSectionsPagerAdapter.notifyDataSetChanged();
//            mSectionsPagerAdapter.refreshFragments();

        int currentItem = mViewPager.getCurrentItem();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem((currentItem - 2) % 3);
        mViewPager.setCurrentItem((currentItem - 1) % 3);
        mViewPager.setCurrentItem(currentItem);
//        mSectionsPagerAdapter.refreshFragments();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
        setTitle(mSectionsPagerAdapter.getPageTitle(tab.getPosition()));

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void buy(int stock, int amount) {
        double totalPrice = StockRoom.get().getPrice(stock) * amount;

        if(Trader.get().getWallet() >= totalPrice) {
            Trader.get().buyStocks(stock, amount);
            Trader.get().withdraw((int) totalPrice);

            Toast.makeText(this, "Bought " + amount + " " + StockRoom.getName
                    (stock), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, R.string.too_poor, Toast.LENGTH_SHORT).show();
        }

//        mSectionsPagerAdapter.refreshPortfolio();
        refreshAdapter();
    }

    @Override
    public void sell(int stock, int amount) {
        double totalPrice = StockRoom.get().getPrice(stock) * amount;
        if(Trader.get().getStocks(stock) >= amount) {
            Trader.get().sellStocks(stock, amount);
            Trader.get().deposit((int) totalPrice);

            Toast.makeText(this, "Sold " + amount + " " + StockRoom.getName(stock),
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this,"No more stock in " + StockRoom.getName(stock),
                    Toast.LENGTH_SHORT).show();
        }

//        mSectionsPagerAdapter.refreshPortfolio();
        refreshAdapter();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> mFragments;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

            mFragments = new ArrayList<Fragment>();
            mFragments.add(new TradeFragment());
            mFragments.add(new PortfolioFragment());
            mFragments.add(new PlayerFragment());
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "Trade".toUpperCase(l);
                case 1:
                    return "Portfolio".toUpperCase(l);
                case 2:
                    return "Player".toUpperCase(l);
            }
            return null;
        }

        /*
        private void refreshFragments() {
            refreshTrade();
            refreshPortfolio();
        }

        private void refreshTrade() {
            refreshListFragment(0);
        }

        private void refreshPortfolio() {
            refreshListFragment(1);
        }

        private void refreshListFragment(int index) {
            ((ArrayAdapter<Integer>) ((ListFragment) mFragments.get(index)).getListAdapter())
                    .notifyDataSetChanged();
        }
        */
    }

}
