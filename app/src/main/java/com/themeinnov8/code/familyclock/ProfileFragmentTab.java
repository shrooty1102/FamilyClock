package com.themeinnov8.code.familyclock;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

/**
 * Created by code on 3/7/15.
 */
public class ProfileFragmentTab extends Fragment {

    private Switch gpsSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeComponents();

        return rootView;
    }

    private void initializeComponents() {

        gpsSwitch = (Switch) getView().findViewById(R.id.gpsSwitch);

    }

}
