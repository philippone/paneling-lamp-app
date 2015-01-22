package net.philippschardt.panelinglamp;

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
import android.util.Log;
import android.widget.EditText;

import com.astuetz.PagerSlidingTabStrip;

import Util.Motor;
import database.PanelingLampDBHelper;
import fragments.EditFragments.EditLEDFragment;
import fragments.EditFragments.EditMotorsFragment;
import fragments.OnFragmentInteractionListener;
import fragments.OnReceiverListener;
import fragments.forms.OnHandleMessageListener;

public class EditFormActivity extends ActionBarActivity implements OnFragmentInteractionListener, OnHandleMessageListener {


    private final String TAG = getClass().getName();
    public static final String EXTRA_CARD = "editFormActivy_extra_card";
    public static final String EXTRA_NAME = "editFormActivy_extra_name";


    // init motors
    private Motor[] motor;
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private ViewPagerAdapter pagerAdapter;
    private Fragment mEditMotorsFragment;
    private EditLEDFragment mEditLEDFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_form);


        initModel();
        // Fragments
        mEditMotorsFragment = EditMotorsFragment.newInstance(0, 0, 0, 0, 0);
        mEditLEDFragment = EditLEDFragment.newInstance(0,0,0,0);

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

    private void initModel() {
        // init motor
        motor = new Motor[] {
                new Motor(0),
                new Motor(1),
                new Motor(2),
                new Motor(3),
                new Motor(4)};
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
        Log.d(TAG, "handleInput " + message);
        if (message.startsWith("ms;")) {
            handleMotorUpdate(message);
        } else if (message.startsWith("r;"))
            handleConnectionReply(message);
        else if (message.startsWith("mfr;"))
            handleMoveToFormReply(message);
    }

    private void handleMotorUpdate(String message) {
        String[] splitted = message.split(";");

        int motorNr = Integer.parseInt(splitted[1]);
        float motorPos = Float.parseFloat(splitted[2]);

        updateMotorPosInGUI(motorNr, motorPos);
    }

    private void updateMotorPosInGUI(int motorNr, float motorPos) {
        ((OnReceiverListener) mEditMotorsFragment).updateMotorPosinGUI(motorNr, motorPos);
    }

    /**
     * handle Connection Reply message
     * "r;motor1 position; motor 2 position; ...; motor n position"
     * */
    private void handleConnectionReply(String message) {
        String[] splitted = message.substring(2).split(";");

        for(int i = 0; i < motor.length; i++) {
            // update motor (and gui)
            float p = Float.parseFloat(splitted[i]);

            // update GUI
            updateMotorPosInGUI(i, p);
        }
    }


    /**
     * move form reply
     * " mfr; formID; "
     *
     * */
    private void handleMoveToFormReply(String message) {
        // TODO nothing to do
        /*String[] splitted = message.split(";");

        long id = Long.parseLong(splitted[1]);

        // update DB
        setFormActiveInDB(id);

        // notify view
        ((OnReceiverListener)currentFragment).updateActiveStatus(id);
*/
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
    public boolean sendMsg(String msg) {
        Intent i = new Intent(this, MySocketService.class);
        i.putExtra(MySocketService.EXTRA_MESSAGE, msg);
        if (startService(i) != null)
            return true;
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

        // TODO
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
                case 1:
                    return mEditLEDFragment;
                default:
                    return null;
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


}
