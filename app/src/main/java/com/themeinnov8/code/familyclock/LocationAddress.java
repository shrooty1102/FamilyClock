package com.themeinnov8.code.familyclock;

/**
 * Created by code on 3/8/15.
 */

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationAddress {

    private Context context;
    private Location location;
    private boolean isLocationAvailable;

    public LocationAddress(Context context, Location location, boolean isLocationAvailable) {
        this.context = context;
        this.location = location;
        this.isLocationAvailable = isLocationAvailable;
    }

    /**
     * Gives you complete address of the location
     * @return complete address in String
     */
    public String getLocationAddress() {

        if (isLocationAvailable) {

            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
// Get the current location from the input parameter list
// Create a list to contain the result address
            List<Address> addresses = null;
            try {
/*
* Return 1 address.
*/
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e1) {
                e1.printStackTrace();
                return ("IO Exception trying to get address:" + e1);
            } catch (IllegalArgumentException e2) {
// Error message to post in the log
                String errorString = "Illegal arguments "
                        + Double.toString(location.getLatitude()) + " , "
                        + Double.toString(location.getLongitude())
                        + " passed to address service";
                e2.printStackTrace();
                return errorString;
            }
// If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
// Get the first address
                Address address = addresses.get(0);
/*
* Format the first line of address (if available), city, and
* country name.
*/
                String addressText = String.format(
                        "%s, %s, %s",
// If there's a street address, add it
                        address.getMaxAddressLineIndex() > 0 ? address
                                .getAddressLine(0) : "",
// Locality is usually a city
                        address.getLocality(),
// The country of the address
                        address.getCountryName());
// Return the text
                return addressText;
            } else {
                return "No address found by the service: Note to the developers, If no address is found by google itself, there is nothing you can do about it. :(";
            }
        } else {
            return "Location Not available";
        }

    }

}