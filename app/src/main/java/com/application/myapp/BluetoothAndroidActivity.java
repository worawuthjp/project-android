package com.application.myapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.module.ConnectedThread;
import com.application.module.Device;
import com.application.module.DeviceAdapter;
import com.imagealgorithmlab.barcode.SymbologyData;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothAndroidActivity extends AppCompatActivity {

    private final String TAG = BluetoothAndroidActivity.class.getSimpleName();

    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    // GUI Components
    private TextView mBluetoothStatus;
    private TextView mReadBuffer;
    private Button mDiscoverBtn;
    private ListView mDevicesListView;
    private ProgressBar mProgressBar;

    private BluetoothAdapter mBTAdapter;
    private ArrayList<Device> deviceList;
    private DeviceAdapter deviceAdapter;

    private Context context;

    private Handler mHandler;
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_android);

        context = this;

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothStatus = findViewById(R.id.bluetooth_status);
        mReadBuffer = findViewById(R.id.read_buffer);
        mDiscoverBtn = findViewById(R.id.discover);
        mDevicesListView = findViewById(R.id.devices_list_view);
        mProgressBar = findViewById(R.id.progressbarLoadingDevice);

        deviceList = new ArrayList<>();

        deviceAdapter = new DeviceAdapter(context , deviceList);
        mDevicesListView.setAdapter(deviceAdapter);
//        arrayAdapter = new ArrayAdapter<String>(this , android.R.layout.simple_list_item_1);
//        mDevicesListView.setAdapter(arrayAdapter);

        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == MESSAGE_READ){
                    byte[] readBuff = (byte[]) msg.obj;
                    String readMessage = new String(readBuff, 0 , msg.arg1);
                    mReadBuffer.setText(readMessage);
                    if (!mReadBuffer.getText().toString().toLowerCase().equals("")){
                        Intent intent = new Intent();
                        intent.putExtra("message" , readMessage);
                        setResult(RESULT_OK , intent);
                        finish();
                    }
                }

                if(msg.what == CONNECTING_STATUS){
                    mProgressBar.setVisibility(View.GONE);
                    if(msg.arg1 == 1) {
                        mBluetoothStatus.setText("Connected to Device: " + msg.obj);
                        mDevicesListView.setVisibility(View.GONE);
                    }
                    else
                        mBluetoothStatus.setText("Connection Failed");
                }
            }
        };


        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(blReceiver, filter);

        if (initBluetooth()){
            if (bluetoothOn()){
//                arrayAdapter.clear();

                discover();

                mDevicesListView.setOnItemClickListener(mDeviceClickListener);

                mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDiscoverBtn.setVisibility(View.GONE);
                        discover();
                    }
                });

            }
        }

    }

    public boolean initBluetooth(){
        if (mBTAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getApplicationContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean bluetoothOn(){
        if (!mBTAdapter.isEnabled()) {
            mBTAdapter.enable();
//            Toast.makeText(getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();
            SystemClock.sleep(500);
        }
        else{
//            Toast.makeText(getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    private void clearDiscovering(){
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
//            Toast.makeText(getApplicationContext(),"Discovery stopped",Toast.LENGTH_LONG).show();
        }
    }

    private void discover(){
        // Check if the device is already discovering
        if (mBTAdapter.isEnabled()){
//            arrayAdapter.clear();
            deviceAdapter.clearData();
            clearDiscovering();
            mBTAdapter.startDiscovery();
            mBluetoothStatus.setText("Scan Devices");

            mProgressBar.setVisibility(View.VISIBLE);
// clear items
//            Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
//            BluetoothDeviceFilter deviceFilter = new BluetoothDeviceFilter.Builder()
//                    .setNamePattern(Pattern.compile("My device"))
//                    .addServiceUuid(new ParcelUuid(new UUID(0x123abcL, -1L)), null)
//                    .build();
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                if (device.getName() != null){
                    deviceList.add(new Device(device.getName() , device.getAddress()));
//                    arrayAdapter.add(device.getName());
                    Log.i(TAG , "deviceName-> " + device.getName() + " , deviceAddress-> " + device.getAddress());
                }
//                    mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress())
                deviceAdapter.notifyDataSetChanged();
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                if (deviceAdapter.getCount() == 0){
                    mProgressBar.setVisibility(View.GONE);
                    mBluetoothStatus.setText("No devices founds.");
                    mDiscoverBtn.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            mProgressBar.setVisibility(View.VISIBLE);
            mBluetoothStatus.setText("Connecting...");

            // Get the device MAC address, which is the last 17 chars in the View
            Device info = deviceList.get(position);
            Log.i(TAG, "onItemClick: info name -> " + info.getName() + " , address -> " + info.getAddress());
            final String address = info.getAddress();
            final String name = info.getName();

            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                @Override
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(!fail) {
                        mConnectedThread = new ConnectedThread(mBTSocket, mHandler);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();

        }
    };

    private boolean backPressToExit = false;

    //@Override
//    public void onBackPressed() {
//
//        if (backPressToExit) {
//            super.onBackPressed();
//            return;
//        }
//        clearDiscovering();
//        this.backPressToExit = true;
//        Toast.makeText(context , "Please Back Button to outside" , Toast.LENGTH_SHORT).show();
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                backPressToExit = false;
//            }
//        }, 2000);
//    }

    private void bluetoothOff(){
        mBTAdapter.disable(); // turn off
//        Toast.makeText(getApplicationContext(),"Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        //mConnectedThread.cancel();
        //unregisterReceiver(blReceiver);
        //bluetoothOff();
    }


}