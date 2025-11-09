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


public class FragmentTimerEdit extends Fragment
        implements SetTimerHourDialogFragment.onSetTimeHour,
        AddAplicationDialogRecyclerViewAdapter.OnCheckBoxSellected{

    private ImageButton goBackBtn; //Almacenar el botón para volver

    private String timerName, timerDescription, timerEmoticon; //Para guardar los datos en cadenas

    //Almacenar los EditText del nombre y la descripción
    private EditText timerNameET, timerDescriptionET, timerEmoticonET;

    //Para almacenar el tiempo del temporizador
    private int startHour, startMinute;

    private TextView timerHourTV; //Para mostar el tiempo elegido al usuario
    private CardView setTimerHourBtn; //"Boton" para asginar el tiempo

    //Boton para añadir aplicaciones y crear el modo temporizador
    private Button addAplicationBtn, saveEditBtn;

    //Variable para almamcenar el dialogo que asigna el tiempo
    private SetTimerHourDialogFragment dialogSetTimerHour;

    //Variable para almamcenar el dialogo que añade aplicaciones
    private AddAplicationDialogFragment dialogAddAplication;

    //ChipGroup para mostar las aplicaciones seleccionadas
    private ChipGroup chipGroup;

    //LayoutInflater para colocar una chip en el chipgroup de apps
    private LayoutInflater inflater;

    //Array para guardar las aplicaciones seleccionadas
    private ArrayList<String> selectedApps;

    public FragmentTimerEdit() {
        // Required empty public constructor
    }



    public void setTimerHour(int startHour, int startMinute){

        String format = "%02d";
        String startHourFormat, startMinuteFormat;
        String timerHourString;
        this.startHour = startHour;
        this.startMinute = startMinute;

        //Añadir el formato para las horas y minutos
        startHourFormat   = String.format(format, this.startHour);
        startMinuteFormat = String.format(format, this.startMinute);

        //Mostar el tiempo elegido en el TextView
        timerHourString = startHourFormat + " : " + startMinuteFormat;
        timerHourTV.setText(timerHourString);

        Log.d("setTimerHour", "Hours & minutes: " + this.startHour
                + ":" + this.startMinute);


    }

    //Función para crear el temporizador (implemetar la logica necesaria)
    public void saveEditTimerMode(){
        //Logica para crear el timer
        boolean readyToCreate = true;

        //Revisar si no estan vacios algunos campos
        if(timerNameET.getText().toString().isEmpty()){
            Toast.makeText(getContext(),
                    "Pon un nombre al temporizador", Toast.LENGTH_SHORT).show();
            readyToCreate = false;
        }
        if(selectedApps.isEmpty()){
            Toast.makeText(getContext(),
                    "Selecciona al menos una aplicación", Toast.LENGTH_SHORT).show();
            readyToCreate = false;
        }

        //Crear el temporizador si esta listo
        if(readyToCreate){

            timerEmoticon = timerEmoticonET.getText().toString();
            timerName = timerNameET.getText().toString();
            timerDescription = timerDescriptionET.getText().toString();

            Toast.makeText(getContext(),
                    "Se ha creado el temporizador", Toast.LENGTH_SHORT).show();
            Log.d("createTimerMode", "Name: " + timerName);
            Log.d("createTimerMode", "Description: " + timerDescription);
            Log.d("createTimerMode", "Emoticon: " +timerEmoticon);
            Log.d("createTimerMode", "Time:  " + startHour + ":" + startMinute);
            Log.d("createTimerMode", "Selected Apps:  " + selectedApps.toString());

            Bundle savedChanges = new Bundle();
            savedChanges.putString("newEmoticon", timerEmoticon);
            savedChanges.putString("newName", timerName);
            savedChanges.putString("newDescription", timerDescription);
            savedChanges.putInt("newStartHour", startHour);
            savedChanges.putInt("newStartMinute", startMinute);
            savedChanges.putStringArrayList("newSelectedApps", selectedApps);

            getParentFragmentManager().setFragmentResult("editTimerResult", savedChanges);
            getParentFragmentManager().popBackStack(); //Regresar al fragmento anterior

        }
    }

    //Metodo para inicializar los datos de la sesión de enfoque a editar
    public void initSessionInfo(String timerName, String timerDescription,
                                String timerEmoticon, int startHour, int startMinute,
                                ArrayList<String> selectedApps){

        this.timerName = timerName;
        this.timerDescription = timerDescription;
        this.timerEmoticon = timerEmoticon;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.selectedApps = new ArrayList<>(selectedApps);

    }

    //Metodo para inicializar los elementos (EditText, TextView, ChipGroup, etc.) en la vista
    public void setElementsInfo(){
        timerNameET.setText(timerName);
        timerDescriptionET.setText(timerDescription);
        timerEmoticonET.setText(timerEmoticon);

        //Varaibles para iniciar el formato de timerHourTV
        String format = "%02d";
        String startHourFormat, startMinuteFormat;
        String timeRangeString;
        startHourFormat = String.format(format, this.startHour);
        startMinuteFormat = String.format(format, this.startMinute);


        //Añadir el formato de los minutos si estos son mayores a 0
        timeRangeString = startHourFormat + " : " + startMinuteFormat;

        timerHourTV.setText(timeRangeString);

        //Asginar los nombres de las aplicaciones obtenidas
        for(String appName : selectedApps){
            //Crear una chip
            inflater = LayoutInflater.from(requireContext());
            if(inflater != null || chipGroup == null || !isAdded()){
                Chip chip = (Chip) inflater.inflate(R.layout.create_timer_item_chip, chipGroup,false);
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

    //Metodo para crear un chip en el chipGroup
    @Override
    public void onChecked(String applicationName){
        //Crear una chip
        inflater = LayoutInflater.from(requireContext());
        if(inflater != null || chipGroup == null || !isAdded()){
            Chip chip = (Chip) inflater.inflate(R.layout.create_timer_item_chip, chipGroup,false);
            chip.setText(applicationName); //Asginar el nombre de la app
            chip.setTag(applicationName);
            chip.setOnCloseIconClickListener(v -> chipGroup.removeView(chip));
            chipGroup.addView(chip);

            //Añadir la app a la lista y actualizar en el dialogo
            selectedApps.add(applicationName);
            dialogAddAplication.setSelectedApps(selectedApps);
            Log.d("onChecked", selectedApps.toString());
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
        Log.d("onUnchecked", selectedApps.toString());
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer_edit, container, false);

        goBackBtn = view.findViewById(R.id.goBackButton); //Boton para volver atras

        timerNameET = view.findViewById(R.id.timerNameEditText);
        timerDescriptionET = view.findViewById(R.id.timerDescriptionEditText);
        timerEmoticonET = view.findViewById(R.id.timerEmoticonEditText);

        //Boton para seleccionar el tiempo del temporizador
        setTimerHourBtn = view.findViewById(R.id.setTimerHourButton);

        addAplicationBtn = view.findViewById(R.id.addAppsButton); // Boton para agregar apps
        chipGroup = view.findViewById(R.id.appChipGroup); // Obtener el chipgroup
        timerHourTV = view.findViewById(R.id.timerHoursTextView); // TextView para mostar el tiempo

        saveEditBtn = view.findViewById(R.id.saveEditTimerButton);

        //Listener para el boton de regresar
        goBackBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        //Listener para el boton de seleccionar el tiempo
        setTimerHourBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(),
                    "Click", Toast.LENGTH_SHORT).show();
            dialogSetTimerHour = new SetTimerHourDialogFragment();
            dialogSetTimerHour.show(getChildFragmentManager(), "setTimerHourDialog");
        });


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

        saveEditBtn.setOnClickListener(v -> {
            saveEditTimerMode();
        });

        setElementsInfo();
        return view;
    }
}