package com.example.pebble_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;

public class FocusFragment extends Fragment
        implements ChooseModeDialogFragment.OnModeSelectedListener {

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
                //((FocusContainerFragment) parent).openCreateTimerFragment();
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

    //Metodo para inicializar el RecyclerView
    void getFocusModesRowModels(){
       /*Implementar la logica al crear la base de datos
       * para obtener los modos creados.
       *
       * Actualizar el RecyclerView al obtener los modos
       * creados de la base
       */
    }


    // Metodo para crear un nuevo modo de enfoque (Sesión o temporizador)
    void createNewFocusMode(String focusModeName, String focusModeTime
            , String focusModeDays, String focusModeIcon, String focusModeType){

        /*
        * Aqui solo se deberian guardar los modos en la base de datos
        * e implementar el metodo "getFocusModesRowModels()" para
        * actualizar el RecyclerView
        *
        * Adicionalmente se deberia pasar un array con los nombres de las apps a
        * bloquear (nuevo fragmento para crear un modo)
        * */

        //Para guardar el tipo y subirlo a la base
        String newModeType = focusModeType;

        if(focusModeDays == null){
            focusModeDays = "";
        }

        //Crear el modelo para implementarlo en el recyclerview
        FocusModesRowModel newMode = new FocusModesRowModel(focusModeName, focusModeTime,
                focusModeDays, focusModeIcon);

        focusModesRowModels.add(newMode);
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

        //Idea: crear un nuevo fragmento para ir al presionar el boton para añadir
        //Crear los nuevos modos (ejemplo)
        for(int i = 0; i < 5; i++){
            if(i == 0){
                createNewFocusMode("Digital Detox", "1h 00m",
                        null, "\uD83E\uDEB4", "timer" );
            }
            else if (i == 1) {
                createNewFocusMode("Study Mode", "1h 30m",
                        null, "\uD83D\uDCDA", "timer" );
            }
            else if (i == 2) {
                createNewFocusMode("Work Mode", "9 AM - 5 PM",
                        "L     M     X     J     V",
                        "\uD83D\uDCBB", "session" );
            }
            else if (i == 3) {
                createNewFocusMode("Me Time", "6 PM - 10 PM",
                        "L     M     X     J     V",
                        "\uD83E\uDDD8", "session" );
            }
            else if (i == 4) {
                createNewFocusMode("Good Sleep", "10 PM - 6 AM",
                        "D     L     M     X     J     V     S",
                        "\uD83D\uDE34", "session" );
            }

        }

        //Crear el adapter para el recyclerView
        adapter = new FocusModesRecyclerViewAdapter(requireContext(), focusModesRowModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        return view;
    }
}