package com.ingsoft.bancoapp.products;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.ingsoft.bancoapp.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private final SparseArray<GroupProducts> groups;
    public LayoutInflater inflater;
    public Activity activity;

    public MyExpandableListAdapter(Activity act, SparseArray<GroupProducts> groups) {
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String children = (String) getChild(groupPosition, childPosition);
        TextView text = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_detalles_producto, null);
        }
        text = (TextView) convertView.findViewById(R.id.contenidoTarjetaD);
        text.setText(children);
//        convertView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(activity, children,
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item, null);
        }

        GroupProducts group = (GroupProducts) getGroup(groupPosition);

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.tituloTarjeta);
        lblListHeader.setText(group.title);
        lblListHeader.setTypeface(lblListHeader.getTypeface(),Typeface.BOLD);
        TextView lblListHeaderDescription = (TextView) convertView.findViewById(R.id.contenidoTarjeta);
        lblListHeaderDescription.setText(group.description);

        ImageView imgFoto = (ImageView) convertView.findViewById(R.id.imgFoto);
        Picasso.get().load(group.image).into(imgFoto);

        Button seeDetails = (Button) convertView.findViewById(R.id.btnVerDetalles);
        View finalConvertView = convertView;

        if (isExpanded){
            seeDetails.setText("Ver menos");
            finalConvertView.findViewById(R.id.itemLayout).setBackgroundColor(Color.parseColor("#72FFFFFF"));
        } else {
            finalConvertView.findViewById(R.id.itemLayout).setBackgroundColor(Color.parseColor("#00000000"));
            seeDetails.setText("Ver más");
        }

        seeDetails.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isExpanded){
                    ((ExpandableListView) parent).collapseGroup(groupPosition);
                }
                else {
                    ((ExpandableListView) parent).expandGroup(groupPosition, true);
                    ((ExpandableListView) parent).setSelection(groupPosition);
                }
            }
        });
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
