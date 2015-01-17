package net.philippschardt.panelinglamp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Util.Motor;
import Util.MsgCreator;
import fragments.DeveloperFragment;
import fragments.FormsFragment;
import fragments.OnFragmentInteractionListener;
import fragments.OnReceiverListener;


public class PanelingLamp extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        OnFragmentInteractionListener {


    private String TAG = getClass().getName();

    /*Bluetooth stuff*/
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ExecutorService es = Executors.newFixedThreadPool(1);
    private BluetoothManager bluetoothManager = null;
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothDevice bluetoothDevice = null;
    private BluetoothSocket bluetoothSocket = null;
    private OutputStream bluetoothOutputStream = null;
    private InputStream bluetoothInputStream = null;
    private boolean paired = false;
    private boolean connected = false;
    private TextView btConsleView;
    private BroadcastReceiver deviceFoundReceiver;
    private BroadcastReceiver devicePairingReceiver;
    private BroadcastReceiver devicePairedReceiver;
    ArduinoReceiverThread art = null;


    // init motors
    private Motor[] motor;

    private Fragment currentFragment = null;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paneling_lamp);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));



        initModel();


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
    protected void onStart() {
        super.onStart();

        // register Reciever and open connection
        setBTConnection();


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        // send startMessage
        sendMsg(MsgCreator.initConnection());
    }

    @Override
    protected void onStop() {
        super.onStop();
/*
        // unregsiter Receiver
        unregisterReceiver(devicePairingReceiver);
        unregisterReceiver(deviceFoundReceiver);
        unregisterReceiver(devicePairedReceiver);

        try {
            art.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
  */
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
                currentFragment = DeveloperFragment.newInstance(position + 1);
                break;
            // TODO
            default:
                currentFragment = PlaceholderFragment.newInstance(position + 1);
                break;

        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, currentFragment)
                .commit();

    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.paneling_lamp, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



 

    /**
    * OnFragmentInterfaceListener
    *
    * */
    @Override
    public boolean sendMsg(String msg) {
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {

            if (!msg.endsWith("\n")) {
                msg += "\n";
            }


            try {
                /*OutputStreamWriter osw = new OutputStreamWriter(bluetoothSocket.getOutputStream(), getString(R.string.arduino_bt_encoding));
                osw.write("testtextwithaverylonglength\n");
                osw.flush();
                */

                //bluetoothSocket.getOutputStream().write(new byte[]{(byte) 0xFF,(byte) 0xFF, (byte) 0x0a, (byte) 0x0b, 0x00 , 0x00, 0x13, 0x7a});
                bluetoothSocket.getOutputStream().write(msg.getBytes());
                bluetoothSocket.getOutputStream().flush();
                //Log.d("BluetoothTest", "Msg " + msg +  " was sent");
                return true;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public void onSectionAttached(int number) {
        Log.d(TAG, "section attached " + number);
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
        }
    }


    public void resetAllMotors() {
        // TODO test if motors are running
        for(int i = 0; i < motor.length; i++) {
            sendMsg(MsgCreator.forceReset(i));

        }
    }

    public int getMotorCount() {
        return motor.length;
    }


    public void adjustAllMotorToZero() {
        for(int i = 0; i < motor.length; i++) {
            sendMsg(MsgCreator.overridePos(i, 0));
        }
    }


    public boolean liftMotorUp(int index, float rotations) {
        return sendMsg(MsgCreator.move(index, rotations));
    }

    public boolean liftMotorDown(int index, float rotations){
        return sendMsg(MsgCreator.move(index, -rotations));

    }

    public boolean moveMotorToPos(int index, float position) {
        return sendMsg(MsgCreator.moveTo(index, position));
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
            ((PanelingLamp) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }




   /*
   * Bluetooth stuff
   *
   * **/

    private void setBTConnection() {
        boolean foundFlag = false;

        try {
            // All receivers here
            IntentFilter discoveryFilter = new IntentFilter();
            discoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);


            /*
            * device found Reciever
            * @called when device is found then bond to the BT Module
            * */
            deviceFoundReceiver = new BroadcastReceiver() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onReceive(Context context, Intent intent) {

                    Log.d(TAG, "Action found recevied with " + intent.getExtras().size() + " extras");

                    try {

                        BluetoothDevice foundDevice = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);
                        //  intent.getExtras().getParcelable(BluetoothDevice.EXTRA_CLASS);

                        Log.d(TAG, "BT Module found");
                        //logData(foundDevice);

                        if (checkBTModule(foundDevice)) {

                            bluetoothDevice = foundDevice;
                            //bluetoothDevice.setPin(new byte[]{0x01,0x02,0x03,0x04});
                            boolean bond = foundDevice.createBond();
                            //bluetoothDevice.setPairingConfirmation(true);

                            if (!bond) {
                                Log.e(TAG, "Bonding to device" + foundDevice.getName() +
                                        " failed");
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };

            IntentFilter pairFilter = new IntentFilter();
            pairFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);


            /*
            * Device Parired Receiver
            *
            * @called if new device was paired
            * */
            devicePairedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    BluetoothDevice pairedDevice = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);
                    int newState = intent.getExtras().getInt(BluetoothDevice.EXTRA_BOND_STATE);

                    Log.d(TAG,"state change");
                    //logData(pairedDevice);

                    if (checkBTModule(pairedDevice)) {

                        paired = (newState == BluetoothDevice.BOND_BONDED);
                        Log.d(TAG, "Pairing successful " + newState + " state " + BluetoothDevice.BOND_BONDED);
                        //logData(pairedDevice);
                        try {
                            BluetoothDevice.class.getDeclaredMethod("cancelPairingUserInput", null).invoke(pairedDevice);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }

                    }

                }
            };

            IntentFilter pairingFilter = new IntentFilter();
            pairingFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);

            /*
            * Device Parining Receiver
            *
            * enter pin
            * */
            devicePairingReceiver = new BroadcastReceiver() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onReceive(Context context, Intent intent) {

                    Log.d(TAG, "Pairing running");
                    BluetoothDevice device = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);

                    Log.d(TAG, "Pairing running with device " + device.getName() + " status " + device.getBondState());

                    if (checkBTModule(device)) {

                        Log.d(TAG, "Pairing still running");

                        try {
                            Log.d(TAG, "getting pin");


                            byte[] convertPinToBytes = (byte[]) BluetoothDevice.class.getMethod("convertPinToBytes", String.class).invoke(device, "1234");
                            device.setPin(convertPinToBytes);
                            device.setPairingConfirmation(true);

                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }

                    }

                }
            };

            registerReceiver(devicePairingReceiver, pairingFilter);
            registerReceiver(deviceFoundReceiver, discoveryFilter);
            registerReceiver(devicePairedReceiver, pairFilter);




            //Log.d(TAG,getSystemService(BLUETOOTH_SERVICE).getClass().getName());
            bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

            if (Build.VERSION.SDK_INT < 18) {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            } else {
                bluetoothAdapter = bluetoothManager.getAdapter();
            }

            bluetoothAdapter.enable();
            Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

            for (BluetoothDevice foundBluetoothDevice : bondedDevices) {

                Log.d(TAG, "bonded device " + foundBluetoothDevice.getName());
                if (checkBTModule(foundBluetoothDevice)) {

                    // BT Adapter Found;
                    Log.d(TAG, "Module found wohoo");
                    paired = (foundBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED);
                    foundFlag = true;
                    bluetoothDevice = foundBluetoothDevice;

                    break;

                }

            }

            if (!foundFlag) {

                bluetoothAdapter.startDiscovery();

            }

            if (paired) {
                openBTConnection();
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }


    private void openBTConnection() {

        if (bluetoothDevice != null && paired && bluetoothAdapter.isEnabled()) {
            try {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothSocket.connect();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Log.e(TAG, "Not ready to connect");
        }

        art = null;
        try {
            art = new ArduinoReceiverThread(bluetoothSocket, this);
            es.execute(art);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private boolean checkBTModule(BluetoothDevice btd) {
        if (btd != null) {
            return btd.getName().equals(getString(R.string.arduino_bt_module_name));
        } else {
            return btd.getAddress().equals(getString(R.string.arduino_bt_module_adress));
        }
    }






    /**
     * Receiver Thread
     *
     * */
    private class ArduinoReceiverThread implements Runnable {

        private BluetoothSocket socket;
        private InputStream in;
        private Activity ui;
        private BufferedReader br;
        private InputStreamReader isr;

        ArduinoReceiverThread(BluetoothSocket socket, Activity ui) throws IOException {

            this.ui = ui;
            this.socket = socket;
            if (socket.isConnected()) {
                this.in = socket.getInputStream();
            } else {

                throw new IOException("Unconnected Socket received in constuctor");

            }
            isr = new InputStreamReader(in, getString(R.string.arduino_bt_encoding));
            br = new BufferedReader(isr);

        }

        @Override
        public void run() {

            String line = "";
            while (socket.isConnected()){

                try {
                    while ((line = br.readLine()) != null){

                        final String message = line;
                        ui.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                handleInput(message);
                                //logBTInput(message);
                            }
                        });


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                    Log.e(TAG, "Bluetooth disconnect");

                    //close socket
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;

                }

            }

        }
    }



    /**
     * handle inputs from arduino/lamp
     * */
    public void handleInput(String message) {

        Log.d("test", message);
        if (message.startsWith("ms;")) {
            handleMotorUpdate(message);
        } else if (message.startsWith("r;"))
            handleConnectionReply(message);
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

    /*
    * handle motor update message
    * receive: "ms;motor index; motor position"
    *
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

}
