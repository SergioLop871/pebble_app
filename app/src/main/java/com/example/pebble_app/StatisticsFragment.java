package com.example.pebble_app;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        //ImageButton para regresar a ScreenTimeFragment
        backButton = view.findViewById(R.id.backArrowImgButton);

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

        //
        barDataSet.setColors(
                ContextCompat.getColor(requireContext(), R.color.pibbleLogoWater),
                ContextCompat.getColor(requireContext(), R.color.pibbleLogoSand)
        );

        //Crear los datos para mostrar en la grafica
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);


        /*---------------------------------PERSONALIZACION----------------------------------------*/

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