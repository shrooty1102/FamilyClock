package com.themeinnov8.code.familyclock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.telephony.TelephonyManager;
import android.telephony.SmsManager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.ApiOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
//import com.microsoft.windowsazure.mobileservices.*;
//import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
//import com.microsoft.windowsazure.mobileservices.notifications.Registration;
//import com.microsoft.windowsazure.mobileservices.table.*;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

//import org.json.JSONException;
//import org.json.JSONObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

public class RegistrationActivity extends ActionBarActivity {

    private EditText username, regioncode, phnumber;
    private Button register, register2;


    // Mobile Service Table used to access data
    private com.microsoft.windowsazure.mobileservices.table.MobileServiceTable<Member> memberTable;
    private String IMEI;

    private String salt;
    private String guid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initializeComponents();

        //loadSharedPreferences();

        setOnClickListeners();
    }

    /*private void loadSharedPreferences() {
        StartAppActivity.sharedPrefs = getSharedPreferences(StartAppActivity.registrationfile, MODE_PRIVATE);
    }*/



    private void initializeComponents() {
        username = (EditText) findViewById(R.id.username);
        regioncode = (EditText) findViewById(R.id.regioncode);
        phnumber = (EditText) findViewById(R.id.phnumber);
        register = (Button) findViewById(R.id.register);
    }

    private void setOnClickListeners() {
        // set onClickListener to Register button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendRequest();
            }
        });
    }

    private void sendRequest() {

        com.google.gson.JsonObject member = new com.google.gson.JsonObject();
        try {

            member.addProperty("imei", getIMEI());
            member.addProperty("rcode", regioncode.getText().toString());
            member.addProperty("phno", phnumber.getText().toString());

        } catch (JsonParseException e) {
            e.printStackTrace();
        }

        StartAppActivity.mClient.invokeApi("registration?action=register", member, "POST", null, new ApiJsonOperationCallback() {
            @Override
            public void onCompleted(JsonElement result, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {

                    extractSALTandGUID(result);

                    storeInSharedPreferences();

                    sendSaltSMS();

                    gotoSALTConfirmation();
                } else {
                    Log.d("Family Clock Logs", "Exception = " + exception.toString());
                }
            }
        });
    }

    private void sendSaltSMS() {

        String phoneNo = phnumber.getText().toString();

        String message = "Your Confirmation Code is "+salt+". Enter this code on the Confirmation page and complete your registration.";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
        } catch (Exception e) {
            Log.d("Family Clock Logs", "Exception : "+e.toString());
        }
    }

    private void storeInSharedPreferences() {

        SharedPreferences.Editor editor = StartAppActivity.sharedPrefs.edit();

        editor.putString("salt", salt);
        editor.putString("guid", guid);
        editor.putString("imei", getIMEI());
        //editor.putString("name", username.getText().toString());
        editor.putString("rcode", regioncode.getText().toString());
        editor.putString("phno", phnumber.getText().toString());

        editor.commit();

    }


    private void extractSALTandGUID(JsonElement result) {
        salt = result.getAsJsonObject().get("SALT").getAsString();
        guid = result.getAsJsonObject().get("userGUID").getAsString();
    }

    private void gotoSALTConfirmation() {

        Intent saltIntent = new Intent(RegistrationActivity.this, SALTConfirmationActivity.class);
        RegistrationActivity.this.finish();
        startActivity(saltIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
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

    public String getIMEI() {
        TelephonyManager telephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        String m_deviceId = telephonyMgr.getDeviceId();
        return m_deviceId;
    }
}
