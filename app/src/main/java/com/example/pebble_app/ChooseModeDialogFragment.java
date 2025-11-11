package com.example.pebble_app;

import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class ChooseModeDialogFragment extends DialogFragment {

    /*Se crea una interfaz para poder comunicarse con
    * FocusFragmentContainer a traves de FocusFragment
    * y poder realizar la transacción hacia
    * CreateFocusSessionFragment y
    * CreateFocusTimerFragment (Aun no implementado)
    *  */
    public interface OnModeSelectedListener{
        void onModeSelected(String mode);
    }

    /*
    * Este atributo/variable sirve para guardar una referencia
    * a la instancia FocusFragment, cuando se crea en
    * tiempo de ejecución
    * */
    private OnModeSelectedListener listener;

    /*Sobrescribir el metodo onAttach
    * de la clase Fragment para obtener el contexto,
    * el contexo sera el Fragmento que contiene al
    * DialogFragment, que es "FocusFragment"
    * */
    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        Fragment parent = getParentFragment(); //Obtener la instancia de FocusFragment creada

        //Verificar si la instancia implementa la interfaz
        if(parent instanceof OnModeSelectedListener){
            //Se castea la instancia para indicar al compilador que esta usa la interfaz
            listener = (OnModeSelectedListener) parent;
        }
    }

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

        //Listener para crear una sesión de enfoque
        sessionBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onModeSelected("session"); //Llamar a FocusContainerFragment
            }
            dismiss(); //Cerrar el dialogFragment
        });

        //Listener para crear un temporizador
        timerBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onModeSelected("timer"); //Llamar a FocusContainerFragment
            }
            dismiss(); //Cerrar el dialogFragment
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
