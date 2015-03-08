package com.themeinnov8.code.familyclock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by code on 3/8/15.
 */
public class MyGroupsListAdapter extends ArrayAdapter<MyGroups> {

    Context mContext;
    int mLayoutResourceId;

    public MyGroupsListAdapter(Context context, int resource) {
        super(context, resource);

        mContext = context;
        mLayoutResourceId = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final MyGroups currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.row, parent, false);
        }

        row.setTag(currentItem);

        final TextView tvGroupItem = (TextView) row.findViewById(R.id.groupItem);
        tvGroupItem.setText(currentItem.getText());

        tvGroupItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGroupDetails = new Intent(getContext(), GroupDetailsActivity.class);

                intentGroupDetails.putExtra("groupid", currentItem.getId());
                intentGroupDetails.putExtra("groupname", currentItem.getText());

                getContext().startActivity(intentGroupDetails);
            }
        });

        return row;
    }
}
