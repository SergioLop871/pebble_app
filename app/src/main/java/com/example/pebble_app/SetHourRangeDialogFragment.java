package com.example.pebble_app;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class SetHourRangeDialogFragment extends DialogFragment {

    //Interfaz para asignar el rango de tiempo a CreateFocusSessionFragment
    public interface onSetTimeRange{
        void setTimeRange(int startHour, int startMinute, int startAmPm,
                          int endHour, int endMinute, int endAmPm);
    }


    private onSetTimeRange listener;
    private Fragment parent;

    private Button cancelBtn, setTimeRangeBtn;


    private int startHourI, startMinuteI, startAmPmI, endHourI, endMinuteI, endAmPmI;
    private NumberPicker startHourNP, startMinuteNP, startAmPmNPT,
            endHourNP, endMinuteNP, endAmPmNPT;

    private String[] amPmTextArray = {"am","pm"};

    private Boolean settingNP;

    //Varaibles para verificar si el rango de tiempo es valido
    int startTotalMinutes, endTotalMinutes;


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

    //Metodo para saber si el rango de tiempo es valido (hora de inicio menor a la de fin)
    private Boolean checkValidTimeRange(){

        int startH = startHourNP.getValue();
        int startM = startMinuteNP.getValue();
        int startAmPm = startAmPmNPT.getValue();
        int endH = endHourNP.getValue();
        int endM = endMinuteNP.getValue();
        int endAmPm = endAmPmNPT.getValue();

        //Obtener el total de los minutos de la hora de inicio
                                                             //Si son las 12 am son las 0 horas (formato 24h)
        startTotalMinutes = (startAmPm == 1) ? startH + 12 : ((startH == 12) ? 0 : startH); // Obtener las horas (formato 24h)
        startTotalMinutes = (startTotalMinutes * 60) + startM; // Obtener los minutos totales

        //Obtener el totoal de los minutos de la hora de fin
        endTotalMinutes = (endAmPm == 1) ? (endH + 12) : endH; // Obtener las horas (formato 24h)
        endTotalMinutes = (endTotalMinutes * 60) + endM; // Obtener los minutos totales


        return startTotalMinutes < endTotalMinutes; //Verificar si las hora de inicio es menor a la de fin
    }

    //Metodo para obtener al fragmento padre
    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        //Obtener la instancia de AddAplicationDialogFragment creada
        parent = getParentFragment();
        if(parent instanceof onSetTimeRange){
            listener = (onSetTimeRange) parent;
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_set_hour_range, container, false);

        cancelBtn = view.findViewById(R.id.cancelButton);
        setTimeRangeBtn = view.findViewById(R.id.setTimeRangeButton);

        String nPFormat = "%02d";

        startHourI = 8; startMinuteI = 0; startAmPmI = 0;
        endHourI = 9; endMinuteI = 0; endAmPmI = 0;

        startHourNP = view.findViewById(R.id.startHourNumberPicker);
        startMinuteNP = view.findViewById(R.id.startMinuteNumberPicker);
        startAmPmNPT = view.findViewById(R.id.startAmPmNumberPickerText);
        endHourNP = view.findViewById(R.id.endHourNumberPicker);
        endMinuteNP = view.findViewById(R.id.endMinuteNumberPicker);
        endAmPmNPT = view.findViewById(R.id.endAmPmNumberPickerText);

        cancelBtn.setOnClickListener(v -> {
            dismiss();
        });

        //Boton para confirmar el rango de tiempo
        setTimeRangeBtn.setOnClickListener(v -> {
            if(checkValidTimeRange()){
                listener.setTimeRange(startHourNP.getValue(), startMinuteNP.getValue(),
                        startAmPmNPT.getValue(), endHourNP.getValue(), endMinuteNP.getValue(),
                        endAmPmNPT.getValue());
                dismiss();
            } else{
                Toast.makeText(getContext(),
                        "Rango invalido: la hora de inicio debe ser menor a la de fin",
                        Toast.LENGTH_LONG).show();
            }

        });

        NumberPicker[] numberPickers = { startHourNP, startMinuteNP, startAmPmNPT,
                endHourNP, endMinuteNP, endAmPmNPT };

        //Horas de inicio y fin
        startHourNP.setMinValue(1);
        startHourNP.setMaxValue(12);
        startHourNP.setValue(startHourI);
        startHourNP.setFormatter(value -> String.format(nPFormat,value));

        endHourNP.setMinValue(1);
        endHourNP.setMaxValue(12);
        endHourNP.setValue(endHourI);
        endHourNP.setFormatter(value -> String.format(nPFormat,value));

        //Minutos de inicio y fin
        startMinuteNP.setMinValue(0);
        startMinuteNP.setMaxValue(59);
        startMinuteNP.setValue(0);
        startMinuteNP.setFormatter(value -> String.format(nPFormat,value));
        endMinuteNP.setMinValue(0);
        endMinuteNP.setMaxValue(59);
        endMinuteNP.setValue(0);
        endMinuteNP.setFormatter(value -> String.format(nPFormat,value));

        //Am,Pm de inicio y fin
        startAmPmNPT.setMinValue(0);
        startAmPmNPT.setMaxValue(1);
        startAmPmNPT.setDisplayedValues(amPmTextArray);
        endAmPmNPT.setMinValue(0);
        endAmPmNPT.setMaxValue(1);
        endAmPmNPT.setDisplayedValues(amPmTextArray);

        //Verificar si la hora de incio es mayor a la de fin para reducirla y vicevesa
        for(NumberPicker np : numberPickers){
            deleteNumberPickerEditText(np);
            np.setOnValueChangedListener((numberPicker, oldValue, newValue) -> {
                int id = numberPicker.getId();

                //Verificar si el usuario modifico un number picker (endHourNP o endAmPmNPT)
                if (id == R.id.endHourNumberPicker) {
                    endHourI = newValue;
                    /*Si se coloca la hora de fin en 12,
                    * endAmPmI se cambia a PM, para evitar conflictos
                    * con los dias seleccionados y que
                    * la hora de fin no sea menor a la de inicio */
                    if(endHourI == 12){ endAmPmNPT.setValue(1);}
                } else if (id == R.id.endAmPmNumberPickerText) {
                    endAmPmI = newValue;

                    if(endAmPmI == 0 && endHourI == 12){
                        /*Si endAmPmI es el indice "Am" y la hora fin
                        * es "12", la hora fin se cambia a 11 para
                        * evitar conflictos con los dias selecionados */
                        endHourNP.setValue(11);
                    }
                }

            });
        }
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
