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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentFocusSessionEdit#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFocusSessionEdit extends Fragment
        implements AddAplicationDialogRecyclerViewAdapter.OnCheckBoxSellected,
        SetHourRangeDialogFragment.onSetTimeRange {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    private LayoutInflater inflater; //LayoutInflater para colocar una chip en el chipgroup de apps
    private ChipGroup chipGroup; //Para obtener el chipGroup del layout
    private ImageButton goBackBtn; //Boton para volver atras

    private Button addAplicationBtn, saveEditBtn; //Boton para guardar cambios

    private EditText sessionNameET, sessionDescriptionET, sessionEmoticonET;

    private CardView setHoursRangeBtn; //Boton para seleccionar horas

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

    //Actualizar el rango de tiempo
    private SetHourRangeDialogFragment dialogSetHourRange;
    private String mParam1;
    private String mParam2;

    public FragmentFocusSessionEdit() {
        // Required empty public constructor
    }

    public void saveEditSession(){
        //Logica para editar el modo de enfoque (Cambiar en la base)
        Toast.makeText(getContext(), "Boton guardar presionado", Toast.LENGTH_SHORT).show();
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

    public static FragmentFocusSessionEdit newInstance(String param1, String param2) {
        FragmentFocusSessionEdit fragment = new FragmentFocusSessionEdit();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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

        chipGroup = view.findViewById(R.id.appChipGroup);

        //Listener para el boton de crear sesion de enfoque
        saveEditBtn.setOnClickListener(v -> {
            saveEditSession();
        });


        return view;
    }
}