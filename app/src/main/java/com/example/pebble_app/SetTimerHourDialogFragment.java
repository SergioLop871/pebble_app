package com.example.pebble_app;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class SetTimerHourDialogFragment extends DialogFragment {

    //Interfaz para asignar el rango de tiempo a CreateFocusSessionFragment
    public interface onSetTimeHour{
        void setTimerHour(int startHour, int startMinute);
    }


    private onSetTimeHour listener;
    private Fragment parent;

    private Button cancelBtn, setTimerHourBtn;


    private int startHourI, startMinuteI;
    private NumberPicker startHourNP, startMinuteNP;


    //Funcion para evitar que el usuario pueda cambiar los valores de numberPicker
    private void deleteNumberPickerEditText(NumberPicker np){
        for(int i = 0; i < np.getChildCount(); i++){
            View child = np.getChildAt(i);
            if(child instanceof EditText){
                EditText editText = (EditText) child;
                editText.setFocusable(false);
                editText.setClickable(false);
                editText.setLongClickable(false);
                editText.setCursorVisible(false);
                editText.setInputType(InputType.TYPE_NULL);
            }
        }
    }


    //Metodo para obtener al fragmento padre
    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        //Obtener la instancia de AddAplicationDialogFragment creada
        parent = getParentFragment();
        if(parent instanceof onSetTimeHour){
            listener = (onSetTimeHour) parent;
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_set_timer_hour, container, false);

        cancelBtn = view.findViewById(R.id.cancelButton); //Boton para cancelar
        setTimerHourBtn = view.findViewById(R.id.setTimerHourButton); //Boton para confirmar el tiempo

        String nPFormat = "%02d"; //Formato para los NumberPicker

        startHourI = 1; startMinuteI = 0; //Se inicializa el tiempo con 01 : 00

        startHourNP = view.findViewById(R.id.startHourNumberPicker);
        startMinuteNP = view.findViewById(R.id.startMinuteNumberPicker);

        cancelBtn.setOnClickListener(v -> {
            dismiss();
        });

        //Listener para confirmar el tiempo asginado
        setTimerHourBtn.setOnClickListener(v -> {
            int totalMinutes =  (startHourNP.getValue() * 60) + startMinuteNP.getValue();
            if(totalMinutes != 0){
                listener.setTimerHour(startHourNP.getValue(), startMinuteNP.getValue());
                dismiss();
            } else{
                Toast.makeText(getContext(),
                        "Elige un tiempo de al menos un minuto", Toast.LENGTH_SHORT).show();
            }


        });

        //Horas
        startHourNP.setMinValue(0);
        startHourNP.setMaxValue(99);
        startHourNP.setValue(startHourI);
        startHourNP.setFormatter(value -> String.format(nPFormat,value));

        //Minutos
        startMinuteNP.setMinValue(0);
        startMinuteNP.setMaxValue(59);
        startMinuteNP.setValue(startMinuteI);
        startMinuteNP.setFormatter(value -> String.format(nPFormat,value));

        //Borrar los editText para evitar que el usuario modifique los numeros directamente
        deleteNumberPickerEditText(startHourNP);
        deleteNumberPickerEditText(startMinuteNP);


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
