package com.themeinnov8.code.familyclock;

import android.content.Intent;
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


public class SALTConfirmationActivity extends ActionBarActivity {

    private EditText edittextsalt;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saltconfirmation);

        initializeComponents();

        setOnClickListeners();
        //loadSharedPreferences();
    }

    private void setOnClickListeners() {

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

    }

    private void initializeComponents() {
        edittextsalt = (EditText) findViewById(R.id.edittextsalt);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
    }

    /*private void loadSharedPreferences() {
        sharedPrefs = getSharedPreferences(registrationfile, MODE_PRIVATE);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_saltconfirmation, menu);
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

    private void sendRequest() {

        com.google.gson.JsonObject member = new com.google.gson.JsonObject();
        try {

            String hash = StartAppActivity.sharedPrefs.getString("imei", "0")
                    +StartAppActivity.sharedPrefs.getString("guid", "0")
                    +edittextsalt.getText().toString();

            member.addProperty("imei", StartAppActivity.sharedPrefs.getString("imei", "0"));
            member.addProperty("rcode", StartAppActivity.sharedPrefs.getString("rcode", "0"));
            member.addProperty("phno", StartAppActivity.sharedPrefs.getString("phno", "0"));
            member.addProperty("hash", hash);

        } catch (JsonParseException e) {
            e.printStackTrace();
        }

        StartAppActivity.mClient.invokeApi("registration?action=validate", member, "POST", null, new ApiJsonOperationCallback() {
            @Override
            public void onCompleted(JsonElement result, Exception exception, ServiceFilterResponse response) {
                if(exception == null) {

                    String salt = result.getAsJsonObject().get("SALT").getAsString();

                    Log.d("Family Clock Logs", "SALT = "+salt);

                    if(salt.equals("111111")) {
                        gotoNameActivity();
                    } else {
                        SALTConfirmationActivity.this.finish();
                    }

                } else {
                    Log.d("Family Clock Logs", "Exception = " + exception.toString());
                }
            }
        });
    }

    private void gotoNameActivity() {
        Intent nameIntent = new Intent(SALTConfirmationActivity.this, NameActivity.class);
        SALTConfirmationActivity.this.finish();
        startActivity(nameIntent);
    }
}
