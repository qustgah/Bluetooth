package com.gah.widget.bluetoothapp;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by GaoAhui on 2016/7/5.
 */
public class AppInfoListAdapter extends BaseAdapter {
    Context context;
    List<ResolveInfo> list;

    public AppInfoListAdapter(Context context, List<ResolveInfo> list) {
        this.context = context;
        this.list = list;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_list_app_item,parent,false);
            holder.img_app = (ImageView) convertView.findViewById(R.id.img_app);
            holder.text_app = (TextView) convertView.findViewById(R.id.text_app);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        ResolveInfo info = list.get(position);
        holder.text_app.setText(info.loadLabel(context.getPackageManager()));
        holder.img_app.setImageDrawable(info.loadIcon(context.getPackageManager()));
        return convertView;
    }

    private static class ViewHolder{
        ImageView img_app;
        TextView text_app;
    }


}
