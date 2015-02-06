package net.philippschardt.panelinglamp;

import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MySocketService extends Service {

    public static final String EXTRA_MESSAGE = "MySocketService_EXTRA_MSG";
    public static final String EXTRA_MESSAGE_FORWARD = "MySocketService_EXTRA_MSG_forward";
    public static final String BROADCAST_ACTION = "MySocketService_BRODCAST_forward_msg";
    public static final String EXTRA_RESTART = "MySocketService_RESTART";
    private final String TAG = getClass().getName();
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private boolean paired;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ArduinoReceiverThread art;
    private ExecutorService es = Executors.newFixedThreadPool(1);
    private NotificationManager mNotifyMgr;
    private int mNotificationId;
    private Thread bluetoothThred;

    public MySocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /*NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.form)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText("Hello World!")
                .setOngoing(true);



        // Sets an ID for the notification
        mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.

        mNotifyMgr.notify(mNotificationId, mBuilder.build());



        Intent notificationIntent = new Intent(this, PanelingLamp.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("Hi")
                .setContentIntent(pendingIntent).build();

        startForeground(mNotificationId,  notification);

      */

        bluetoothThred = new Thread(new Runnable() {
            @Override
            public void run() {
                setBTConnection();
            }
        });

        bluetoothThred.run();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int i = super.onStartCommand(intent, flags, startId);

        if (paired) {
            if (intent != null && intent.hasExtra(EXTRA_MESSAGE)) {
                boolean sent = sendMsg(intent.getStringExtra(EXTRA_MESSAGE));
                // TODO notify activity
                // reconnect, resent?
            }
        }


        return i;
    }



    public boolean sendMsg(String msg) {
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {

            if (!msg.endsWith("\n")) {
                msg += "\n";
            }


            try {
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

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service destroyed");
        super.onDestroy();
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //stopForeground(true);
        //mNotifyMgr.cancel(mNotificationId);
    }

    /*
   * Bluetooth stuff
   *
   * **/

    private void setBTConnection() {
        boolean foundFlag = false;

        try {

            if (Build.VERSION.SDK_INT > 18) {
                //Log.d(TAG,getSystemService(BLUETOOTH_SERVICE).getClass().getName());
                bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
            }
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
            art = new ArduinoReceiverThread(bluetoothSocket);
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
     */
    private class ArduinoReceiverThread implements Runnable {

        private BluetoothSocket socket;
        private InputStream in;

        private BufferedReader br;
        private InputStreamReader isr;

        ArduinoReceiverThread(BluetoothSocket socket) throws IOException {

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
            while (socket.isConnected()) {

                try {
                    while ((line = br.readLine()) != null) {

                        final String message = line;
                        Log.d(TAG, "message received: " + message);

                        if (message != "") {
                            notifyGUI(message);
                        }
                        /*ui.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                handleInput(message);
                                //logBTInput(message);
                            }
                        });
*/
                        // TODO send Broadcast message to Activity

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

    private void notifyGUI(String message) {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(EXTRA_MESSAGE_FORWARD, message);
        sendBroadcast(intent);
    }


}
