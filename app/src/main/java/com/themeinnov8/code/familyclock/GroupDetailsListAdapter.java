package com.themeinnov8.code.familyclock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by code on 3/8/15.
 */
public class GroupDetailsListAdapter extends ArrayAdapter<GroupMembers> {

    Context mContext;
    int mLayoutResourceId;


    public GroupDetailsListAdapter(Context context, int resource) {
        super(context, resource);

        mContext = context;
        mLayoutResourceId = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final GroupMembers currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.groupmemberlistitem, parent, false);
        }

        row.setTag(currentItem);

        final TextView tvMemberName = (TextView) row.findViewById(R.id.tvMemberName);
        final TextView tvDoingActivity = (TextView) row.findViewById(R.id.tvDoingActivity);
        final TextView tvAtLocation = (TextView) row.findViewById(R.id.tvAtLocation);

        tvMemberName.setText(currentItem.getText());



        return row;
    }

}
