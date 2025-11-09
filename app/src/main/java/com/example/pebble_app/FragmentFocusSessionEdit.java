package com.example.pebble_app;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
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


public class FragmentFocusSessionEdit extends Fragment
        implements AddAplicationDialogRecyclerViewAdapter.OnCheckBoxSellected,
        SetHourRangeDialogFragment.onSetTimeRange {


    private LayoutInflater inflater; //LayoutInflater para colocar una chip en el chipgroup de apps
    private ChipGroup chipGroup; //Para obtener el chipGroup del layout
    private ImageButton goBackBtn; //Boton para volver atras

    private Button addAplicationBtn, saveEditBtn; //Boton para guardar cambios

    private String sessionName, sessionDescription, sessionEmoticon; //Para guardar los datos en cadenas
    private EditText sessionNameET, sessionDescriptionET, sessionEmoticonET;

    private CardView setHoursRangeBtn; //Boton para seleccionar horas

    //Botones de los dias de la semana
    private CardView sunBtn, monBtn, tueBtn, wedBtn, thuBtn, friBtn, satBtn;

    //Variables para almacenar las horas de inicio y fin en forma de enteros
    private int startHour, startMinute, endHour, endMinute, startAmPm, endAmPm;

    private TextView timeRangeTV; //TV para mostar el rango de tiempo al asginarlo

    private String startAmPmText, endAmPmText; //Para mostar el formato AM/PM

    //ArrayList para pasar las aplicaciones dentro del chipGroup a AddAplicationDialogFragment
    private ArrayList<String> selectedApps;

    //ArrayLisr para almacenar los dias seleccionados
    private ArrayList<String> selectedDays;

    //Almacenar el dialogo para actualizar las aplicaciones seleccionadas en tiempo real
    private AddAplicationDialogFragment dialogAddAplication;

    //Actualizar el rango de tiempo
    private SetHourRangeDialogFragment dialogSetHourRange;

    public FragmentFocusSessionEdit() {
        // Required empty public constructor
    }

    public void saveEditSession(){
        //Logica para editar el modo de enfoque (Cambiar en la base)
        Toast.makeText(getContext(), "Boton guardar presionado", Toast.LENGTH_SHORT).show();
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
            sessionName = sessionNameET.getText().toString();
            sessionDescription = sessionDescriptionET.getText().toString();
            sessionEmoticon = sessionEmoticonET.getText().toString();

            Toast.makeText(getContext(),
                    "Se ha creado la sesión", Toast.LENGTH_SHORT).show();
            Log.d("createTimerMode", "Name: " + sessionName);
            Log.d("createTimerMode", "Description: " + sessionDescription);
            Log.d("createTimerMode", "Emoticon: " + sessionEmoticon);
            Log.d("createTimerMode", "Time range:  " + timeRangeTV.getText().toString());
            Log.d("createTimerMode", "Selected days:  " + selectedDays.toString());
            Log.d("createTimerMode", "Selected Apps:  " + selectedApps.toString());


            Bundle savedChanges = new Bundle();
            savedChanges.putString("newEmoticon", sessionEmoticon);
            savedChanges.putString("newName", sessionName);
            savedChanges.putString("newDescription", sessionDescription);
            savedChanges.putInt("newStartHour", startHour);
            savedChanges.putInt("newStartMinute", startMinute);
            savedChanges.putInt("newStartAmPm", startAmPm);
            savedChanges.putInt("newEndHour", endHour);
            savedChanges.putInt("newEndMinute", endMinute);
            savedChanges.putInt("newEndAmPm", endAmPm);
            savedChanges.putStringArrayList("newSelectedDays", selectedDays);
            savedChanges.putStringArrayList("newSelectedApps", selectedApps);

            getParentFragmentManager().setFragmentResult("editSessionResult", savedChanges);
            getParentFragmentManager().popBackStack();



        }
    }

    //Metodo para inicializar los datos de la sesión de enfoque a editar
    public void initSessionInfo(String sessionName, String sessionDescription,
                                String sessionEmoticon, int startHour, int startMinute,
                                int startAmPm, int endHour, int endMinute, int endAmPm,
                                ArrayList<String> selectedDays, ArrayList<String> selectedApps){

        this.sessionName = sessionName;
        this.sessionDescription = sessionDescription;
        this.sessionEmoticon = sessionEmoticon;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.startAmPm = startAmPm;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.endAmPm = endAmPm;

        this.selectedDays = new ArrayList<>(selectedDays);
        this.selectedApps = new ArrayList<>(selectedApps);

    }

    //Metodo para inicializar los elementos (EditText, TextView, ChipGroup, etc.) en la vista
    public void setElementsInfo(CardView[] dayButtons){
        sessionNameET.setText(sessionName);
        sessionDescriptionET.setText(sessionDescription);
        sessionEmoticonET.setText(sessionEmoticon);

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

        timeRangeTV.setText(timeRangeString);

        //Seleccionar los dias de la semana obtenidos
        for(CardView dayButton : dayButtons){
            if(dayButton.getChildAt(0) instanceof TextView){
                TextView childTV = (TextView) dayButton.getChildAt(0);
                if(selectedDays.contains(childTV.getText().toString())){
                    dayButton.setSelected(true);
                }
            }
        }

        //Asginar los nombres de las aplicaciones obtenidas
        for(String appName : selectedApps){
            //Crear una chip
            inflater = LayoutInflater.from(requireContext());
            if(inflater != null || chipGroup == null || !isAdded()){
                Chip chip = (Chip) inflater.inflate(R.layout.create_session_item_chip, chipGroup,false);
                chip.setText(appName); //Asginar el nombre de la app
                chip.setTag(appName);
                chip.setOnCloseIconClickListener(v -> {
                    chipGroup.removeView(chip);
                    selectedApps.remove(appName);
                });
                chipGroup.addView(chip);
            }
        }

    }

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
        View view = inflater.inflate(R.layout.fragment_focus_session_edit, container, false);

        goBackBtn = view.findViewById(R.id.goBackButton); //Obtener el botón de volver
        addAplicationBtn = view.findViewById(R.id.addAppsButton); //Botón para agregar aplicaciones
        saveEditBtn = view.findViewById(R.id.saveEditModeButton); //Botón para guardar los cambios

        sessionNameET = view.findViewById(R.id.focusSessionNameEditText); //Nombre de la session
        sessionDescriptionET = view.findViewById(R.id.focusSessionDescriptionEditText); // Descripción
        sessionEmoticonET = view.findViewById(R.id.focusSessionEmoticonEditText); //Emoticono

        chipGroup = view.findViewById(R.id.appChipGroup); //Obtener el chipgroup

        //Obtener el botón de asignar rango de horas y minutos
        setHoursRangeBtn = view.findViewById(R.id.setHoursRangeButton);
        //TV para mostrar el rango de tiempo seleccionado
        timeRangeTV = view.findViewById(R.id.timeRangeTextView);

        //Boton de volver
        goBackBtn.setOnClickListener(v -> {
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

        setElementsInfo(dayButtons);

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
                Log.d("SelectedDays edit", selectedDays.toString()); //Mostar los dias en consola
            });
        }

        //Listener para crear el dialogFragment de añadir apps
        addAplicationBtn.setOnClickListener(v -> {
            dialogAddAplication = new AddAplicationDialogFragment(); //Crear el fragmento
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



        //Listener para el boton de crear sesion de enfoque
        saveEditBtn.setOnClickListener(v -> {
            saveEditSession();
        });


        return view;
    }
}