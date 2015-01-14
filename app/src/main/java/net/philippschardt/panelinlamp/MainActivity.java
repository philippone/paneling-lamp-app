package net.philippschardt.panelinlamp;

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
import android.os.ParcelUuid;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

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


public class MainActivity extends ActionBarActivity {


    /*Bluetooth stuff*/
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "MainActivity";
    short currentServo = 1;
    float currRotation = 1;
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


    private int dimmerProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        SeekBar dimmer = (SeekBar) findViewById(R.id.dimmer);

        dimmer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                dimmerProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                sendMsg("a");//dimmerProgress + "");

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        setBTConnection();
        // register Reciever
        //TODO aus der anderen methode nehmen
    }

    @Override
    protected void onStop() {
        super.onStop();

        // unregsiter Receiver
        unregisterReceiver(devicePairingReceiver);
        unregisterReceiver(deviceFoundReceiver);
        unregisterReceiver(devicePairedReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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




    public void onRadioButtonServoClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_servo1:
                if (checked)
                    currentServo = 1;
                    break;
            case R.id.radio_servo2:
                if (checked)
                    currentServo = 2;
                break;
            case R.id.radio_servo3:
                if (checked)
                    currentServo = 3;
                break;
            case R.id.radio_servo4:
                if (checked)
                    currentServo = 4;
                break;
            case R.id.radio_servo5:
                if (checked)
                    currentServo = 5;
                break;

        }
    }


    public void onRadioButtonRotationClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton_1R:
                if (checked)
                    currRotation = 1;
                break;
            case R.id.radioButton_1_2r:
                if (checked)
                    currRotation = 0.5f;
                break;
        }
    }

    public void togglePower (View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            // lamp is on
            // TODO send command to arduino
        } else {
            // lamp is off
            // TODO send command to arduino
        }
    }


    public void controlUPDOWN(View view) {
        switch(view.getId()) {
            case R.id.button_down:
                //TODO send command to arduino servo
                break;
            case R.id.button_up:
                //TODO send command to arduino servo
                break;
        }


        // TODO only test
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {

            try {
                /*OutputStreamWriter osw = new OutputStreamWriter(bluetoothSocket.getOutputStream(), getString(R.string.arduino_bt_encoding));
                osw.write("testtextwithaverylonglength\n");
                osw.flush();
                */

                String test = "check123\n";
                //bluetoothSocket.getOutputStream().write(new byte[]{(byte) 0xFF,(byte) 0xFF, (byte) 0x0a, (byte) 0x0b, 0x00 , 0x00, 0x13, 0x7a});
                bluetoothSocket.getOutputStream().write(test.getBytes());
                bluetoothSocket.getOutputStream().flush();


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



    public void sendMsg(String msg) {

        Log.d("BluetoothTest", "send Msg " + msg);

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
                Log.d("BluetoothTest", "Msg " + msg +  " was sent");


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


   /*
   * Bluetooth stuff
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
                        logData(foundDevice);

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
                        logData(pairedDevice);
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

        ArduinoReceiverThread art = null;
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

    private void logData(BluetoothDevice btd) {

        Log.d(TAG, " Device Data ");
        Log.d(TAG, " Device Name: " + btd.getName());
        Log.d(TAG, " Device Address: " + btd.getAddress());

        ParcelUuid[] uuids = btd.getUuids();
        if (uuids != null) {
            for (ParcelUuid uuid : uuids) {
                Log.d(TAG, " UUID: " + uuid.getUuid());
            }
        }
        Log.d(TAG, " Device bond state: " + btd.getBondState());

    }


    private void logBTInput(String text) {

        if (btConsleView == null) {
            btConsleView = (TextView) findViewById(R.id.BTConsole);
        }

        btConsleView.append(text + "\n");


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

                                logBTInput(message);

                            }
                        });


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                    Log.e(TAG, "Bluetooth disconnect");

                    //Restart Connection

                    break;

                }

            }

        }
    }

}
