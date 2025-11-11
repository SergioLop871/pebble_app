package com.example.pebble_app;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class CreateFocusSessionFragment extends Fragment
        implements AddAplicationDialogRecyclerViewAdapter.OnCheckBoxSellected,
        SetHourRangeDialogFragment.onSetTimeRange{

    //Botón para volver a FocusFragment
    private ImageButton backBtn;

    //Nombre y descripcion de la session de enfoque
    private EditText sessionNameET, sessionDescriptionET, sessionEmoticonET;

    //Rango de horas y minutos de la sesión
    private int sessionStartHour, sessionStartMinute, sessionEndHour, getSessionEndMinute;

    //LayoutInflater para colocar una chip en el chipgroup de apps
    private LayoutInflater inflater;

    //Para obtener el chipGroup del layout
    private ChipGroup chipGroup;

    //Botones del layout
    private Button addAplicationBtn, createSessionBtn;

    //Boton para seleccionar rango de horas
    private CardView setHoursRangeBtn;

    //Botones de los dias de la semana
    private CardView sunBtn, monBtn, tueBtn, wedBtn, thuBtn, friBtn, satBtn;

    //Variables para almacenar las horas de inicio y fin en forma de enteros
    private int startHour, startMinute, endHour, endMinute, startAmPm, endAmPm;

    private TextView timeRangeTV; //TV para mostar el rango de tiempo al asginarlo

    private String startAmPmText, endAmPmText; //Para mostar el formato AM/PM

    //ArrayList para pasar las aplicaciones dentro del chipGroup a AddAplicationDialogFragment
    private ArrayList<String> selectedApps = new ArrayList<>();

    //ArrayLisr para almacenar los dias seleccionados
    private ArrayList<String> selectedDays = new ArrayList<>();

    //Almacenar el dialogo para actualizar las aplicaciones seleccionadas en tiempo real
    private AddAplicationDialogFragment dialogAddAplication;

    private SetHourRangeDialogFragment dialogSetHourRange;

    public CreateFocusSessionFragment() {
        // Required empty public constructor
    }

    //Función para crear la sesion (implemetar la logica necesaria)
    public void createSession(){
        //Logica para crear la sesión
        boolean readyToCreate = true;

        //Revisar si no estan vacios algunos campos
        if(sessionNameET.getText().toString().isEmpty()){
            Toast.makeText(getContext(),
                    "Pon un nombre a la session de enfoque", Toast.LENGTH_SHORT).show();
            readyToCreate = false;
        }
        if(selectedApps.isEmpty()){
            Toast.makeText(getContext(),
                    "Selecciona al menos una aplicación", Toast.LENGTH_SHORT).show();
            readyToCreate = false;
        }

        //Crear la sesion si esta lista
        if(readyToCreate){
            String name = sessionNameET.getText().toString();
            String description = sessionDescriptionET.getText().toString();
            String emoji = sessionEmoticonET.getText().toString();
            // Dividir por el guión
            String[] parts = timeRangeTV.getText().toString().split("-");
            // Quitar espacios sobrantes
            String begin_time = parts[0].trim();
            String end_time = parts[1].trim();
            ArrayList<String> days = selectedDays;
            ArrayList<String> apps = selectedApps;

            Log.d("createTimerMode", "Name: " + name);
            Log.d("createTimerMode", "Description: " + description);
            Log.d("createTimerMode", "Emoticon: " + emoji);
            Log.d("createTimerMode", "Time range:  " + begin_time + " - " + end_time);
            Log.d("createTimerMode", "Selected days:  " + days.toString());
            Log.d("createTimerMode", "Selected Apps:  " + apps.toString());

            DatabaseHelper myDB = new DatabaseHelper(requireContext());
            myDB.addFocusSession(name, description, emoji, begin_time, end_time, days, apps);

            getParentFragmentManager().popBackStack(); //Regresar al fragmento anterior
        }
    }

    //Metodo de la interfaz usada para obtener el tiempo desde el dialogFragment para el rango de tiempo
    @Override
    public void setTimeRange(int startHour, int startMinute, int startAmPm,
                      int endHour, int endMinute, int endAmPm){

        String format = "%02d";
        String startMinuteFormat, endMinuteFormat;
        String timeRangeString;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.startAmPm = startAmPm;
        this.endAmPm = endAmPm;
        this.startAmPmText = (startAmPm == 0) ? "am" : "pm";
        this.endAmPmText = (endAmPm == 0) ? "am" : "pm";

        startMinuteFormat = String.format(format, this.startMinute);

        endMinuteFormat = String.format(format, this.endMinute);

        //Añadir el formato de los minutos si estos son mayores a 0
        timeRangeString = this.startHour
                + ((this.startMinute > 0) ? (":" + startMinuteFormat) : "")
                + " " + this.startAmPmText.toUpperCase() + " - " + this.endHour
                + ((this.endMinute > 0) ? (":" + endMinuteFormat) : "")
                + " " + this.endAmPmText.toUpperCase();

        timeRangeTV.setText(timeRangeString);

        //Log.d("setTimeRange", "TextView: " + timeRangeTV.getText());

        Log.d("setTimeRange", "Start: " + this.startHour
                + ":" + this.startMinute + " " + this.startAmPm);

        Log.d("setTimeRange", "End: " + this.endHour
                + ":" + this.endMinute + " " + this.endAmPm);

    }

    //Metodo para crear un chip en el chipGroup
    @Override
    public void onChecked(String applicationName){
        //Crear una chip
        inflater = LayoutInflater.from(requireContext());
        if(inflater != null || chipGroup == null || !isAdded()){
            Chip chip = (Chip) inflater.inflate(R.layout.create_session_item_chip, chipGroup,false);
            chip.setText(applicationName); //Asginar el nombre de la app
            chip.setTag(applicationName);
            chip.setOnCloseIconClickListener(v -> chipGroup.removeView(chip));
            chipGroup.addView(chip);

            //Añadir la app a la lista y actualizar en el dialogo
            selectedApps.add(applicationName);
            dialogAddAplication.setSelectedApps(selectedApps);
        }
    }

    //Metodo para eliminar un chip del chipGroup
    @Override
    public void onUnchecked(String applicationName){
        for(int i = 0; i < chipGroup.getChildCount(); i++){
            View child = chipGroup.getChildAt(i);
            if(child instanceof Chip){
                Chip chip = (Chip) child;
                if(applicationName.equals(chip.getTag())){
                    Log.d("onUnchecked", "Chip a eliminar: " + chip.getTag());
                    chipGroup.removeView(chip);

                    //Borrar la app de la lista y actualizar en el dialogo
                    selectedApps.remove(applicationName);
                    dialogAddAplication.setSelectedApps(selectedApps);
                    break;
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_focus_session, container, false);

        backBtn = view.findViewById(R.id.goBackButton); //Obtener el boton de volver
        addAplicationBtn = view.findViewById(R.id.addAppsButton); //Boton para agregar aplicaciones
        createSessionBtn = view.findViewById(R.id.createModeButton); //Boton para crear la session


        sessionNameET = view.findViewById(R.id.focusSessionNameEditText);
        sessionDescriptionET = view.findViewById(R.id.focusSessionDescriptionEditText);
        sessionEmoticonET = view.findViewById(R.id.focusSessionEmoticonEditText);

        //Obtener el botón de asignar rango de horas y minutos
        setHoursRangeBtn = view.findViewById(R.id.setHoursRangeButton);
        //TV para mostrar el rango de tiempo seleccionado
        timeRangeTV = view.findViewById(R.id.timeRangeTextView);


        backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack(); //Regresar al fragmento anterior
        });

        setHoursRangeBtn.setOnClickListener(v -> {
            dialogSetHourRange = new SetHourRangeDialogFragment(); //Dialogo para asignar rango de hora
            dialogSetHourRange.show(getChildFragmentManager(), "setHourRangeDialog");
        });

        //Obtener los botones de los dias
        sunBtn = view.findViewById(R.id.daysSundayButton);
        monBtn = view.findViewById(R.id.daysMondayButton);
        tueBtn = view.findViewById(R.id.daysTuesdayButton);
        wedBtn = view.findViewById(R.id.daysWednesdayButton);
        thuBtn = view.findViewById(R.id.daysThursdayButton);
        friBtn = view.findViewById(R.id.daysFridayButton);
        satBtn = view.findViewById(R.id.daysSaturdayButton);

        //Crear un arreglo de botones
        CardView[] dayButtons = {sunBtn, monBtn, tueBtn, wedBtn, thuBtn, friBtn, satBtn};

        //Asignar un onClickListener a los botnes de los dias
        for(CardView card : dayButtons){
            card.setOnClickListener(v -> {
                //Cambiar el estado del boton a seleccionado o no seleccionado
                v.setSelected(!v.isSelected());
                //Obtener el TextView hijo dentro del cardview
                TextView dayText = (TextView) ((CardView) v).getChildAt(0);
                String selectedDay = dayText.getText().toString();
                if(v.isSelected()){
                    selectedDays.add(selectedDay); //Añadir si esta seleccionado
                } else{
                    selectedDays.remove(selectedDay); //Eliminar si no esta seleccionado
                }
                Log.d("SelectedDays", selectedDays.toString()); //Mostar los dias en consola
            });
        }



        //Boton para crear el dialogFragment de añadir apps
        addAplicationBtn.setOnClickListener(v -> {
            dialogAddAplication = new AddAplicationDialogFragment();  //Crear el fragmento
            selectedApps.clear(); //Borrar la lista
            //Añadir el nombre de las aplicaciones en el chipgroup a selectedApps
            for(int i = 0; i < chipGroup.getChildCount(); i++){
                View child = chipGroup.getChildAt(i);
                if(child instanceof Chip){
                    Chip chip = (Chip) child;
                    selectedApps.add(chip.getText().toString());
                }
            }
            //Pasar los nombres de apps al dialogo
            dialogAddAplication.setSelectedApps(selectedApps);
            //Mostrar dialogo
            dialogAddAplication.show(getChildFragmentManager(), "addAplicationDialog");
        });

        chipGroup = view.findViewById(R.id.appChipGroup);

        //Listener para el boton de crear sesion de enfoque
        createSessionBtn.setOnClickListener(v -> {
            createSession();
        });

        return view;
    }

}