package com.example.pebble_app;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class FocusFragment extends Fragment
        implements ChooseModeDialogFragment.OnModeSelectedListener,
        FocusModesRecyclerViewAdapter.OnCardViewClickListener{

    /*
    * Aqui van las variables para almacenar la info que se muestra
    * en el fragmento de manera mas detallada en otro fragmento
    * (ej. articulo de lista -> vista detallada del articulo)
    *
    * */

    //Claves para cada valor del argumento
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Ejemplo:
    //private static final String ARG_NOMBRE_PRODUCTO = "param1";
    //private static final String ARG_PRECIO = "param2";
    //private static final String ARG_EXISTENCIAS = "param3";

    //Valores de los argumentos obtenidos
    private String mParam1;
    private String mParam2;

    //Variable para crear el recyclerView
    private RecyclerView recyclerView;

    //Variable para crear el adaptador del recyclerView
    private FocusModesRecyclerViewAdapter adapter;

    //ArrayList para crear los elementos del recyclerView
    private ArrayList<FocusModesRowModel> focusModesRowModels = new ArrayList<>();

    // ArrayLists para los datos de las columnas de la tabla de sesión de enfoque
    ArrayList<String> focus_session_name, focus_session_emoji, focus_session_begin_time, focus_session_end_time;
    ArrayList<ArrayList<String>> focus_session_days;
    ArrayList<Integer> focus_session_ids;

    // Database Helper
    DatabaseHelper myDB;

    private ImageButton streakDescriptionBtn, settingsBtn, createModeBtn;


    //Constructor vacio
    public FocusFragment() {
        // Required empty public constructor
    }

    /*
    * Crea una nueva instancia del fragmento para pasarle los datos deseados
    * horas de las aplicaciones, nombres, etc. para una vista mas detallada.
    *
    * Para esto FocusFrament newInstance(), crea un Bundle para almacenar
    * los datos en forma clave-valor donde ARG_PARAM1 es la clave y
    * param1 es la variable donde se almacena el valor.
    *
    * */
    public static FocusFragment newInstance(String param1, String param2) {
        FocusFragment fragment = new FocusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //Para poder cambiar de visualizar el modo seleccionado del ReyclerView
    @Override
    public void onCardViewClicked(int sessionId, String mode){
        Fragment parent = getParentFragment();
        if(mode.equals("session")){
            if(parent instanceof FocusContainerFragment){
                ((FocusContainerFragment) parent).openSessionFragment(sessionId);
            }
        } else if (mode.equals("timer")) {
            if(parent instanceof FocusContainerFragment){
                ((FocusContainerFragment) parent).openTimerFragment();
            }
        }
    }

    /*Se sobrescribe el metodo de la interfaz para que ChooseModeDialogFragment
    * se pueda comunicar con FocusFragment, y FocusFragment con
    * FocusContainerFragment, para poder cambiar de Fragmento que permite
    * crear un modo de enfoque
    * */
    @Override
    public void onModeSelected(String mode) {
        if (mode.equals("session")) {
            Fragment parent = getParentFragment();
            if (parent instanceof FocusContainerFragment) {
                ((FocusContainerFragment) parent).openCreateSessionFragment();
            }
        } else if (mode.equals("timer")) {
            Fragment parent = getParentFragment();
            if (parent instanceof FocusContainerFragment) {
                ((FocusContainerFragment) parent).openCreateTimerFragment();
            }
        }
    }


    /*Creación "logica" del fragmento
    *
    * Se inicializan los datos y se leen los argumentos
    * asignados al llamar a newInstance()
    *
    * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // Metodo para almacenar los datos obtenidos de la BD (Local) de la tabla sesión de enfoque en Arrays
    void storeDataInArrays () {
        myDB = new DatabaseHelper(requireContext());

        // Inicializar arreglos de la tabla de sesión de enfoque
        focus_session_ids = new ArrayList<>();
        focus_session_name = new ArrayList<>();
        focus_session_emoji = new ArrayList<>();
        focus_session_begin_time = new ArrayList<>();
        focus_session_end_time = new ArrayList<>();
        focus_session_days = new ArrayList<>();

        // Obtener los datos de la tabla de sesión de enfoque
        Cursor cursor = myDB.readAllFocusData();
        if (cursor.getCount() == 0) {
            Toast.makeText(requireContext(), "No hay datos", Toast.LENGTH_LONG);
        }
        else {
            while (cursor.moveToNext()) {
                // Obtener el id de la sesión
                int sessionId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));

                // Guardar los datos en los arreglos
                focus_session_ids.add(sessionId);
                focus_session_name.add(cursor.getString(1));
                focus_session_emoji.add(cursor.getString(3));
                focus_session_begin_time.add(cursor.getString(4));
                focus_session_end_time.add(cursor.getString(5));

                // Obtener los días asociados
                Cursor daysCursor = myDB.ReadSessionDays(sessionId);
                ArrayList<String> days = new ArrayList<>();

                if (daysCursor.getCount() == 0) {
                    Toast.makeText(requireContext(), "Error al obtener los horarios", Toast.LENGTH_LONG);
                }
                else {
                    while (daysCursor.moveToNext()) {
                        days.add(daysCursor.getString(daysCursor.getColumnIndexOrThrow("week_day")));
                    }
                }

                // Agregar los días a la lista principal
                focus_session_days.add(days);
            }
        }
    }

    // Metodo para guardar las sesiones de enfoque obtenidas de la base de datos (Local) en focusModesRowModels
    void createFocusModesRowModels(){
        String focusModeType = "session";

        for (int i = 0; i < focus_session_name.size(); i++) {
            int sessionId = focus_session_ids.get(i);
            String focusModeName = focus_session_name.get(i);
            String focusModeIcon = focus_session_emoji.get(i);
            String focusModeBeginTime = focus_session_begin_time.get(i);
            String focusModeEndTime = focus_session_end_time.get(i);
            String focusModeDays = String.join("     ", focus_session_days.get(i));

            //Crear el modelo para implementarlo en el recyclerview
            FocusModesRowModel newMode = new FocusModesRowModel(sessionId, focusModeName, focusModeIcon,
                    focusModeBeginTime, focusModeEndTime, focusModeDays, focusModeType);

            focusModesRowModels.add(newMode);
        }
    }

    /* Creacion visual del fragmento (interfaz grafica)
    *
    * Se conectan los elementos (buttons, textview, etc) con
    * el layout XML para inflarlo (colocar los elementos visuales)
    *
    * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_focus, container, false);

        recyclerView = view.findViewById(R.id.focusModesRecyclerView);

        //Definición de los botones
        streakDescriptionBtn = view.findViewById(R.id.streakDescriptionButton);
        settingsBtn = view.findViewById(R.id.settingsButton);
        createModeBtn = view.findViewById(R.id.createModeButton);

        streakDescriptionBtn.setOnClickListener(v -> {
            StreakDescriptionDialogFragment dialog = new StreakDescriptionDialogFragment();
            dialog.show(getChildFragmentManager(), "streakDescriptionDialog");
        });

        createModeBtn.setOnClickListener(v -> {
            ChooseModeDialogFragment dialog = new ChooseModeDialogFragment();
            dialog.show(getChildFragmentManager(), "chooseModeDialog");
        });

        focusModesRowModels.clear();

        //Crear el adapter para el recyclerView
        adapter = new FocusModesRecyclerViewAdapter(requireContext(), focusModesRowModels, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Cargar datos desde la base de datos y mostrarlos en el RecyclerView
        storeDataInArrays();
        createFocusModesRowModels();
        adapter.notifyDataSetChanged();

        return view;
    }
}