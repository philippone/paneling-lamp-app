package net.philippschardt.panelinglamp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.astuetz.PagerSlidingTabStrip;

import database.PanelingLampDBHelper;
import fragments.EditFragments.EditMotorsFragment;
import fragments.OnFragmentInteractionListener;
import fragments.forms.OnHandleMessageListener;

public class EditFormActivity extends ActionBarActivity implements OnFragmentInteractionListener, OnHandleMessageListener {

    public static final String EXTRA_CARD = "editFormActivy_extra_card";
    public static final String EXTRA_NAME = "editFormActivy_extra_name";

    private Toolbar toolbar;
    private ViewPager mViewPager;
    private ViewPagerAdapter pagerAdapter;
    private Fragment mEditMotorsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_form);

        // Fragments
        mEditMotorsFragment = EditMotorsFragment.newInstance(0, 0, 0, 0, 0);

        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.editformTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Viewpager + Tabs
        mViewPager = (ViewPager) findViewById(R.id.edit_form_viewPager);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.edit_motors_tabs);
        tabs.setViewPager(mViewPager);


        // receive values
        Intent dataIntent = getIntent();
        if (dataIntent != null) {

            String name = dataIntent.getStringExtra(EXTRA_NAME);

            // set values
            EditText et = (EditText) findViewById(R.id.edit_motors_name);
            et.setText(name);
            et.setSelection(et.getText().length());

        }
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                String msg = intent.getStringExtra(MySocketService.EXTRA_MESSAGE_FORWARD);
                handleInput(msg);
            }
        }
    };



    @Override
    public void handleInput(String message) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register Reciever
        registerReceiver(mMessageReceiver, new IntentFilter(MySocketService.BROADCAST_ACTION));
    }


    @Override
    protected void onPause() {
        super.onPause();

        // unregister message receiver
        unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onSectionAttached(int number) {

    }

    @Override
    public boolean sendMsg(String message) {

        // TODO
        return false;
    }

    @Override
    public void resetAllMotors() {
        // TODO
    }

    @Override
    public int getMotorCount() {
        return 0;
    }

    @Override
    public void adjustAllMotorToZero() {
        // TODO

    }

    @Override
    public boolean liftMotorUp(int index, float roations) {
        // TODO
        return false;
    }

    @Override
    public boolean liftMotorDown(int index, float rotations) {
        // TODO
        return false;
    }

    @Override
    public boolean moveMotorToPos(int index, float position) {
        // TODO
         return false;
    }

    @Override
    public boolean moveToForm(long id, float[] motorPos, int[] ledValues) {
        // TODO
        return false;
    }

    @Override
    public PanelingLampDBHelper getDBHelper() {
        return null;
    }



    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private String[] titles = new String[]{getString(R.string.motorTab), getString(R.string.ledTab)};

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        public Fragment getItem(int num) {
            switch (num) {

                case 0:
                    return mEditMotorsFragment;
                default:
                    return EditPlaceHolder.newInstance(num );
            }
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

    }



    public static class EditPlaceHolder extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static EditPlaceHolder newInstance(int sectionNumber) {
            EditPlaceHolder fragment = new EditPlaceHolder();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public EditPlaceHolder() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_paneling_lamp, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

        }
    }


}
