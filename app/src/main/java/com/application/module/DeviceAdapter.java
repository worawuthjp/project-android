package com.application.module;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.application.myapp.R;

import java.util.ArrayList;

public class DeviceAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Device> deviceArrayList;
    LayoutInflater inflater;

    public DeviceAdapter(Context context , ArrayList<Device> devices) {
        this.context = context;
        this.deviceArrayList = devices;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return deviceArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.device_list_item , null);
            holder.uName = (TextView) convertView.findViewById(R.id.deviceName);
            holder.uAddress = (TextView) convertView.findViewById(R.id.deviceAddress);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.uName.setText(deviceArrayList.get(position).getName());
        holder.uAddress.setText(deviceArrayList.get(position).getAddress());
        return convertView;
    }

    static class ViewHolder {
        TextView uName;
        TextView uAddress;
    }

    public void clearData() {
        deviceArrayList.clear();
    }
}
