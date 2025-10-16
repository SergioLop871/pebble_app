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

public class ScreenTimeFragment extends Fragment {

    // Atributo para gráfica de pastel
    private PieChart pieChart;

    public ScreenTimeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen_time, container, false);

        // Asignar el elemento de la gráfica en el layout del fragmento
        pieChart = view.findViewById(R.id.pie_chart);

        // Crear objeto AppUsageStatics
        AppUsageStatistics appUsageStatistics = new AppUsageStatistics(requireContext());

        /*---------------------------------OBTENER LOS DATOS--------------------------------------*/
        // Obtener la lista de estadísticas de uso
        List<UsageStats> usageStats = appUsageStatistics.getUsageStatistics(UsageStatsManager.INTERVAL_MONTHLY);

        // Inicializar una lista de datos
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        // Crear datos (Ejemplo)
        for(int i = 1; i<=4; i++){
            String nombreApp = "App" + i;

            // Crear un dato (categoría)
            // PieEntry(horas,nombreApp)
            PieEntry pieEntry = new PieEntry(i,nombreApp); //PieEntry(valor, label)

            // Agregar el dato a la lista
            pieEntries.add(pieEntry);

        }
        // Inicializar el dataset con la lista
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Aplicaciones");

        // Asignar el dataset a la gráfica
        pieChart.setData(new PieData(pieDataSet));


        /*---------------------------------PERSONALIZACION----------------------------------------*/
        // Texto para el centro de la gráfica
        String screenTimeString = "4h 15m\n";
        String screenTimeLabel = "Tiempo en Pantalla";

        SpannableString s = new SpannableString(screenTimeString + screenTimeLabel);

        // Tamaño mayor al texto de las tiempo
        s.setSpan(new RelativeSizeSpan(1.5f), 0, screenTimeString.length(), 0);

        // Tamaño menor al texto de descripción del tiempo
        s.setSpan(new RelativeSizeSpan(0.8f), screenTimeLabel.length(), screenTimeLabel.length(), 0);


        // Obtener la leyenda del pie chart (cajas en la esquina inferior izquierda)
        Legend l = pieChart.getLegend();

        // Deshabilitar la leyenda
        l.setEnabled(false);

        // Evitar que la gráfica se gire al seleccionarla
        pieChart.setRotationEnabled(false);

        // Quitar desplazamiento al seleccionar una sección de la gráfica
        pieDataSet.setSelectionShift(0f);

        // Cambiar la posición del valor y 'label' de cada categoría
        // Sacar los valores de cada categoría fuera del círculo
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        // Ocultar los valores numéricos de cada categoría
        pieDataSet.setDrawValues(false);

        /* Asignar la longitud a las líneas que conectan a los labels
        y valores con la gráfica (Si están fuera del círculo) */

        //pieDataSet.setValueLinePart1Length(0.2f); //Linea interior (Rango de 0 - 1)
        //pieDataSet.setValueLinePart2Length(0.2f); //Linea exterior (Rango de 0 - 1)

        // Ocultar líneas de labels y valores (fuera del círculo)
        pieDataSet.setValueLineColor(Color.TRANSPARENT); // O o Color.TRANSPARENT

        // Asignar los colores a la gráfica
        pieDataSet.setColors(
                ContextCompat.getColor(requireContext(), R.color.pibbleLogoWater),
                ContextCompat.getColor(requireContext(), R.color.pibbleLogoSand),
                ContextCompat.getColor(requireContext(), R.color.piechartGray),
                ContextCompat.getColor(requireContext(), R.color.white)
        );
        //pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS); //default

        // Animación en X, Y en milisegundos
        pieChart.animateXY(1000,1000);

        // Ocultar descripción
        pieChart.getDescription().setEnabled(false);

        // Cambiar color y tamaño de los labels de cada categoría
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);

        // Texto al centro
        pieChart.setCenterText(s);

        pieChart.setCenterTextColor(Color.parseColor("#FFFFFF"));

        //-----Fondo del centro (Agujero) de la gráfica. (Varias opciones)
        //pieChart.setHoleColor(Color.LTGRAY);

        pieChart.setDrawHoleEnabled(true); //En caso de que no esté activado

        // Colores personalizados
        //pieChart.setHoleColor(Color.parseColor("#000000"));
        pieChart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.bgWidgetBlack)); // Desde colors.xml

        // Quitar círculo transparente que sirve como borde del Agujero (círculo central)
        pieChart.setTransparentCircleRadius(pieChart.getHoleRadius());

        // Redibujar
        pieChart.invalidate();

        return view;
    }
}