package com.gah.bluetooth.utils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.os.ParcelUuid;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by GaoAhui on 2016/6/30.
 */
public class BlueToothProfileManager {


    private static BlueToothProfileManager INSTANCE;
    private BlueToothProfileManager() {
    }

    public static BlueToothProfileManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new BlueToothProfileManager();
        }
        return INSTANCE;
    }

    /**
     * 是否已经配对
     * @param device 蓝牙设备
     * @return 是否配对
     */
    public boolean isCreateBond(BluetoothDevice device){
        boolean result = false;
        try{
            Method m = device.getClass().getDeclaredMethod("createBond");
            m.setAccessible(true);
            Boolean originalResult = (Boolean) m.invoke(device);
            result = originalResult;
        }catch(Exception e){
            Log.e("Exception",e.getMessage());
        }
        return result;
    }

    public boolean connect(BluetoothProfile proxy,BluetoothDevice btDevice) throws Exception {
        Method connectMethod = proxy.getClass().getDeclaredMethod("connect", BluetoothDevice.class);
        connectMethod.setAccessible(true);
        Boolean returnValue = (Boolean) connectMethod.invoke(proxy,btDevice);
        return returnValue;
    }

    public boolean disconnect(BluetoothProfile proxy,BluetoothDevice btDevice) throws Exception {
        Method disconnectMethod = proxy.getClass().getDeclaredMethod("disconnect", BluetoothDevice.class);
        disconnectMethod.setAccessible(true);
        Boolean returnValue = (Boolean) disconnectMethod.invoke(proxy,btDevice);
        return returnValue;
    }

    public ParcelUuid[] getUuids(BluetoothDevice btDevice)throws Exception{
        Method m = btDevice.getClass().getDeclaredMethod("getUuids");
        m.setAccessible(true);
        return (ParcelUuid[]) m.invoke(btDevice);
    }

}
