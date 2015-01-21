package net.philippschardt.panelinglamp;

import android.app.Activity;
import android.content.Intent;
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

public class EditFormActivity extends ActionBarActivity implements OnFragmentInteractionListener {

    public static final String EXTRA_CARD = "editFormActivy_extra_card";
    public static final String EXTRA_NAME = "editFormActivy_extra_name";

    private Toolbar toolbar;
    private ViewPager mViewPager;
    private ViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_form);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.editformTitle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mViewPager = (ViewPager) findViewById(R.id.edit_form_viewPager);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.edit_motors_tabs);
        tabs.setViewPager(mViewPager);


        Intent dataIntent = getIntent();

        String name = dataIntent.getStringExtra(EXTRA_NAME);

        // set values
        EditText et = (EditText)findViewById(R.id.edit_motors_name);
        et.setText(name);
        et.setSelection(et.getText().length());


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSectionAttached(int number) {

    }

    @Override
    public boolean sendMsg(String message) {
        return false;
    }

    @Override
    public void resetAllMotors() {

    }

    @Override
    public int getMotorCount() {
        return 0;
    }

    @Override
    public void adjustAllMotorToZero() {

    }

    @Override
    public boolean liftMotorUp(int index, float roations) {
        return false;
    }

    @Override
    public boolean liftMotorDown(int index, float rotations) {
        return false;
    }

    @Override
    public boolean moveMotorToPos(int index, float position) {
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
                    return EditMotorsFragment.newInstance(0,0,0,0,0);
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
