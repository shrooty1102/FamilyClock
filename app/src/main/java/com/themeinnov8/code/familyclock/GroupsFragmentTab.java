package com.themeinnov8.code.familyclock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

/**
 * Created by code on 3/7/15.
 */
public class GroupsFragmentTab extends Fragment {

    private TextView tvRefresh, tvCreateGroup;

    private String imei;
    private String guid;
    private String phno;
    private String rcode;
    private String hash;

    private View rootView;

    private ListView listViewGroups;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        initializeComponents();

        //loadSharedPreferences();

        setOnClickListeners();

        return rootView;
    }

    /*private void loadSharedPreferences() {



    }*/

    private void setOnClickListeners() {

        tvCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });


        //listViewGroups.setAdapter();

    }

    private void showInputDialog() {

            // get prompts.xml view
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View promptView = layoutInflater.inflate(R.layout.prompt, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setView(promptView);

            final EditText editTextGroupName = (EditText) promptView.findViewById(R.id.edittextGroupName);

            // setup a dialog window
            alertDialogBuilder.setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            sendCreateGroupRequest(editTextGroupName.getText().toString());

                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create an alert dialog
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();

    }

    private void initializeComponents() {

        tvRefresh = (TextView) rootView.findViewById(R.id.tvRefresh);
        tvCreateGroup = (TextView) rootView.findViewById(R.id.tvCreateNew);
        listViewGroups = (ListView) rootView.findViewById(R.id.listView1);

    }

    private void sendCreateGroupRequest(String groupname) {

        //storeDataInSharedPreferences();

        com.google.gson.JsonObject member = new com.google.gson.JsonObject();

        try {

            member.addProperty("imei", StartAppActivity.sharedPrefs.getString("imei", "0"));
            member.addProperty("rcode", StartAppActivity.sharedPrefs.getString("rcode", "0"));
            member.addProperty("phno", StartAppActivity.sharedPrefs.getString("phno", "0"));
            member.addProperty("hash", StartAppActivity.sharedPrefs.getString("hash", "0"));
            member.addProperty("groupname", groupname);

        } catch (JsonParseException e) {
            e.printStackTrace();
        }

        StartAppActivity.mClient.invokeApi("creategroup", member, "POST", null, new ApiJsonOperationCallback() {
            @Override
            public void onCompleted(JsonElement result, Exception exception, ServiceFilterResponse response) {
                if(exception == null) {

                    String salt = result.getAsJsonObject().get("result").getAsString();
                    String message = result.getAsJsonObject().get("message").getAsString();

                    Log.d("Family Clock Logs", "SALT = " + salt);
                    Log.d("Family Clock Logs", "message = " + message);

                    if(!salt.equals("000000")) {

                        queryMyGroups();

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Could not save your status", Toast.LENGTH_SHORT);
                    }

                } else {
                    Log.d("Family Clock Logs", "Exception = " + exception.toString());
                }
            }
        });

    }

    private void queryMyGroups() {

        // todo code table query at server side

        

    }

}
