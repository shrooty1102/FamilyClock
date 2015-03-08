package com.themeinnov8.code.familyclock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;


public class NameActivity extends ActionBarActivity {

    private EditText edittextname;
    private Button btnSubmit;

    String imei, rcode, phno, hash, name, guid, salt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        initializeComponents();

        loadSharedPreferences();

        setOnClickListeners();
    }

    private void setOnClickListeners() {

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendRequest();
            }
        });

    }

    private void sendRequest() {

        name = edittextname.getText().toString();

        com.google.gson.JsonObject member = new com.google.gson.JsonObject();

        try {
            member.addProperty("name", name);
            member.addProperty("imei", imei);
            member.addProperty("rcode", rcode);
            member.addProperty("phno", phno);
            member.addProperty("hash", hash);

        } catch (JsonParseException e) {
            e.printStackTrace();
        }

        StartAppActivity.mClient.invokeApi("registration?action=updatename", member, "POST", null, new ApiJsonOperationCallback() {
            @Override
            public void onCompleted(JsonElement result, Exception exception, ServiceFilterResponse response) {
                if(exception == null) {

                    String salt = result.getAsJsonObject().get("SALT").getAsString();

                    Log.d("Family Clock Logs", "SALT = " + salt);

                    if(salt.equals("222222")) {
                        gotoFamilyClockActivity();
                    } else {
                        NameActivity.this.finish();
                    }

                } else {
                    Log.d("Family Clock Logs", "Exception = " + exception.toString());
                }
            }
        });

    }

    private void gotoFamilyClockActivity() {

        Intent intentFamilyClock = new Intent(NameActivity.this, FamilyClockActivity.class);
        NameActivity.this.finish();
        startActivity(intentFamilyClock);

    }

    private void loadSharedPreferences() {

        imei = StartAppActivity.sharedPrefs.getString("imei", "0");
        rcode = StartAppActivity.sharedPrefs.getString("rcode", "0");
        phno = StartAppActivity.sharedPrefs.getString("phno", "0");
        guid = StartAppActivity.sharedPrefs.getString("guid", "0");
        salt = StartAppActivity.sharedPrefs.getString("salt", "0");

        hash = imei+guid+salt;

        storeHashInSharedPreferences();

    }

    private void storeHashInSharedPreferences() {

        SharedPreferences.Editor editor = StartAppActivity.sharedPrefs.edit();
        editor.putString("hash", hash);
        editor.commit();

    }

    private void initializeComponents() {

        edittextname = (EditText) findViewById(R.id.username);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_name, menu);
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
