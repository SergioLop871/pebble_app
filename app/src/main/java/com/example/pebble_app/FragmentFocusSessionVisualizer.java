package com.example.pebble_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


public class FragmentFocusSessionVisualizer extends Fragment {

    private ImageButton goBackBtn;

    private Button deleteBtn, editBtn;

    public FragmentFocusSessionVisualizer() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_focus_session_visualizer, container, false);

        //Boton para volver atras
        goBackBtn = view.findViewById(R.id.goBackButton);
        deleteBtn = view.findViewById(R.id.deleteModeButton);
        editBtn = view.findViewById(R.id.editModeButton);


        goBackBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack(); //Eliminar el fragmento actual para volver
        });


        return view;
    }
}