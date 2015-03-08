package com.themeinnov8.code.familyclock;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import org.json.JSONObject;

import java.net.MalformedURLException;


public class GroupDetailsActivity extends ActionBarActivity {

    private String groupid;
    private String groupname;

    private ListView groupdetailslistview;
    private TextView tvGroupName;
    private Button btnAddMember;

    private GroupDetailsListAdapter gdlistadapter;

    private MobileServiceClient mClient;

    private MobileServiceList<MyGroups> results;

    private com.microsoft.windowsazure.mobileservices.table.MobileServiceTable<MyGroups> groupDetailsTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        groupid = getIntent().getStringExtra("groupid");
        groupname = getIntent().getStringExtra("groupname");

        initializeComponents();

        setOnClickListeners();
    }

    private void setOnClickListeners() {

        btnAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void initializeComponents() {

        groupdetailslistview = (ListView) findViewById(R.id.listViewGroupDetails);
        tvGroupName = (TextView) findViewById(R.id.tvGroupName);
        btnAddMember = (Button) findViewById(R.id.btnAddMember);

        tvGroupName.setText(groupname);

        queryGroupMembers();

    }

    private void queryGroupMembers() {

        // todo code table query at server side

        com.google.gson.JsonObject member = new com.google.gson.JsonObject();

        try {
            member.addProperty("groupid", groupid);
            member.addProperty("imei", StartAppActivity.sharedPrefs.getString("imei", "0"));
            member.addProperty("rcode", StartAppActivity.sharedPrefs.getString("rcode", "0"));
            member.addProperty("phno", StartAppActivity.sharedPrefs.getString("phno", "0"));
            member.addProperty("hash", StartAppActivity.sharedPrefs.getString("hash", "0"));

        } catch (JsonParseException e) {
            e.printStackTrace();
        }

        StartAppActivity.mClient.invokeApi("getuserstatus", member, "POST", null, new ApiJsonOperationCallback() {
            @Override
            public void onCompleted(JsonElement result, Exception exception, ServiceFilterResponse response) {
                if(exception == null) {

                    String salt = result.getAsJsonObject().get("SALT").getAsString();
                    String message = result.getAsJsonObject().get("message").getAsString();

                    Log.d("Family Clock Logs", "message = " +message);

                    if(salt.equals("111111")) {

                        JsonObject resultObject = result.getAsJsonObject();

                        Log.d("Family Clock Logs", "Members : "+resultObject.get("members"));

                    } else {

                    }

                } else {
                    Log.d("Family Clock Logs", "Exception = " + exception.toString());
                }
            }
        });


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
