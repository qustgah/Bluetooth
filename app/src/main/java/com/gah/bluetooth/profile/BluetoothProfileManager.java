package com.gah.bluetooth.profile;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.ParcelUuid;
import android.provider.ContactsContract;
import android.util.Log;

import com.gah.bluetooth.utils.BlueToothProfileManager;
import com.gah.eventbus.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GaoAhui on 2016/7/4.
 */
public class BluetoothProfileManager {

    private Context context;
    private Map<Integer, IBluetoothProfile> allProfileMap = new HashMap<>();
    private static BluetoothProfileManager INSTANCE;
    private static int[] APPSUPPORTPROFILE = {BluetoothProfile.A2DP,BluetoothProfile.HEADSET,4,5};

    private BluetoothProfileManager(Context context) {
        this.context = context;
    }

    public static BluetoothProfileManager getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = new BluetoothProfileManager(context);
        }
        return INSTANCE;
    }

    private void getSupportprofile(final BluetoothDevice device,boolean connect){
        ParcelUuid[] uuids;
        try {
            uuids = BlueToothProfileManager.getInstance().getUuids(device);
            // A2DP
            ParcelUuid[] SINK_UUIDS = {
                    MyBluetoothUuid.AudioSink,
                    MyBluetoothUuid.AdvAudioDist,
            };
            if (MyBluetoothUuid.containsAnyUuid(uuids, SINK_UUIDS)) {
                Log.e("蓝牙","profile    A2DP" );
                if (connect){
                    addProfileToMap(BluetoothProfile.A2DP,device);
                }else {
                    pickProfileToDisConnect(BluetoothProfile.A2DP,device);
                }
            }
            // Headset / Handsfree
            if (MyBluetoothUuid.isUuidPresent(uuids, MyBluetoothUuid.Handsfree_AG) ||
                    MyBluetoothUuid.isUuidPresent(uuids, MyBluetoothUuid.HSP_AG) || MyBluetoothUuid.isUuidPresent(uuids, MyBluetoothUuid.Handsfree)
                    || MyBluetoothUuid.isUuidPresent(uuids, MyBluetoothUuid.HSP) ) {
                Log.e("蓝牙","profile    Headset" );
                if (connect){
                    addProfileToMap(BluetoothProfile.HEADSET,device);
                }else {
                    pickProfileToDisConnect(BluetoothProfile.HEADSET,device);
                }
            }
            /**
             * 输入设备 hid   BluetoothProfile.INPUT_DEVICE
             */
            if (MyBluetoothUuid.isUuidPresent(uuids, MyBluetoothUuid.Hid)) {
                Log.e("输入设备","profile    Hid" );
                if (connect){
                    addProfileToMap(4,device);
                }else {
                    pickProfileToDisConnect(4,device);
                }
            }

            /**
             * nap   BluetoothProfile.PAN 5
             */
            if ((MyBluetoothUuid.isUuidPresent(uuids, MyBluetoothUuid.NAP))) {
                if (connect){
                    addProfileToMap(5,device);
                }else {
                    pickProfileToDisConnect(5,device);
                }
            }

        } catch (Exception e) {
            Log.e("getSupportprofile",e.getMessage());
        }
    }

    public void getConnnectedDevice(Context context){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null){
             for (int i = 0 ; i < APPSUPPORTPROFILE.length;i++){
                 if (adapter.getProfileConnectionState(APPSUPPORTPROFILE[i]) == BluetoothProfile.STATE_CONNECTED){
                     adapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
                         @Override
                         public void onServiceConnected(int profile, BluetoothProfile proxy) {
                             List<BluetoothDevice> devices = proxy.getConnectedDevices();
                             if (devices != null && devices.size() > 0){
                                 //TODO 传给界面处理
                                 new Event(Event.EVENT_BT_CONNECTED_DEVICES,devices).send();
                             }
                         }
                         @Override
                         public void onServiceDisconnected(int profile) {

                         }
                     },APPSUPPORTPROFILE[i]);
                 }
             }
        }
    }
    public void addProfileToMap(final int profileInt, final BluetoothDevice device){
        if (!allProfileMap.containsKey(profileInt)){
            new BaseBluetoothProfileImpl<>(context, profileInt, new BaseBluetoothProfileImpl.MyServiceListener() {
                @Override
                public void onServiceConnected(IBluetoothProfile profile) {
                    allProfileMap.put(profileInt,profile);
                    profile.connect(device);
                }
                @Override
                public void onServiceDisconnected() {

                }
            });
        }else {
            allProfileMap.get(profileInt).connect(device);
        }
    }

    public void pickProfileToDisConnect(int profileInt,BluetoothDevice device){
        if (allProfileMap.containsKey(profileInt)){
            allProfileMap.get(profileInt).disconnect(device);
        }
    }
    public void connect(BluetoothDevice device) {
        getSupportprofile(device,true);
    }

    public void disconnect(BluetoothDevice device) {
        getSupportprofile(device,false);
    }

    public void close() {
        for (IBluetoothProfile profile : allProfileMap.values()) {
            profile.close();
        }
        allProfileMap.clear();
        allProfileMap = null;
        context = null;
    }





}
