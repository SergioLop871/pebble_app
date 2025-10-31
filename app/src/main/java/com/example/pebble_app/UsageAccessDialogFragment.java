package com.example.pebble_app;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class UsageAccessDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_usage_access, container, false);

        Button continueBtn = view.findViewById(R.id.continueDialogButton);
        Button doNotAllowBtn = view.findViewById(R.id.doNotAllowDialogButton);

        continueBtn.setOnClickListener(v -> {
            dismiss();
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        });

        doNotAllowBtn.setOnClickListener(v -> {
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            // Evitar el cierre del diálogo con el botón "Back"
            getDialog().setCancelable(false);
            // Evitar el cierre del diálogo al tocar fuera de el
            getDialog().setCanceledOnTouchOutside(false);
        }
    }

}