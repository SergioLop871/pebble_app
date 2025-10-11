package com.example.pebble_app;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class FocusFragment extends Fragment {

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

        //Declaracion de identificadores de elementos graficos y de más apartir de aquí


        return view;
    }
}