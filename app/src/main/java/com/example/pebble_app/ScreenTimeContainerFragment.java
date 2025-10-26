package com.example.pebble_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ScreenTimeContainerFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ScreenTimeFragment screenTimeFragment;

    public ScreenTimeContainerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen_time_container, container, false);

        if(screenTimeFragment == null){
            screenTimeFragment = new ScreenTimeFragment();
        }

        //Evitar agregar otro fragmento en cambio de config. (ej. rotar la pantalla)
        if (savedInstanceState == null){
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, screenTimeFragment, "screenTime")
                    .commit();
        }

        // Inflate the layout for this fragment
        return view;
    }

}