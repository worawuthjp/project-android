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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    private TextView txtLabel;
    private ProgressBar progressBarScanDevices;
    private ListView listDevices;
    private TextView mReadBuffer;
    private final String TAG = BluetoothAndroidActivity.class.getSimpleName();

    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
//    private ArrayAdapter<String> mBTArrayAdapter;
    private ArrayList<Device> deviceList;
    private DeviceAdapter deviceAdapter;

    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread connectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_android);

        txtLabel = findViewById(R.id.txtLabel);
        progressBarScanDevices = findViewById(R.id.progressBarScanDevice);

        mReadBuffer = findViewById(R.id.readBuffer);
        deviceList = new ArrayList<>();

        deviceAdapter = new DeviceAdapter(this , deviceList);

        listDevices = findViewById(R )

    }


}