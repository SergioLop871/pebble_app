package com.example.pebble_app;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


public class FragmentFocusSessionVisualizer extends Fragment {

    //Elementos del visualizador
    private ImageButton goBackBtn;
    private Button deleteBtn, editBtn;
    private TextView emoticonTV, nameTV, descriptionTV, timeRangeTV;

    private CardView sundayBtn, mondayBtn, tuesdayBtn, wednesdayBtn, thursdayBtn,
            fridayBtn, saturdayBtn;


    //Variables para almacenar los datos y pasarlos a la seccion de editar
    private String emoticon, name, description;

    private int startHour, startMinute, startAmPm, endHour, endMinute, endAmPm;

    private ArrayList<String> selectedDays; //ArrayList para guardar los dias

    private ArrayList<String> selectedApps; //ArrayList para guardar las apps


    public FragmentFocusSessionVisualizer() {
        // Required empty public constructor
    }

    //Metodo llamado por el adapter para inicializar los datos
    public void initSessionInfo(String emoticon, String name, String description,
                                int startHour, int startMinute, int startAmPm,
                                int endHour, int endMinute, int endAmPm,
                                ArrayList<String> selectedDays,
                                ArrayList<String> selectedApps){

        //Inicializar los datos del elemento seleccionado
        this.emoticon = emoticon;
        this.name = name;
        this.description = description;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.startAmPm = startAmPm;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.endAmPm = endAmPm;
        this.selectedDays = new ArrayList<>(selectedDays);
        this.selectedApps = new ArrayList<>(selectedApps);

    }

    //Incializar la información para mostar al usuario
    public void initShowInfo(CardView[] dayButtons){

        //Inicializacion temporal para pruebas
        emoticon = "\uD83D\uDE34";
        name = "Good Sleep";
        description = "Preparate para dormir al activar el" +
                "modo de buen sueño, que bloquea las " +
                "aplicaciones que evitan que duermas. " +
                "Asigna un rango de horas y activa el " +
                "modo rutina si lo deseas.";
        startHour = 10;
        startMinute = 0;
        startAmPm = 0;
        endHour = 6;
        endMinute = 0;
        endAmPm = 1;



        //Varaibles para iniciar el formato de timeRangeTV
        String format = "%02d";
        String startMinuteFormat, startAmPmText, endMinuteFormat, endAmPmText;
        String timeRangeString;
        startAmPmText = (startAmPm == 0) ? "am" : "pm";
        endAmPmText = (endAmPm == 0) ? "am" : "pm";
        startMinuteFormat = String.format(format, this.startMinute);
        endMinuteFormat = String.format(format, this.endMinute);

        //Añadir el formato de los minutos si estos son mayores a 0
        timeRangeString = this.startHour
                + ((this.startMinute > 0) ? (":" + startMinuteFormat) : "")
                + " " + startAmPmText.toUpperCase() + " - " + this.endHour
                + ((this.endMinute > 0) ? (":" + endMinuteFormat) : "")
                + " " + endAmPmText.toUpperCase();

        //Asginar los valores para mostar al usuario
        emoticonTV.setText(emoticon);
        nameTV.setText(name);
        descriptionTV.setText(description);
        timeRangeTV.setText(timeRangeString);

        selectedDays = new ArrayList<>(Arrays.asList("L", "D", "X", "S"));

        selectedApps = new ArrayList<>(Arrays.asList("Settings", "Chrome", "YT Music"));

        for(CardView dayButton : dayButtons){
            if(dayButton.getChildAt(0) instanceof TextView){
                TextView childTV = (TextView) dayButton.getChildAt(0);
                if(selectedDays.contains(childTV.getText().toString())){
                    dayButton.setSelected(true);
                }
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_focus_session_visualizer, container, false);

        //Obtener los elementos del layout
        goBackBtn = view.findViewById(R.id.goBackButton); //Boton para volver atras
        deleteBtn = view.findViewById(R.id.deleteModeButton);
        emoticonTV = view.findViewById(R.id.emoticonTextView);
        nameTV = view.findViewById(R.id.nameTextView);
        descriptionTV = view.findViewById(R.id.descriptionTextView);
        timeRangeTV = view.findViewById(R.id.timeRangeTextView);

        //dias de la semana
        sundayBtn = view.findViewById(R.id.daysSundayButton);
        mondayBtn = view.findViewById(R.id.daysMondayButton);
        tuesdayBtn = view.findViewById(R.id.daysTuesdayButton);
        wednesdayBtn = view.findViewById(R.id.daysWednesdayButton);
        thursdayBtn = view.findViewById(R.id.daysThursdayButton);
        fridayBtn = view.findViewById(R.id.daysFridayButton);
        saturdayBtn = view.findViewById(R.id.daysSaturdayButton);

        CardView[] dayButtons = {sundayBtn, mondayBtn, tuesdayBtn, wednesdayBtn,
                thursdayBtn, fridayBtn, saturdayBtn};

        initShowInfo(dayButtons); //Se inicializan los elementos del layout

        editBtn = view.findViewById(R.id.editModeButton);

        goBackBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack(); //Eliminar el fragmento actual para volver
        });

        editBtn.setOnClickListener(v -> {
            FragmentFocusSessionEdit editSessionFragment = new FragmentFocusSessionEdit();
            editSessionFragment.initSessionInfo(name, description, emoticon, startHour, startMinute,
                    startAmPm, endHour, endMinute, endAmPm, selectedDays, selectedApps);
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            ft.replace(R.id.fragmentContainerView, editSessionFragment)
                    .addToBackStack(null)
                    .commit();

        });

        return view;
    }
}