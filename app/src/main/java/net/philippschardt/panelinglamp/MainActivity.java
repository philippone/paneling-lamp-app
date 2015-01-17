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
import android.os.ParcelUuid;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Util.Motor;
import Util.MsgCreator;


public class MainActivity extends ActionBarActivity implements Motor.OnMotorInterfaceListener {

    private static final String TAG = "MainActivity";


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

    // View stuff
    private EditText rotationEdit;
    private EditText absPosEdit;


    // motor stuff
    private Motor[] motor;
    private ArrayList<TextView> motorPosView;
    private ArrayList<ProgressBar> motorProgView;
    short currentMotor = 0;
    float currRotation = 1;
    private int dimmerProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init motors
        motor = new Motor[] {new Motor(0, this), new Motor(1, this), new Motor(2, this), new Motor(3, this), new Motor(4, this)};
        motorPosView = new ArrayList<TextView>(Arrays.asList(
                (TextView) findViewById(R.id.textView_motor1_position),
                (TextView) findViewById(R.id.textView_motor2_position),
                (TextView) findViewById(R.id.textView_motor3_position),
                (TextView) findViewById(R.id.textView_motor4_position),
                (TextView) findViewById(R.id.textView_motor5_position)
        ));

        motorProgView = new ArrayList<ProgressBar>(Arrays.asList(
                (ProgressBar) findViewById(R.id.progressBar_motor1),
                (ProgressBar) findViewById(R.id.progressBar_motor2),
                (ProgressBar) findViewById(R.id.progressBar_motor3),
                (ProgressBar) findViewById(R.id.progressBar_motor4),
                (ProgressBar) findViewById(R.id.progressBar_motor5)
                ));

        // todo get saved positions

        // view
        rotationEdit = (EditText) findViewById(R.id.editText_rel_rot);
        absPosEdit = (EditText) findViewById(R.id.editText_rot_abs);

        SeekBar dimmer = (SeekBar) findViewById(R.id.dimmer);

        dimmer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // set new dimmer value
                dimmerProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { /* nothing*/}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // if more than 1 LED dimmer use seekBar.getID() to get LED ID;

                // send dimmer value to lamp
                sendMsg(MsgCreator.setLED(0, dimmerProgress));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        // register Reciever and open connection
        setBTConnection();

        //TODO aus der anderen methode nehmen

        // send startMessage
        sendMsg(MsgCreator.initConnection());
    }



    @Override
    protected void onStop() {
        super.onStop();

        // unregsiter Receiver
        unregisterReceiver(devicePairingReceiver);
        unregisterReceiver(deviceFoundReceiver);
        unregisterReceiver(devicePairedReceiver);

        try {
            art.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            case R.id.radio_motor1:
                if (checked)
                    currentMotor = 0;
                    break;
            case R.id.radio_motor2:
                if (checked)
                    currentMotor = 1;
                break;
            case R.id.radio_motor3:
                if (checked)
                    currentMotor = 2;
                break;
            case R.id.radio_motor4:
                if (checked)
                    currentMotor = 3;
                break;
            case R.id.radio_motor5:
                if (checked)
                    currentMotor = 4;
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

        String value = rotationEdit.getText().toString();
        if (value != "") {
            currRotation = Float.parseFloat(value);
        } else {
            // do nothing
            return;
        }

        switch(view.getId()) {
            case R.id.button_down:
                //send position to arduino
                boolean b = sendMsg(MsgCreator.move(currentMotor, -currRotation));
                if (b) {
                    motorPosView.get(currentMotor).setVisibility(View.GONE);
                    motorProgView.get(currentMotor).setVisibility(View.VISIBLE);
                }
                break;
            case R.id.button_up:
                //send position to arduino
                boolean b1 = sendMsg(MsgCreator.move(currentMotor, currRotation));
                if (b1) {
                    motorPosView.get(currentMotor).setVisibility(View.GONE);
                    motorProgView.get(currentMotor).setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    public void resetMotors(View v) {
        // TODO test if motors are running
        for(int i = 0; i < motor.length; i++) {
            sendMsg(MsgCreator.forceReset(i));
            motorPosView.get(i).setVisibility(View.GONE);
            motorProgView.get(i).setVisibility(View.VISIBLE);
        }
    }

    public void setZero(View v) {
        for(int i = 0; i < motor.length; i++) {
            sendMsg(MsgCreator.overridePos(i, 0));
        }
    }

    /**
     * set Button:
     * sets the given position to selected motor
     * */
    public void setPosition(View v) {
        float position = 0;
        String value = absPosEdit.getText().toString();
        if (value != "") {
            position = Float.parseFloat(value);
        } else {
            // do nothing
            return;
        }
        sendMsg(MsgCreator.moveTo(currentMotor, position));
        motorPosView.get(currentMotor).setVisibility(View.GONE);
        motorProgView.get(currentMotor).setVisibility(View.VISIBLE);

    }


    /*
    * send Msg to Arduino
    *
    * **/
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

        //btConsleView.append(text + "\n");
        btConsleView.setText("\" " + text + "\" \n" +  btConsleView.getText().toString() );

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
            motor[i].setPosition(Float.parseFloat(splitted[i]));
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
    }



    @Override
    public void notifyGuI(int motorNr, float position) {
        motorPosView.get(motorNr).setText(position + " rotations ");
        motorPosView.get(motorNr).setVisibility(View.VISIBLE);
        motorProgView.get(motorNr).setVisibility(View.INVISIBLE);
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
                                logBTInput(message);
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

}
