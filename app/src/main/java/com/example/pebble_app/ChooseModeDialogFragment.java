package com.example.pebble_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

public class ChooseModeDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_choose_create_mode, container, false);

        //Obtener los elementos a usar
        ImageButton closeDialogBtn = view.findViewById(R.id.closeCreateModeDialog);
        CardView sessionBtn = view.findViewById(R.id.createSessionButton);
        CardView timerBtn = view.findViewById(R.id.createTimerButton);

        //Listeners para los botones a usar
        closeDialogBtn.setOnClickListener(v -> {
            dismiss();
        });

        sessionBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Modo Sesión seleccionado", Toast.LENGTH_SHORT).show();
            dismiss(); // cerrar el diálogo
        });

        timerBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Modo Temporizador seleccionado", Toast.LENGTH_SHORT).show();
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
        }
    }

}
