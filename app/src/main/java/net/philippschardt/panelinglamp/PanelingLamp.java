package net.philippschardt.panelinglamp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import database.PanelingLampContract;
import database.PanelingLampDBHelper;
import fragments.OnFragmentInteractionListener;
import fragments.OnReceiverListener;
import fragments.EditFragments.ManualControlFragment;
import fragments.forms.FormsFragment;
import fragments.forms.OnHandleMessageListener;
import fragments.settings.SettingsFragment;
import util.AddNewFormDialog;
import util.Motor;


public class PanelingLamp extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        OnFragmentInteractionListener, OnHandleMessageListener, AddNewFormDialog.OnAddNewFormDialogListener {


    public static final int MOTOR_NUMBER = 5;
    public static final int LED_NUMBER = 7;
    private String TAG = getClass().getName();



    // init motors
    private Motor[] motor;

    private Fragment currentFragment = null;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;
    private PanelingLampDBHelper mDbHelper;
    private String newFormName;
    private float[] newFormMotorV;
    private int[] newFormledV;
    private File photoFile;

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paneling_lamp);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),
                toolbar);

        initModel();


        mDbHelper = new PanelingLampDBHelper(this);


        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();



/*
        // Insert the new row, returning the primary key value of the new row
        db.insert(
                PanelingLampContract.FormEntry.TABLE_NAME,
                null,
                PanelingLampContract.FormEntry.createContentValues("Form 1" , R.drawable.paneling_lamp + "", 0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 255, 255, 0, 0,0,0, 0, false, true, 0, true, 0));

        // Insert the new row, returning the primary key value of the new row
        db.insert(
                PanelingLampContract.FormEntry.TABLE_NAME,
                null,
                PanelingLampContract.FormEntry.createContentValues("Form 2", R.drawable.paneling_lamp2 + "", 0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 255, 255, 0,0,0,0, 0, false, true, 1, true, 1));

         for (int i = 2; i < 10; i++) {
            // Insert the new row, returning the primary key value of the new row
            db.insert(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    null,
                    PanelingLampContract.FormEntry.createContentValues("Form " + i, R.drawable.paneling_lamp + "", 0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 255, 255, 0, 0, 0,0,0, false, false, i, true, 0));
        }

*/
        Intent i = new Intent(this, MySocketService.class);
        startService(i);
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
    protected void onResume() {
        super.onStart();

        // register Reciever
        registerReceiver(mMessageReceiver, new IntentFilter(MySocketService.BROADCAST_ACTION));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.paneling_lamp, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (position) {
            case 0:
                currentFragment = FormsFragment.newInstance(position + 1);
                break;
            case 1:
                currentFragment = ManualControlFragment.newInstance(position + 1);
                
                break;

            case 4:
                currentFragment = SettingsFragment.newInstance(position + 1);

                break;
            case 3:
            // TODO settings, about
            default:
                currentFragment = PlaceholderFragment.newInstance(position + 1);
                break;

        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, currentFragment)
                .commit();

    }





        /**
        * OnFragmentInterfaceListener
        *
        * */
    @Override
    public boolean sendMsg(String msg) {
        Log.d(TAG, "sent: " + msg);
        Intent i = new Intent(this, MySocketService.class);
        i.putExtra(MySocketService.EXTRA_MESSAGE, msg);
        if (startService(i) != null)
            return true;
        return false;
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
        }

        Log.d(TAG, "set Title" + mTitle);
        getSupportActionBar().setTitle(mTitle);
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
    public void updateAdatpers() {
        ((OnReceiverListener)currentFragment).notifyAdapters();
    }

    @Override
    public PanelingLampDBHelper getDBHelper() {
        return mDbHelper;
    }

    @Override
    public void confirmName(String name) {
         newFormName = name;
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
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





    /**
     * handle inputs from arduino/lamp
     * */

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


    /**
     * handle Connection Reply message
     * "r;motor1 position; motor 2 position; ...; motor n position"
     * */
    private void handleConnectionReply(String message) {
        String[] splitted = message.substring(2).split(";");

        for(int i = 0; i < motor.length; i++) {
            // update motor (and gui)
            float p = Float.parseFloat(splitted[i]);
            motor[i].setPosition(p);

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
        String[] splitted = message.split(";");

        long id = Long.parseLong(splitted[1]);

        if (id > 0) {
            // update DB
            setFormActiveInDB(id);

            // notify view
            // todo notify all listviews
            ((OnReceiverListener) currentFragment).notifyAdapters();

            // notify drawer
            mNavigationDrawerFragment.updateCurrentForm(mDbHelper.getReadableDatabase(), id);
        }
    }

    public void setFormActiveInDB(long formActiveInDB) {
        // set all to status 0
        // set active to 1
        int c = PanelingLampContract.updateStatus(mDbHelper.getWritableDatabase(), formActiveInDB , 1);

        Log.d(TAG, "changed " + c);
    }


    /**
    * handle motor update message
    * receive: "ms;motor index; motor position"
    * */
    private void handleMotorUpdate(String message) {
        String[] splitted = message.split(";");

        int motorNr = Integer.parseInt(splitted[1]);
        float motorPos = Float.parseFloat(splitted[2]);

        // update motor (and gui)
        motor[motorNr].setPosition(motorPos);

        updateMotorPosInGUI(motorNr, motorPos);
    }


    /**
     * update GUI
     *
     * */
    private void updateMotorPosInGUI(int motorNr, float motorPos) {
        ((OnReceiverListener)currentFragment).updateMotorPosinGUI(motorNr,  motorPos);
    }


    static final int REQUEST_IMAGE_CAPTURE = 1;
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
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);



            } else {
                Log.d(TAG, "photo == null");
            }
        }
    }

    @Override
    public void showAddNewFormDialog(float[] motorV, int[] ledV) {
        DialogFragment newFragment = new AddNewFormDialog();

        // save user values
        newFormMotorV = motorV;
        newFormledV = ledV;

        // show dialog to get name and picture
        newFragment.show(getSupportFragmentManager(), "newformdialog");
    }

    @Override
    public void onScrollUp(int l, int t, int x, int y, int dx, int dy) {
        OnReceiverListener frag = (OnReceiverListener) currentFragment;
        frag.onScrollUp();
    }

    @Override
    public void onScrollDown(int l, int t, int x, int y, int dx, int dy) {
        OnReceiverListener frag = (OnReceiverListener) currentFragment;
        frag.onScrollDown();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO if abort


        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // if image was taken
                Log.d(TAG, "onActivtyResutl " + newFormName);

                // save new in DB form
                PanelingLampContract.saveOwnForm(mDbHelper.getWritableDatabase(), newFormName, newFormThumbPath, newFormMotorV, newFormledV);


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


                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(photoFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                photoFile = null;

            }

        }
        photoFile = null;

    }

    String newFormThumbPath;

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


}
