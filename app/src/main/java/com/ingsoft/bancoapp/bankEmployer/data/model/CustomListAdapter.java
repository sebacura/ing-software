package com.ingsoft.bancoapp.bankEmployer.data.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ingsoft.bancoapp.R;
import com.ingsoft.bancoapp.bankEmployer.data.RequestItem;

import java.util.ArrayList;
/**
 * Created by tutlane on 23-08-2017.
 */
public class CustomListAdapter extends BaseAdapter {
    private ArrayList<RequestItem> listData;
    private LayoutInflater layoutInflater;
    public CustomListAdapter(Context aContext, ArrayList<RequestItem> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }
    @Override
    public int getCount() {
        return listData.size();
    }
    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View v, ViewGroup vg) {
        ViewHolder holder;
        if (v == null) {
            v = layoutInflater.inflate(R.layout.list_row, null);
            holder = new ViewHolder();
            holder.name = (TextView) v.findViewById(R.id.name);
            holder.address = (TextView) v.findViewById(R.id.address);
            holder.ci = (TextView) v.findViewById(R.id.ci);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.name.setText(listData.get(position).getFirstName());
        holder.address.setText(listData.get(position).getAddress());
        holder.ci.setText(listData.get(position).getCi());
        return v;
    }
    static class ViewHolder {
        TextView name;
        TextView address;
        TextView ci;
    }
}