package com.gah.widget.bluetoothapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gah.bluetooth.profile.BluetoothProfileManager;
import com.gah.bluetooth.utils.BlueToothProfileManager;
import com.gah.eventbus.Event;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GaoAhui
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DeviceListActivity";
    private static final int REQUEST_ENABLE_BT = 1;

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    private BluetoothAdapter mBtAdapter;
    private BtDeviceListAdapter listAdapter;
    private List<BluetoothDevice> bluetoothDeviceList;
    private List<BluetoothDevice> connectedDevices;
    private List<BluetoothDevice> disConnectingDevices;
    private List<BluetoothDevice> connectingDevices;
    private android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        bluetoothDeviceList = new ArrayList<>();
        connectedDevices = new ArrayList<>();
        disConnectingDevices = new ArrayList<>();
        connectingDevices = new ArrayList<>();
        listAdapter = new BtDeviceListAdapter(this,bluetoothDeviceList,connectedDevices);

        // Initialize the button to perform device discovery
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
            }
        });

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(listAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);//蓝牙设备连接广播
        this.registerReceiver(mReceiver,filter);

        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);//蓝牙设备请求断开广播
        this.registerReceiver(mReceiver,filter);

        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);//蓝牙设备断开连接广播
        this.registerReceiver(mReceiver,filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);//蓝牙设备名称改变
        this.registerReceiver(mReceiver,filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBtAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
        switch (event.getId()){
            case Event.EVENT_BT_CONNECTED_DEVICES:
                List<BluetoothDevice> devices =(List<BluetoothDevice>) event.getObject();
                if (devices != null && devices.size() > 0){
                    for (BluetoothDevice device : devices){
                        if (!bluetoothDeviceList.contains(device)){
                            bluetoothDeviceList.add(device);
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    private void doDiscovery() {
        Log.d(TAG, "doDiscovery()");
        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle("正在扫描");
        bluetoothDeviceList.clear();
        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            final BluetoothDevice device = mBtAdapter.getRemoteDevice(address);

/**             第一种连接方式*/
//            if (!BlueToothProfileManager.getInstance().isCreateBond(device)){
//                mBtAdapter.getProfileProxy(MainActivity.this, new BluetoothProfile.ServiceListener() {
//                    @Override
//                    public void onServiceConnected(int profile, BluetoothProfile proxy) {
//                        try {
//                            BlueToothProfileManager.getInstance().connect(proxy,device);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    @Override
//                    public void onServiceDisconnected(int profile) {
//                        Log.e("onServiceDisconnected","profile :" + profile);
//                    }
//                }, BluetoothProfile.HEADSET);
//            }
            if (!BlueToothProfileManager.getInstance().isCreateBond(device)){
                BluetoothProfileManager.getInstance(MainActivity.this).connect(device);
            }
        }
    };

    /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {//发现设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (!bluetoothDeviceList.contains(mBtAdapter.getRemoteDevice(device.getAddress()))){
                    bluetoothDeviceList.add(mBtAdapter.getRemoteDevice(device.getAddress()));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listAdapter.notifyDataSetChanged();
                        }
                    });
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {//扫描完毕
                setProgressBarIndeterminateVisibility(false);
                setTitle("扫描完毕");
                mBtAdapter.cancelDiscovery();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listAdapter.notifyDataSetChanged();
                    }
                });
            }else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){// 连接
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e("ACTION_ACL_CONNECTED",device.getName() + "" + device.getAddress());
                if (!bluetoothDeviceList.contains(device)){
                    bluetoothDeviceList.add(mBtAdapter.getRemoteDevice(device.getAddress()));
                }
                if (!connectedDevices.contains(device)){
                    connectedDevices.add(device);
                }
                if (disConnectingDevices.contains(device)){
                    connectedDevices.remove(device);
                }
                if (connectingDevices.contains(device)){
                    connectingDevices.remove(device);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listAdapter.notifyDataSetChanged();
                    }
                });
            }else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){//断开连接
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e("ACTION_ACL_DISCONNECTED",device.getName() + "" + device.getAddress());
                if (connectedDevices.contains(device)){
                    connectedDevices.remove(device);
                }
                if (disConnectingDevices.contains(device)){
                    connectedDevices.remove(device);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listAdapter.notifyDataSetChanged();
                    }
                });

            }else if (BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED.equals(action)){//名称改变
                String name = intent.getStringExtra(BluetoothAdapter.EXTRA_LOCAL_NAME);
                Log.e("ACTION_LOCAL_NAME_CHANGED",name);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listAdapter.notifyDataSetChanged();
                    }
                });
            }else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e("ACTION_ACL_DISCONNECT_REQUESTED",device.getName() + "" + device.getAddress());
                if (connectedDevices.contains(device)){
                    connectedDevices.remove(device);
                }
                if (!disConnectingDevices.contains(device)){
                    disConnectingDevices.add(device);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    };

    public List<BluetoothDevice> getConnectedDevices() {
        return connectedDevices;
    }

    public List<BluetoothDevice> getDisConnectingDevices() {
        return disConnectingDevices;
    }

    public List<BluetoothDevice> getConnectingDevices() {
        return connectingDevices;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                Log.e("蓝牙扫描","REQUEST_ENABLE_BT");
                if (resultCode == Activity.RESULT_OK){
                    doDiscovery();
                }
                break;
        }
    }


}
