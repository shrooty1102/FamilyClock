package com.themeinnov8.code.familyclock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
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
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.table.query.ExecutableQuery;
import com.microsoft.windowsazure.mobileservices.table.query.Query;

import java.net.MalformedURLException;
import java.util.List;
import java.lang.Runnable;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;
import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.*;

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

    private MobileServiceClient mClient;

    private MobileServiceList<MyGroups> results;

    /**
     * Adapter to sync the items list with the view
     */
    private MyGroupsListAdapter myGroupsListAdapter;

    private com.microsoft.windowsazure.mobileservices.table.MobileServiceTable<MyGroups> myGroupsTable;

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


        listViewGroups.setAdapter(myGroupsListAdapter);

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

        myGroupsListAdapter = new MyGroupsListAdapter(getActivity(), android.R.layout.simple_list_item_1);

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

        try {
            // Create the Mobile Service Client instance, using the provided
            // Mobile Service URL and key
            mClient = new MobileServiceClient("https://familyclock2.azure-mobile.net/", "CpzlaeuCHlMWejwkEwMaMHEmIZhtgW53", getActivity() );
            myGroupsTable = mClient.getTable("GroupMembers", MyGroups.class);

            sendQueryRequest();

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        }

    }

    private void sendQueryRequest() {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    /*final List<MyGroups> results =
                            myGroupsTable.where().field("_memberid").
                                    eq(val(StartAppActivity.sharedPrefs.getString("guid", "0"))).execute().get();*/

                    results = myGroupsTable.where().field("_memberid").eq().val(StartAppActivity.sharedPrefs.getString("guid", "0")).execute().get();

                    Log.d("Family Clock Logs", "Your Groups : "+results.getTotalCount());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myGroupsListAdapter.clear();

                            for(MyGroups item : results){
                                myGroupsListAdapter.add(item);
                            }
                        }
                    });
                } catch (Exception e){
                    //createAndShowDialog(e, "Error");
                    Log.d("Family Clock Logs", "Exception : "+e.toString());
                }

                return null;
            }

            /*protected void onPostExecute() {
                myGroupsListAdapter.clear();

                for(MyGroups item : results){
                    myGroupsListAdapter.add(item);
                }
            }*/
        }.execute();



    }

    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

}
