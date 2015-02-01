package net.philippschardt.panelinglamp;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    public static final String EXTRA_IS_NEW_FORM =  "editFormActivy_extra_is_new_form";;
    public static final String EXTRA_NAME = "editFormActivy_extra_name";
    public static final String EXTRA_THUMBNAIL = "editFormActivy_extra_thumbnail";
    public static final String EXTRA_m = "editFormActivy_extra_m";
    public static final String EXTRA_l = "editFormActivy_extra_l";
    static final int REQUEST_IMAGE_CAPTURE_EDIT = 2;
    private final String TAG = getClass().getName();
    String newFormThumbPath;
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
    private PagerSlidingTabStrip mTabStrip;
    private File photoFile;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                String msg = intent.getStringExtra(MySocketService.EXTRA_MESSAGE_FORWARD);
                handleInput(msg);
            }
        }
    };
    private float lastTopValueAssigned = -1;
    private boolean newThumb = false;
    private boolean takePhotoAndSaveNewShape;
    private float[] mV;
    private int[] lV;
    private String name;

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
        mTabStrip = (PagerSlidingTabStrip) findViewById(R.id.edit_shape_tabs);
        mViewPager = (ViewPager) findViewById(R.id.edit_form_viewPager);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.edit_shape_tabs);
        tabs.setViewPager(mViewPager);

        editName = (EditText) findViewById(R.id.edit_motors_name);
        thumbView = (ImageView) findViewById(R.id.edit_form_thumbnail);

        // receive values
        Intent dataIntent = getIntent();
        if (dataIntent != null) {

            boolean createNewForm = dataIntent.getBooleanExtra(EXTRA_IS_NEW_FORM, false);

            if (createNewForm) {
                getSupportActionBar().setTitle(R.string.add_new_form);

                motorV = new float[PanelingLamp.MOTOR_NUMBER];
                ledV = new int[PanelingLamp.LED_NUMBER];

            } else {

                cardID = dataIntent.getLongExtra(EXTRA_ID, -1);
                String name = dataIntent.getStringExtra(EXTRA_NAME);

                // set values

                editName.setText(name);
                editName.setSelection(editName.getText().length());


                // set Thumbnail
                thumb = dataIntent.getStringExtra(EXTRA_THUMBNAIL);


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
                name = editName.getText().toString();
                ArrayList<MotorItemView> m = mEditMotorsFragment.getMotorItem();
                ArrayList<LedItemView> l = mEditLEDFragment.getLedItem();




                 mV = new float[]{m.get(0).getmPos(),m.get(1).getmPos(),m.get(2).getmPos(),m.get(3).getmPos(),m.get(4).getmPos()};
                lV = new int[] {l.get(0).getValue(),l.get(1).getValue(),l.get(2).getValue(),l.get(3).getValue(), l.get(4).getValue(),l.get(5).getValue(),l.get(6).getValue()};

                // if is Standard create a new shape in as own
                if (isStandard ) {
                    if (newThumb)
                        PanelingLampContract.saveOwnForm(mDbHelper.getWritableDatabase(), name, newFormThumbPath, mV, lV);
                    else {
                        dispatchTakePictureIntent();
                        takePhotoAndSaveNewShape = true;
                    }
                } else {
                    // update Shape
                    if (newThumb) {
                        PanelingLampContract.updateCardMotorLED(mDbHelper.getWritableDatabase(), cardID, name, newFormThumbPath, mV, lV);

                    } else {
                        //update db
                        PanelingLampContract.updateCardMotorLED(mDbHelper.getWritableDatabase(), cardID, name, thumb, mV, lV);
                    }
                }
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

    @Override
    public void showAddNewFormDialog(float[] motorV, int[] ledV) {
        DialogFragment newFragment = new AddNewFormDialog();
        newFragment.show(getSupportFragmentManager(), "newformdialog");
    }

    @Override
    public void onScrollUp(int l, int t, int x, int y, int scrollX, int scrollY) {
        /*

            mTabStrip.setTranslationY(-scrollY * 0.5f);
            editName.setTranslationY(-scrollY * 0.5f);
            moveToButton.setTranslationY(-scrollY * 0.5f);
            saveButton.setTranslationY(-scrollY * 0.5f);
            thumbView.setTranslationY(-scrollY * 0.5f);
            mViewPager.setTranslationY(-scrollY * 0.5f);


*/
    }

    @Override
    public void onScrollDown(int l, int t, int x, int y, int scrollX, int scrollY) {
        /*

        mTabStrip.setTranslationY(-scrollY * 0.5f);
        editName.setTranslationY(-scrollY * 0.5f);
        moveToButton.setTranslationY(-scrollY * 0.5f);
        saveButton.setTranslationY(-scrollY * 0.5f);
        thumbView.setTranslationY(-scrollY * 0.5f);
        mViewPager.setTranslationY(-scrollY * 0.5f);
        */
    }

    @Override
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_EDIT);



            } else {
                Log.d(TAG, "photo == null");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult " + resultCode);


        if (requestCode == REQUEST_IMAGE_CAPTURE_EDIT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

            // save new in DB form
            //PanelingLampContract.updateThumbnail(mDbHelper.getWritableDatabase(),cardID, newFormThumbPath);
            // done in onClick of saveButton

                // if standard shape as template
                // then you have to take a new photo to save
                // the new shape
                if (takePhotoAndSaveNewShape) {
                    PanelingLampContract.saveOwnForm(mDbHelper.getWritableDatabase(), name, newFormThumbPath, mV, lV);
                }

            /**
             * scale image down and overwrite
             * */
            // Get the dimensions of the View
            int targetW = 500;//mImageView.getWidth();
            int targetH = 500;//mImageView.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(newFormThumbPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            Log.d(TAG, "file " + photoW + " x " + photoH);

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            Bitmap bitmap = BitmapFactory.decodeFile(newFormThumbPath, bmOptions);
            thumbView.setImageBitmap(bitmap);

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(photoFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            newThumb = true;

        }
        }
        photoFile = null;

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = Environment.getExternalStoragePublicDirectory(
        //      Environment.DIRECTORY_PICTURES);
        File storageDir = getExternalFilesDir("img");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents "file:" +
        newFormThumbPath =  image.getAbsolutePath();
        return image;
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
