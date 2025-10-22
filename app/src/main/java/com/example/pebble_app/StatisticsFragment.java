package com.example.pebble_app;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {

    private BarChart barChart;
    private ImageButton backButton;

    //Botones del selector de esquema
    private ImageButton schemeBtnLeftArrow, schemeBtnRightArrow;
    private TextView schemeBtnToday, schemeBtnWeek, schemeBtnMonth, schemeBtnYear;

    private int schemeSelectorIndex;

    private ArrayList<String> appNames = new ArrayList<>();
    private ArrayList<Integer> appUsageHours = new ArrayList<>();

    //Si se crea el la instancia si parametros
    public StatisticsFragment() {
        // Required empty public constructor
    }

    /*Si se desea pasar los argumentos al crear una nueva instancia de
      StatisticsFragment (ej. new StatisticsFragment(names, hours);
     */
    public static StatisticsFragment newInstance(ArrayList<String> appNames,
                                                 ArrayList<Integer> appUsageHours) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("app_names", appNames);
        args.putIntegerArrayList("app_usage_hours", appUsageHours);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appNames = getArguments().getStringArrayList("app_names");
            appUsageHours = getArguments().getIntegerArrayList("app_usage_hours");
        }
    }

    void setScheme(String scheme){

        if(scheme.equals("today")){
            schemeBtnToday.setSelected(true);
            schemeSelectorIndex = 0;
        } else if (scheme.equals("week")) {
            schemeBtnWeek.setSelected(true);
            schemeSelectorIndex = 1;
        } else if (scheme.equals("month")){
            schemeBtnMonth.setSelected(true);
            schemeSelectorIndex = 2;
        } else if (scheme.equals("year")) {
            schemeBtnYear.setSelected(true);
            schemeSelectorIndex = 3;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        //ImageButton para regresar a ScreenTimeFragment
        backButton = view.findViewById(R.id.backArrowImgButton);

        //Obtener los botones del selector de esquema
        schemeBtnLeftArrow  = view.findViewById(R.id.schemeSelectorLeftArrow);
        schemeBtnRightArrow = view.findViewById(R.id.schemeSelectorRightArrow);
        schemeBtnToday = view.findViewById(R.id.schemeSelectorToday);
        schemeBtnWeek  = view.findViewById(R.id.schemeSelectorWeek);
        schemeBtnMonth = view.findViewById(R.id.schemeSelectorMonth);
        schemeBtnYear  = view.findViewById(R.id.schemeSelectorYear);

        //OnClickListener para los botones del selector de esquema
        View.OnClickListener schemeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int btnId = v.getId();
                String[] schemesPositions = {"today", "week", "month", "year"};

                schemeBtnToday.setSelected(false);
                schemeBtnWeek.setSelected(false);
                schemeBtnMonth.setSelected(false);
                schemeBtnYear.setSelected(false);

                if(btnId == R.id.schemeSelectorLeftArrow){
                    schemeSelectorIndex--;
                    if(schemeSelectorIndex < 0){
                        schemeSelectorIndex = 3;
                    }
                    setScheme(schemesPositions[schemeSelectorIndex]);
                } else if (btnId == R.id.schemeSelectorToday){
                    setScheme(schemesPositions[0]);
                } else if (btnId == R.id.schemeSelectorWeek) {
                    setScheme(schemesPositions[1]);
                } else if (btnId == R.id.schemeSelectorMonth) {
                    setScheme(schemesPositions[2]);
                } else if (btnId == R.id.schemeSelectorYear) {
                    setScheme(schemesPositions[3]);
                } else if (btnId == R.id.schemeSelectorRightArrow){
                    schemeSelectorIndex++;
                    if(schemeSelectorIndex > 3){
                        schemeSelectorIndex = 0;
                    }
                    setScheme(schemesPositions[schemeSelectorIndex]);
                }
            }
        };

        //Botones del selector esquema
        schemeBtnToday.setOnClickListener(schemeClickListener);
        schemeBtnWeek.setOnClickListener(schemeClickListener);
        schemeBtnMonth.setOnClickListener(schemeClickListener);
        schemeBtnYear.setOnClickListener(schemeClickListener);
        schemeBtnLeftArrow.setOnClickListener(schemeClickListener);
        schemeBtnRightArrow.setOnClickListener(schemeClickListener);

        //Dejar por defecto el esquema "Hoy"
        schemeSelectorIndex = 0;
        schemeBtnToday.setSelected(true);

        //OnClickListener para el boton de regresar atras
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Esto hace que regrese al fragmento anterior
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Asignar el elemento de la gráfica en el layout del fragmento
        barChart = view.findViewById(R.id.bar_chart);

        /*---------------------------------OBTENER LOS DATOS--------------------------------------*/
        //Para deshabilitar el toque en las barras de la grafica
        barChart.setTouchEnabled(false);

        ArrayList<BarEntry> barEntries =  new ArrayList<>();

        //Nombres de las apps
        ArrayList<String> xValues = new ArrayList<>();

        //------Iterar los ArrayList

        for(int i = 0; i < appNames.size(); i++){
            String appName = appNames.get(i);
            float appHours = appUsageHours.get(i);

            // BarEntry(index, appHours)
            BarEntry barEntry = new BarEntry(i, appHours);

            //Agregar el dato a la lista
            barEntries.add(barEntry);
            xValues.add(appName);
        }


        //Crear el datset
        BarDataSet barDataSet = new BarDataSet(barEntries, "Aplicaciones");


        /*---------------------------------PERSONALIZACION----------------------------------------*/

        //Asignar los colores para las barras
        barDataSet.setColors(
                ContextCompat.getColor(requireContext(), R.color.pibbleLogoWater),
                ContextCompat.getColor(requireContext(), R.color.pibbleLogoSand)
        );

        barDataSet.setValueTextSize(14f); //Tamaño del texto de los valores de las barras (en dp)

        //Crear los datos para mostrar en la grafica
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);


        //Eliminar el label de descripcion
        barChart.getDescription().setEnabled(false);

        //Color del valor de cada barra
        barDataSet.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.white));

        //Configurar el eje "Y"
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setLabelCount(10);
        yAxis.setAxisMaximum(24f);
        //Cambiar el color de los nombres de cada barra
        yAxis.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));


        //Configuracion de la linea del eje "Y"
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(ContextCompat.getColor(requireContext(), R.color.white));
        //yAxis.setDrawAxisLine(true);


        //Configurar el eje "X"
        XAxis xAxis = barChart.getXAxis(); //Obtener el eje X
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues)); //Asignar los labels a cada barra
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //Posición de los labels de cada barra
        xAxis.setLabelCount(xValues.size()); //Para asignar la cantidad de barras
        //Cambiar el color de los nombres de cada barra
        xAxis.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

        //Configuración de la linea del eje X
        xAxis.setAxisLineWidth(2f);
        xAxis.setAxisLineColor(ContextCompat.getColor(requireContext(), R.color.white));

        //Deshabitar la leyenda (cuadros que colores en la esquina inferior izquierda)
        barChart.getLegend().setEnabled(false);

        //No mostrar la cuadricula de fondo
        xAxis.setDrawGridLines(false);
        yAxis.setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisRight().setDrawLabels(true);


        //Actualizar la grafica con los datos
        barChart.invalidate();

        return view;
    }
}