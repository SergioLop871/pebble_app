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


    int startHoursSize, endHoursSize;

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

        setTimeRangeBtn.setOnClickListener(v -> {
            listener.setTimeRange(startHourNP.getValue(), startMinuteNP.getValue(),
                    startAmPmNPT.getValue(), endHourNP.getValue(), endMinuteNP.getValue(),
                    endAmPmNPT.getValue());
            dismiss();
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
