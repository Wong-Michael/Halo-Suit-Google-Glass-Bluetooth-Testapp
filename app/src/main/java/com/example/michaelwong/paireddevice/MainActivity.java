package com.example.michaelwong.paireddevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private String TAG = "TAG";
    private TextView text1;
    private Thread ConnectThread;
    private Thread ManageThread;
    ManageThread mManageThread;
    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    private BluetoothDevice mmDevice;
    private  BluetoothSocket mmSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;

    private static final UUID insecureUUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private  class ManageThread extends Thread {

        private byte[] mmBuffer;
        public ManageThread (){
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = mmSocket.getInputStream();
            } catch (IOException e) {
                Log.i(TAG, "Error occurred when creating input stream");
            }
            Log.i(TAG, "tmpIn try block done");
            try {
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                Log.i(TAG, "Error occurred when creating output stream");
            }
            Log.i(TAG, "tmpOut try block done");
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

    }
    private class ConnectThread extends Thread {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        public ConnectThread() {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    mmDevice = device;
                    Log.i(TAG, "Glass device: " + device.getName() + " initialized");
                }
            }
            Log.i(TAG, "Trying to connect to Glass");
            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = mmDevice.createRfcommSocketToServiceRecord(insecureUUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            Log.i(TAG, "Connected to Glass");
            mmSocket = tmp;
            Log.i(TAG, "Connected Glass to mmSocket");
        }

        public void  run() {
            Log.i(TAG, "Running connect thread");

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                Log.i(TAG, "Running connect()");
                mmSocket.connect();
            } catch (IOException connectException) {
                Log.i(TAG, "Running connect() failed");

                // Unable to connect; close the socket and return.
            }
            Log.i(TAG, "Running connect() completed with" + mmSocket.isConnected());
            ManageThread = new ManageThread();
            ManageThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        text1 = (TextView) findViewById(R.id.pairedID);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String test = "{\"head temperature\":\"30\"," +
                        "\"armpits temperature\":\"30\"," +
                        "\"crotch temperature\":\"30\"}";
                try {
                    mmOutStream.write(test.getBytes());
                } catch (IOException e) {
                    Log.i(TAG, "Error occurred when sending data");
                }
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        String pairedList = "PairedList: \n";
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        ConnectThread = new ConnectThread();
        ConnectThread.start();


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
}
