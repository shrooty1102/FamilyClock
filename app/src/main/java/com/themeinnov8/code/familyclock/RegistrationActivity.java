package com.themeinnov8.code.familyclock;

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

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.*;

import java.net.MalformedURLException;

public class RegistrationActivity extends ActionBarActivity {

    private EditText username, regioncode, phnumber;
    private Button register;


    // Mobile Service Client reference
    private MobileServiceClient mClient;

    // Mobile Service Table used to access data
    private com.microsoft.windowsazure.mobileservices.table.MobileServiceTable<Member> memberTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initializeComponents();

        setOnClickListeners();

        try {
            Log.d("Family Clock Logs", "Initializing mobile service client... Entered try.");
            initializeMobileServiceClient();
        } catch (MalformedURLException e) {
            Log.d("Family Clock Logs", "Exception : "+e.getMessage());
        }
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

                // Connect to MS Azure Mobile Service and Send the NEW REGISTRATION details.

                Member newMember = createNewMember();

                Log.d("Family Clock Logs", "New User Created.");

                // Get the Mobile Service Table instance to use
                memberTable = mClient.getTable("UserRegistration", Member.class);

                addNewMember(newMember);

            }
        });
    }

    private void addNewMember(final Member newMember) {
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
                                Log.d("Family Clock Logs", "Registration Successful.");
                                //Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT);
                            } else {
                                Throwable cause = exception.getCause();
                                Log.d("Family Clock Logs", "Error in Registration : "+exception.toString());
                                Log.d("Family Clock Logs", "Cause : "+cause.toString());
                            }
                        }
                    });

                    Log.d("Family Clock Logs", "New User Added.");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT);
                        }
                    });
                    //Log.d("Family Clock Logs", "Registration Successful.");
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error in Registration! Please try again.", Toast.LENGTH_SHORT);
                    Log.d("Family Clock Logs", "Registration Exception : "+e.getMessage());
                }

                return null;
            }
        }.execute();
    }

    private Member createNewMember() {
        Member member = new Member();

        member.set_name(""+username.getText());
        member.set_rcode(""+regioncode.getText());
        member.set_phno(Long.parseLong(phnumber.getText().toString()));
        member.set_imei("12345cgfh34nk");

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
}
