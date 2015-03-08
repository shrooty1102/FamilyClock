package com.themeinnov8.code.familyclock;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;


public class StartAppActivity extends Activity {

    private ProgressBar progressBar;
    private int progressStatus = 0;

    private Handler handler = new Handler();

    public static String registrationfile = "RegistrationFile";
    public static SharedPreferences sharedPrefs;

    public static String profiledatafile = "ProfileDataFile";
    public static SharedPreferences profileSharedPrefs;

    String salt, guid;

    // Mobile Service Client reference
    public static MobileServiceClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_app);

        initializeComponents();

        loadSharedPreferences();

        try {
            Log.d("Family Clock Logs", "Initializing mobile service client... Entered try.");
            initializeMobileServiceClient();
        } catch (MalformedURLException e) {
            Log.d("Family Clock Logs", "Exception : "+e.getMessage());
        }

        startLoadingApp();
    }

    private void initializeMobileServiceClient() throws MalformedURLException {
        Log.d("Family Clock Logs", "Initializing mobile service client...");
        mClient = new MobileServiceClient("https://familyclock2.azure-mobile.net/", "CpzlaeuCHlMWejwkEwMaMHEmIZhtgW53", this );
        Log.d("Family Clock Logs", "Initialized mobile service client.");
    }

    private void loadSharedPreferences() {

        sharedPrefs = getSharedPreferences(registrationfile, MODE_PRIVATE);
        salt = sharedPrefs.getString("salt", "");
        guid = sharedPrefs.getString("guid", "");

        profileSharedPrefs = getSharedPreferences(profiledatafile, MODE_PRIVATE);

    }

    private void startLoadingApp() {

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {

                    // sleep half a second (simulating a time consuming task...)
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                    progressStatus = progressStatus+4;
                }

                if (progressStatus >= 100) {

                    // sleep 2 seconds, so that you can see the 100%
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(!salt.equals("") && !guid.equals("")) {

                        // and then open the next activity
                        Intent intentFamilyClock = new Intent(StartAppActivity.this, FamilyClockActivity.class);
                        StartAppActivity.this.finish();
                        startActivity(intentFamilyClock);

                    } else {

                        Intent intentRegistration = new Intent(StartAppActivity.this, RegistrationActivity.class);
                        StartAppActivity.this.finish();
                        startActivity(intentRegistration);

                    }


                }
            }
        }).start();

    }

    private void initializeComponents() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_app, menu);
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
