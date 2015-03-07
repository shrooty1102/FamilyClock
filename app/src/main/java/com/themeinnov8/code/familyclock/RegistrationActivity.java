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

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
//import com.microsoft.windowsazure.mobileservices.*;
//import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
//import com.microsoft.windowsazure.mobileservices.notifications.Registration;
//import com.microsoft.windowsazure.mobileservices.table.*;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

//import org.json.JSONException;
//import org.json.JSONObject;

import java.net.MalformedURLException;

public class RegistrationActivity extends ActionBarActivity {

    private EditText username, regioncode, phnumber;
    private Button register, register2;


    // Mobile Service Client reference
    private MobileServiceClient mClient;

    // Mobile Service Table used to access data
    private com.microsoft.windowsazure.mobileservices.table.MobileServiceTable<Member> memberTable;
    private String IMEI;

    private int salt;
    private String guid;

    public static String registrationfile = "RegistrationFile";
    public static SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initializeComponents();

        loadSharedPreferences();

        setOnClickListeners();

        try {
            Log.d("Family Clock Logs", "Initializing mobile service client... Entered try.");
            initializeMobileServiceClient();
        } catch (MalformedURLException e) {
            Log.d("Family Clock Logs", "Exception : "+e.getMessage());
        }
    }

    private void loadSharedPreferences() {
        sharedPrefs = getSharedPreferences(registrationfile, MODE_PRIVATE);
    }

    private void initializeMobileServiceClient() throws MalformedURLException {
        Log.d("Family Clock Logs", "Initializing mobile service client...");
        mClient = new MobileServiceClient("https://familyclock2.azure-mobile.net/", "CpzlaeuCHlMWejwkEwMaMHEmIZhtgW53", this );
        Log.d("Family Clock Logs", "Initialized mobile service client.");
    }

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

        Member member = createNewMember();

        Log.d("Family Clock Logs", "Sending request from device with IMEI = "+member.imei);

        ListenableFuture<RegistrationResponse> result = mClient.invokeApi("registration", member, RegistrationResponse.class);

        Log.d("Family Clock Logs", "Result : "+result.toString());

        Futures.addCallback(result, new FutureCallback<RegistrationResponse>() {
            @Override
            public void onFailure(Throwable exc) {
                Throwable cause = exc.getCause();
                Log.d("Family Clock Logs", "Error : "+exc.toString());
                Log.d("Family Clock Logs", "Cause : "+cause.toString());
            }

            @Override
            public void onSuccess(RegistrationResponse result) {
                Log.d("Family Clock Logs", "Response from server : "+result.toString());
                Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT);

                extractSALTandGUID(result);

                storeInSharedPreferences();

                gotoFamilyClock();
            }
        });

    }

    private void storeInSharedPreferences() {

        SharedPreferences.Editor editor = sharedPrefs.edit();

        //editor.putString("salt", salt);
        //editor.putString("guid", guid);
        //editor.putString("name", username.getText().toString());
        //editor.putString("rcode", regioncode.getText().toString());
        //editor.putString("phno", phnumber.getText().toString());

        editor.commit();

    }


    private void extractSALTandGUID(RegistrationResponse result) {
        // salt = ;
        // guid = ;
    }

    private void gotoFamilyClock() {

        Intent familyClock = new Intent(this, FamilyClockActivity.class);
        RegistrationActivity.this.finish();
        startActivity(familyClock);

    }

    /*private void addNewMember(final Member newMember) {
        Log.d("Family Clock Logs", "Adding New User... ");
        if (mClient == null) {
            return;
        }

        // Insert the new item
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    memberTable.insert(newMember, new TableOperationCallback<Member>() {
                        @Override
                        public void onCompleted(Member entity, Exception exception, ServiceFilterResponse response) {
                            if(exception==null) {
                                Log.d("Family Clock Logs", "com.themeinnov8.code.familyclock.Registration Successful.");
                                //Toast.makeText(getApplicationContext(), "com.themeinnov8.code.familyclock.Registration Successful", Toast.LENGTH_SHORT);
                            } else {
                                Throwable cause = exception.getCause();
                                Log.d("Family Clock Logs", "Error in com.themeinnov8.code.familyclock.Registration : "+exception.toString());
                                Log.d("Family Clock Logs", "Cause : "+cause.toString());
                            }
                        }
                    });

                    Log.d("Family Clock Logs", "New User Added.");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getApplicationContext(), "com.themeinnov8.code.familyclock.Registration Successful", Toast.LENGTH_SHORT);
                        }
                    });
                    //Log.d("Family Clock Logs", "com.themeinnov8.code.familyclock.Registration Successful.");
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error in com.themeinnov8.code.familyclock.Registration! Please try again.", Toast.LENGTH_SHORT);
                    Log.d("Family Clock Logs", "com.themeinnov8.code.familyclock.Registration Exception : "+e.getMessage());
                }

                return null;
            }
        }.execute();
    }*/

    private Member createNewMember() {
        Member member = new Member();

        member.name = username.getText().toString();
        member.imei = getIMEI();
        member.phno = phnumber.getText().toString();
        member.rcode = regioncode.getText().toString();

        return member;
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
