package com.example.pebble_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class FocusContainerFragment extends Fragment {

    private FocusFragment focusFragment;

    public FocusContainerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_focus_container, container, false);

        if(focusFragment == null){
            focusFragment = new FocusFragment();
        }

        //Evitar agregar otro fragmento en cambio de config. (ej. rotar la pantalla)
        if (savedInstanceState == null){
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, focusFragment, "focus")
                    .commit();
        }

        return view;
    }
}