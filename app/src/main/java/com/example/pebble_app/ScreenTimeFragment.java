package com.example.pebble_app;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class ScreenTimeFragment extends Fragment{

    // Atributo para gráfica de pastel
    private PieChart pieChart;

    //Para almacenar una sola instancia de statisticsFragment
    Fragment statisticsFragment;

    /*ArrayLists temporales para ver el funcionamiento del
    * paso de un bundle (info) del fragmento de ScreenTime a
    * Statistics*/
    private ArrayList<String> appNames = new ArrayList<>();
    private ArrayList<Integer> appUsageHours = new ArrayList<>();

    //Para los btn del selector de esquema
    private ImageButton schemeBtnLeftArrow, schemeBtnRightArrow;
    private TextView schemeBtnToday, schemeBtnWeek, schemeBtnMonth, schemeBtnYear;

    private  int schemeSelectorIndex;

    //Btn y texto para la información de la fecha según el esquema
    private ImageButton schemeInfoBtnLeftArrow, schemeInfoBtnRightArrow;
    private TextView schemeDateTextView;

    //Para cambiar entre fechas (solo esquema semanal, mensual y anual)
    private int schemeDateInfoIndex; //* no implementado aún

    public ScreenTimeFragment() {
        // Required empty public constructor
    }

    /*
    * SetCheme() para cambiar la información en la gráfica pastel de acuerdo al esquema
    * (Aún no implementado)
    * */
    void setScheme(String scheme){

        //View.INVISIBLE: el elemento ocupa el espacio pero no es visible
        //View.VISIBLE: el elemento es visible
        //View.GONE: el elemento no ocupa espacio y no es visible (eliminado)

        if(scheme.equals("today")){
            schemeInfoBtnLeftArrow.setVisibility(View.INVISIBLE);
            schemeInfoBtnRightArrow.setVisibility(View.INVISIBLE);
            schemeBtnToday.setSelected(true);
            schemeDateTextView.setText("Mar, 23 Sep");
            schemeSelectorIndex = 0;
        } else if (scheme.equals("week")) {
            schemeInfoBtnLeftArrow.setVisibility(View.VISIBLE);
            schemeInfoBtnRightArrow.setVisibility(View.VISIBLE);
            schemeBtnWeek.setSelected(true);
            schemeDateTextView.setText("Mar, 23 sep - Mar, 30 sep");
            schemeSelectorIndex = 1;
        } else if (scheme.equals("month")){
            schemeInfoBtnLeftArrow.setVisibility(View.VISIBLE);
            schemeInfoBtnRightArrow.setVisibility(View.VISIBLE);
            schemeBtnMonth.setSelected(true);
            schemeDateTextView.setText("Septiembre");
            schemeSelectorIndex = 2;
        } else if (scheme.equals("year")) {
            schemeInfoBtnLeftArrow.setVisibility(View.VISIBLE);
            schemeInfoBtnRightArrow.setVisibility(View.VISIBLE);
            schemeBtnYear.setSelected(true);
            schemeDateTextView.setText("2025");
            schemeSelectorIndex = 3;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen_time, container, false);

        //Obtener textos (botones) para el selector de esquema de la gráfica
        schemeBtnLeftArrow  = view.findViewById(R.id.schemeSelectorLeftArrow);
        schemeBtnRightArrow = view.findViewById(R.id.schemeSelectorRightArrow);

        schemeBtnToday = view.findViewById(R.id.schemeSelectorToday);
        schemeBtnWeek  = view.findViewById(R.id.schemeSelectorWeek);
        schemeBtnMonth = view.findViewById(R.id.schemeSelectorMonth);
        schemeBtnYear  = view.findViewById(R.id.schemeSelectorYear);

        //Obtener el TextView que muestra la fecha o fechas dependiendo el esquema
        schemeInfoBtnLeftArrow = view.findViewById(R.id.schemeDateInfoLeftArrow);
        schemeInfoBtnRightArrow = view.findViewById(R.id.schemeDateInfoRightArrow);

        schemeDateTextView = view.findViewById(R.id.schemeDateInfo);

        //Crear una sola instancia de statisticsFragment
        statisticsFragment = new StatisticsFragment();

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

        //Hacer invisibles los btn de las flechas (selector de fecha) en el esquema de "Hoy"
        schemeInfoBtnLeftArrow.setVisibility(View.INVISIBLE);
        schemeInfoBtnRightArrow.setVisibility(View.INVISIBLE);

        // Asignar el elemento de la gráfica en el layout del fragmento
        pieChart = view.findViewById(R.id.pie_chart);

        // Crear objeto AppUsageStatics
        AppUsageStatistics appUsageStatistics = new AppUsageStatistics(requireContext());

        //---------------------OnClickListener para ir a "StatisticsFragment"-----------------------
        pieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Crear el fragmento en caso de no existir
                if(statisticsFragment == null){
                    statisticsFragment = new StatisticsFragment();
                }

                //Crear la transición
                FragmentTransaction transaction = getParentFragmentManager()
                        .beginTransaction();

                //Crear el bundle para pasar al fragmento
                Bundle infoApps = new Bundle();

                //Pasar los ArrayList al bundle con una clave
                infoApps.putStringArrayList("app_names", appNames);
                infoApps.putIntegerArrayList("app_usage_hours", appUsageHours);

                //Pasar el bundle al fragmento
                statisticsFragment.setArguments(infoApps);

                //Hacer una animación al ir y volver del fragmento nuevo
                transaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                );

                // Reemplazar el fragmento actual por StatisticsFragment
                transaction.replace(R.id.fragmentContainerView, statisticsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        /*---------------------------------OBTENER LOS DATOS--------------------------------------*/
        //Vaciar los ArrayList para no volver a llenarlos con los mismos datos
        appNames.clear();
        appUsageHours.clear();

        //Deshabilitar el toque en las secciones de la grafica
        pieChart.setTouchEnabled(false);

        // Obtener la lista de estadísticas de uso
        List<UsageStats> usageStats = appUsageStatistics.getUsageStatistics(UsageStatsManager.INTERVAL_MONTHLY);

        // Inicializar una lista de datos
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        // Crear datos (Ejemplo)
        for(int i = 1; i<=4; i++){
            String nombreApp = "App" + i;

            //Agregar nombre al ArrayList de nombres
            appNames.add(nombreApp);

            //Agregar horas de app al ArrayList de horas
            appUsageHours.add(i);

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