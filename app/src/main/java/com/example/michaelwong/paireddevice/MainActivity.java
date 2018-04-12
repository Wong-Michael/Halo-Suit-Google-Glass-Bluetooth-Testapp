package com.example.michaelwong.paireddevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    private String TAG = "TAG";
    private Thread ConnectThread;
    private Thread ManageThread;
    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    private BluetoothDevice mmDevice;
    private  BluetoothSocket mmSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;

    private static final UUID insecureUUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private class ManageThread extends Thread {

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
                    if (device.getName().equals("Michael Wong's Glass"))
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
            Log.d("CONNECTIONBLUETOOTH" , String.valueOf(mmSocket.isConnected()));
            ManageThread = new ManageThread();
            ManageThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int REQUEST_ENABLE_BT = 1;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else {
            pairedDevices = mBluetoothAdapter.getBondedDevices();
            ConnectThread = new ConnectThread();
            ConnectThread.start();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button healthBar100 = (Button) findViewById(R.id.healthBar100);
        healthBar100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = "{\"8 AH battery\":\"100\"}";
                write(string);
                Toast.makeText(MainActivity.this, "is mSocketConnected? : " + String.valueOf(mmSocket.isConnected()), LENGTH_SHORT).show();
            }
        });
        Button healthBar75 = (Button) findViewById(R.id.healthBar75);
        healthBar75.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = "{\"8 AH battery\":\"75\"}";
                write(string);
            }
        });
        Button healthBar50 = (Button) findViewById(R.id.healthBar50);
        healthBar50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = "{\"8 AH battery\":\"50\"}";
                write(string);
            }
        });

        Button energyBar100 = (Button) findViewById(R.id.energyBar100);
        energyBar100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = "{\"2 AH battery\":\"100\"}";
                write(string);
            }
        });
        Button energyBar75 = (Button) findViewById(R.id.energyBar75);
        energyBar75.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = "{\"2 AH battery\":\"75\"}";
                write(string);
            }
        });
        Button energyBar50 = (Button) findViewById(R.id.energyBar50);
        energyBar50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = "{\"2 AH battery\":\"50\"}";
                write(string);
            }
        });

        Button headTemp20 = (Button) findViewById(R.id.headTemp20);
        headTemp20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = "{\"head temperature\":\"20\"}";
                write(string);
            }
        });
        Button headTemp30 = (Button) findViewById(R.id.headTemp30);
        headTemp30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = "{\"head temperature\":\"30\"}";
                write(string);
            }
        });

        Button armpitsTemp20 = (Button) findViewById(R.id.armpitsTemp20);
        armpitsTemp20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = "{\"armpits temperature\":\"20\"}";
                write(string);
            }
        });
        Button armpitsTemp30 = (Button) findViewById(R.id.armpitsTemp30);
        armpitsTemp30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = "{\"armpits temperature\":\"30\"}";
                write(string);
            }
        });

        Button crotchTemp20 = (Button) findViewById(R.id.crotchTemp20);
        crotchTemp20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = "{\"crotch temperature\":\"20\"}";
                write(string);
            }
        });
        Button crotchTemp30 = (Button) findViewById(R.id.crotchTemp30);
        crotchTemp30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = "{\"crotch temperature\":\"30\"}";
                write(string);
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        ConnectThread = new ConnectThread();
        ConnectThread.start();
    }

    public void write (String string) {
        try {
            mmOutStream.write(string.getBytes());
        } catch (Exception e) {
            Log.i(TAG,"Error occurred when sending data " + e.getLocalizedMessage());
            Toast.makeText(this, "error occurred with write" , Toast.LENGTH_SHORT).show();
        }
    }
}
