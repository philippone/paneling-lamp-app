package net.philippschardt.panelinglamp;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import database.PanelingLampContract;
import database.PanelingLampDBHelper;
import fragments.EditFragments.EditLEDFragment;
import fragments.EditFragments.EditMotorsFragment;
import fragments.OnFragmentInteractionListener;
import fragments.OnReceiverListener;
import fragments.forms.OnHandleMessageListener;
import util.AddNewFormDialog;
import util.LedItemView;
import util.Motor;
import util.MotorItemView;
import util.MsgCreator;
import util.UtilPL;

public class EditFormActivity extends ActionBarActivity implements OnFragmentInteractionListener, OnHandleMessageListener {


    public static final String EXTRA_ID = "editFormActivy_extra_ID";
    public static final String EXTRA_IS_STANDARD = "editFormActivy_extra_is_standard";
    private final String TAG = getClass().getName();

    public static final String EXTRA_NAME = "editFormActivy_extra_name";
    public static final String EXTRA_THUMBNAIL = "editFormActivy_extra_thumbnail";
    public static final String EXTRA_m = "editFormActivy_extra_m";
    public static final String EXTRA_l = "editFormActivy_extra_l";



    // init motors
    private Motor[] motor;
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private ViewPagerAdapter pagerAdapter;
    private EditMotorsFragment mEditMotorsFragment;
    private EditLEDFragment mEditLEDFragment;
    private float[] motorV;
    private int[] ledV;
    private long cardID;
    private EditText editName;
    private PanelingLampDBHelper mDbHelper;
    private ImageView thumbView;
    private String thumb;
    private boolean isStandard;
    private FloatingActionButton saveButton;
    private FloatingActionButton moveToButton;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_form);


        // set an exit transition
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        mDbHelper = new PanelingLampDBHelper(this);

        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.inflateMenu(R.menu.menu_card_options_fav);
        //toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.editformTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().men




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

            cardID = dataIntent.getLongExtra(EXTRA_ID, -1);

            String name = dataIntent.getStringExtra(EXTRA_NAME);

            // set values
            editName = (EditText) findViewById(R.id.edit_motors_name);
            editName.setText(name);
            editName.setSelection(editName.getText().length());


            // set Thumbnail
            thumb = dataIntent.getStringExtra(EXTRA_THUMBNAIL);
            thumbView = (ImageView) findViewById(R.id.edit_form_thumbnail);

            UtilPL.setPic(thumbView, thumb);

            setEnterSharedElementCallback(new android.support.v4.app.SharedElementCallback() {
                @Override
                public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                    super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                    editName.setVisibility(View.VISIBLE);

                    revealView(saveButton);
                    revealView(moveToButton);


                }


            });

            isStandard = dataIntent.getBooleanExtra(EXTRA_IS_STANDARD, true);

            if (isStandard) {
                thumbView.setImageDrawable(getResources().getDrawable(Integer.parseInt(thumb)));
            } else {
                // TODO
            }
            // get motor and led valus to pass to fragments
            motorV = dataIntent.getFloatArrayExtra(EXTRA_m);
            ledV = dataIntent.getIntArrayExtra(EXTRA_l);

        }

        initModel();
        // Fragments
        mEditMotorsFragment = EditMotorsFragment.newInstance(motorV);
        mEditLEDFragment = EditLEDFragment.newInstance(ledV);



        thumbView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // save button
         saveButton = (FloatingActionButton) findViewById(R.id.edit_motors_floating_buttn_save);
         moveToButton = (FloatingActionButton) findViewById(R.id.edit_motors_floating_buttn_moveToForm);

        saveButton.setVisibility(View.GONE);
        moveToButton.setVisibility(View.GONE);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get new values
                String name = editName.getText().toString();
                ArrayList<MotorItemView> m = mEditMotorsFragment.getMotorItem();
                ArrayList<LedItemView> l = mEditLEDFragment.getLedItem();



                float[] mV = new float[]{m.get(0).getmPos(),m.get(1).getmPos(),m.get(2).getmPos(),m.get(3).getmPos(),m.get(4).getmPos()};
                int[] lV = new int[] {l.get(0).getValue(),l.get(1).getValue(),l.get(2).getValue(),l.get(3).getValue()};

                //update db
                PanelingLampContract.updateCardMotorLED(mDbHelper.getWritableDatabase(), cardID, name, thumb, mV, lV);
            }
        });

        moveToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditMotorsFragment.activateProgress(0, 1, 2, 3, 4);
                sendMsg(MsgCreator.moveToForm(-1, mEditMotorsFragment.getMotorItem(), mEditLEDFragment.getLedItem()));
            }
        });

    }




    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void revealView(View myView) {
        // get the center for the clipping circle
        int cx = (myView.getLeft() + myView.getRight()) / 2;
        int cy = (myView.getTop() + myView.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        anim.start();
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
    public void updateAdatpers() {
        // nothing do do
    }

    @Override
    public PanelingLampDBHelper getDBHelper() {

        // TODO
        return null;
    }


    static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void showAddNewFormDialog(float[] motorV, int[] ledV) {
        DialogFragment newFragment = new AddNewFormDialog();
        newFragment.show(getSupportFragmentManager(), "newformdialog");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            thumbView.setImageBitmap(imageBitmap);
        }
    }


}
