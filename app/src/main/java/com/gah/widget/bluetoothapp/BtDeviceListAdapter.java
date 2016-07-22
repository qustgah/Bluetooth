package com.gah.widget.bluetoothapp;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gah.bluetooth.profile.BluetoothProfileManager;
import com.gah.bluetooth.utils.BlueToothProfileManager;

import java.util.List;

/**
 * Created by GaoAhui on 2016/7/5.
 */
public class BtDeviceListAdapter extends BaseAdapter{

    Activity context;
    List<BluetoothDevice> list;
    List<BluetoothDevice> connectedList;

    public BtDeviceListAdapter(Activity context,List<BluetoothDevice> list,List<BluetoothDevice> devices) {
        this.context = context;
        this.list = list;
        this.connectedList = devices;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_list_bt_item,parent,false);
            holder.img_device = (ImageView) convertView.findViewById(R.id.img_device);
            holder.text_device = (TextView) convertView.findViewById(R.id.text_device);
            holder.btn_connect = (Button) convertView.findViewById(R.id.btn_connect);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        final BluetoothDevice device = list.get(position);
        if (((MainActivity)context).getConnectedDevices().contains(device)){
            holder.btn_connect.setText("已连接");
        }else if (((MainActivity)context).getDisConnectingDevices().contains(device)){
            holder.btn_connect.setText("断开连接");
        }else{
            holder.btn_connect.setText("连接");
        }
        holder.text_device.setText(device.getName());
        holder.btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).getConnectingDevices().add(device);
                BluetoothProfileManager.getInstance(context).connect(device);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }


    private static class ViewHolder{
        ImageView img_device;
        TextView text_device;
        Button btn_connect;

    }
}
