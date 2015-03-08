package com.themeinnov8.code.familyclock;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.util.List;

/**
 * Created by code on 3/7/15.
 */
public class ProfileFragmentTab extends Fragment {

    private View rootView;

    private Switch gpsSwitch;
    private TextView tvStatus, tvLocation;
    private Button btnSetUsingGPS, btnSave;

    private GPSTracker gpsTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeComponents();

        setGPSswitchstatus();

        setOnClickListeners();

        return rootView;
    }

    private void initializeComponents() {

        gpsTracker = new GPSTracker(getActivity().getApplicationContext());

        gpsSwitch = (Switch) rootView.findViewById(R.id.gpsSwitch);

        tvStatus = (TextView) rootView.findViewById(R.id.tvStatus);
        tvLocation = (TextView) rootView.findViewById(R.id.tvLocation);

        btnSetUsingGPS = (Button) rootView.findViewById(R.id.btnSetUsingGPS);
        btnSave = (Button) rootView.findViewById(R.id.btnSave);

    }

    private void setOnClickListeners() {

        // onClickListener for GPS-Switch
        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                gpsTracker.showSettingsAlert();
            }
        });

        // onClickListener for Button - Set status using GPS
        btnSetUsingGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!gpsTracker.isSystemGPSEnabled()) {
                    gpsTracker.showSettingsAlert();
                }

            }
        });

        // onClickListener for Button - Save my status
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
    }

    private void sendRequest() {

        String status = tvStatus.getText().toString();
        String latitude = ""+gpsTracker.getLatitude();
        String longitude = ""+gpsTracker.getLongitude();

        String location = latitude+" ; "+longitude;

        com.google.gson.JsonObject member = new com.google.gson.JsonObject();

        try {

            member.addProperty("imei", StartAppActivity.sharedPrefs.getString("imei", "0"));
            member.addProperty("rcode", StartAppActivity.sharedPrefs.getString("rcode", "0"));
            member.addProperty("phno", StartAppActivity.sharedPrefs.getString("phno", "0"));
            member.addProperty("hash", StartAppActivity.sharedPrefs.getString("hash", "0"));
            member.addProperty("status", status);
            member.addProperty("location", location);

        } catch (JsonParseException e) {
            e.printStackTrace();
        }

        StartAppActivity.mClient.invokeApi("statusupdate", member, "POST", null, new ApiJsonOperationCallback() {
            @Override
            public void onCompleted(JsonElement result, Exception exception, ServiceFilterResponse response) {
                if(exception == null) {

                    String salt = result.getAsJsonObject().get("SALT").getAsString();
                    String message = result.getAsJsonObject().get("message").getAsString();

                    Log.d("Family Clock Logs", "SALT = " + salt);
                    Log.d("Family Clock Logs", "message = " + message);

                    if(salt.equals("111111")) {
                        Toast.makeText(getActivity().getApplicationContext(), "Status Saved", Toast.LENGTH_SHORT);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Could not save your status", Toast.LENGTH_SHORT);
                    }

                } else {
                    Log.d("Family Clock Logs", "Exception = " + exception.toString());
                }
            }
        });

    }

    /*private String getCurrentLocation() {

        String deviceAddress;

        LocationAddress locationAddress = new LocationAddress();

        //locationAddress.getAddressFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(),
        //        getActivity().getApplicationContext(), new GeocoderHandler());


    }
*/
    private void setGPSswitchstatus() {

        if(gpsTracker.isSystemGPSEnabled()) {
            gpsSwitch.setChecked(true);
        } else {
            gpsSwitch.setChecked(false);
        }
    }

}
