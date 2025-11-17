package com.example.pebble_app;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
    private ArrayList<String> appPackageNames = new ArrayList<>(); // Para obtener los íconos
    private ArrayList<Long> appUsageTimeMs = new ArrayList<>(); // Tiempos en milisegundos para mostrar correctamente

    //Para los btn del selector de esquema
    private ImageButton schemeBtnLeftArrow, schemeBtnRightArrow;
    private TextView schemeBtnToday, schemeBtnWeek, schemeBtnMonth, schemeBtnYear;

    private  int schemeSelectorIndex;

    //Btn y texto para la información de la fecha según el esquema
    private ImageButton schemeInfoBtnLeftArrow, schemeInfoBtnRightArrow;
    private TextView schemeDateTextView;
    
    //TextView para mostrar el tiempo en pantalla en el CardView
    private TextView infoScreenTimeText;
    
    //TextView para mostrar las consultas (pick-ups) en el CardView
    private TextView infoFocusQueriesText;
    
    //TextView para mostrar el puntaje de enfoque en el CardView
    private TextView infoFocusScoreText;

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
            
            // Obtener la fecha actual y formatearla
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM", new Locale("es", "ES"));
            String currentDate = dateFormat.format(calendar.getTime());
            // Capitalizar la primera letra del día
            if (currentDate.length() > 0) {
                currentDate = currentDate.substring(0, 1).toUpperCase() + currentDate.substring(1);
            }
            schemeDateTextView.setText(currentDate);
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
        
        //Obtener el TextView que muestra el tiempo en pantalla en el CardView
        infoScreenTimeText = view.findViewById(R.id.infoScreenTimeText);
        
        //Obtener el TextView que muestra las consultas (pick-ups) en el CardView
        infoFocusQueriesText = view.findViewById(R.id.infoFocusQueriesText);
        
        //Obtener el TextView que muestra el puntaje de enfoque en el CardView
        infoFocusScoreText = view.findViewById(R.id.infoFocusScoreText);

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
        
        // Actualizar la fecha al iniciar con el esquema "Hoy"
        setScheme("today");

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
                infoApps.putStringArrayList("app_package_names", appPackageNames);
                infoApps.putLongArray("app_usage_time_ms", convertToLongArray(appUsageTimeMs));

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
        appPackageNames.clear();
        appUsageTimeMs.clear();

        //Deshabilitar el toque en las secciones de la grafica
        pieChart.setTouchEnabled(false);

        // Obtener la lista de estadísticas de uso
        List<UsageStats> usageStats = appUsageStatistics.getUsageStatistics(UsageStatsManager.INTERVAL_DAILY);
        
        // Obtener PackageManager para obtener nombres de aplicaciones
        PackageManager packageManager = requireContext().getPackageManager();
        
        // Inicializar una lista de datos
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        
        // Calcular tiempo total en pantalla
        long totalScreenTime = 0;
        
        // Verificar si hay datos disponibles
        if (usageStats != null && !usageStats.isEmpty()) {
            // Procesar las top 3 aplicaciones más usadas
            int topAppsCount = Math.min(usageStats.size(), 3);
            long otherAppsTime = 0; // Tiempo acumulado de las demás aplicaciones
            
            // Procesar las top 3 aplicaciones
            for (int i = 0; i < topAppsCount; i++) {
                UsageStats stat = usageStats.get(i);
                long timeInMs = stat.getTotalTimeInForeground();
                
                // Convertir milisegundos a minutos para la gráfica
                float timeInMinutes = timeInMs / (1000f * 60f);
                
                // Obtener el nombre de la aplicación desde el package name
                String appName;
                try {
                    ApplicationInfo appInfo = packageManager.getApplicationInfo(stat.getPackageName(), 0);
                    appName = packageManager.getApplicationLabel(appInfo).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    // Si no se encuentra el nombre, usar el package name
                    appName = stat.getPackageName();
                    Log.w("ScreenTimeFragment", "No se pudo obtener el nombre de la app: " + stat.getPackageName());
                }
                
                // Truncar el nombre si es muy largo para evitar que se amontone en la gráfica
                String appNameForChart = truncateAppName(appName, 12);
                
                // Agregar nombre al ArrayList de nombres (nombre completo)
                appNames.add(appName);
                
                // Agregar package name para obtener el ícono después
                appPackageNames.add(stat.getPackageName());
                
                // Agregar tiempo en milisegundos para mostrar correctamente
                appUsageTimeMs.add(timeInMs);
                
                // Convertir minutos a horas para el ArrayList (redondeado)
                int hours = (int) (timeInMinutes / 60f);
                appUsageHours.add(hours);
                
                // Sumar al tiempo total
                totalScreenTime += timeInMs;
                
                // Crear un dato (categoría) para la gráfica con nombre truncado
                // PieEntry usa minutos como valor
                PieEntry pieEntry = new PieEntry(timeInMinutes, appNameForChart);
                
                // Agregar el dato a la lista
                pieEntries.add(pieEntry);
            }
            
            // Calcular el tiempo de las demás aplicaciones (desde la 4ta en adelante)
            // Agregar cada aplicación individual al RecyclerView, pero agruparlas como "Otro" en la gráfica
            if (usageStats.size() > 3) {
                // Primero, calcular el tiempo total de "Otro" para la gráfica
                for (int i = 3; i < usageStats.size(); i++) {
                    UsageStats stat = usageStats.get(i);
                    otherAppsTime += stat.getTotalTimeInForeground();
                }
                
                // Si hay tiempo acumulado de otras apps, agregar cada una individualmente al RecyclerView
                if (otherAppsTime > 0) {
                    // Convertir milisegundos a minutos para la gráfica
                    float otherAppsTimeInMinutes = otherAppsTime / (1000f * 60f);
                    
                    // Agregar cada aplicación individual al RecyclerView
                    for (int i = 3; i < usageStats.size(); i++) {
                        UsageStats stat = usageStats.get(i);
                        long timeInMs = stat.getTotalTimeInForeground();
                        
                        // Convertir milisegundos a minutos
                        float timeInMinutes = timeInMs / (1000f * 60f);
                        
                        // Obtener el nombre de la aplicación desde el package name
                        String appName;
                        try {
                            ApplicationInfo appInfo = packageManager.getApplicationInfo(stat.getPackageName(), 0);
                            appName = packageManager.getApplicationLabel(appInfo).toString();
                        } catch (PackageManager.NameNotFoundException e) {
                            // Si no se encuentra el nombre, usar el package name
                            appName = stat.getPackageName();
                            Log.w("ScreenTimeFragment", "No se pudo obtener el nombre de la app: " + stat.getPackageName());
                        }
                        
                        // Agregar nombre al ArrayList de nombres
                        appNames.add(appName);
                        
                        // Agregar package name para obtener el ícono después
                        appPackageNames.add(stat.getPackageName());
                        
                        // Agregar tiempo en milisegundos
                        appUsageTimeMs.add(timeInMs);
                        
                        // Convertir minutos a horas para el ArrayList (redondeado)
                        int hours = (int) (timeInMinutes / 60f);
                        appUsageHours.add(hours);
                    }
                    
                    // Sumar al tiempo total
                    totalScreenTime += otherAppsTime;
                    
                    // Crear entrada "Otro" para la gráfica (agrupado)
                    PieEntry otherEntry = new PieEntry(otherAppsTimeInMinutes, "Otro");
                    pieEntries.add(otherEntry);
                }
            }
        }
        
        // Inicializar el dataset con la lista (puede estar vacía si no hay datos)
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Aplicaciones");

        // Asignar el dataset a la gráfica
        pieChart.setData(new PieData(pieDataSet));


        /*---------------------------------PERSONALIZACION----------------------------------------*/
        // Calcular tiempo total en horas y minutos para el texto del centro
        long totalMinutes = totalScreenTime / (1000 * 60);
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        
        // Texto para el centro de la gráfica
        String screenTimeString = hours + "h " + minutes + "m\n";
        String screenTimeLabel = "Tiempo en Pantalla";
        
        // Actualizar el TextView del CardView con el tiempo en pantalla
        if (infoScreenTimeText != null) {
            String screenTimeCardText = hours + "h " + minutes + "m";
            infoScreenTimeText.setText(screenTimeCardText);
        }
        
        // Obtener y mostrar la cantidad de pick-ups (desbloqueos)
        int pickUpsCount = appUsageStatistics.getPickUpsCount();
        if (infoFocusQueriesText != null) {
            infoFocusQueriesText.setText(String.valueOf(pickUpsCount));
        }
        
        // Calcular y mostrar el puntaje de enfoque
        // El enfoque se calcula con la cantidad de tiempo desconectado en el día respecto a la hora actual
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        long startOfDay = cal.getTimeInMillis();
        long now = System.currentTimeMillis();
        long totalDayTime = now - startOfDay; // Tiempo total transcurrido desde medianoche
        
        // Tiempo desconectado = Tiempo total del día - Tiempo total de uso
        long disconnectedTime = totalDayTime - totalScreenTime;
        
        // Calcular el porcentaje de enfoque
        int focusScore = 0;
        if (totalDayTime > 0) {
            focusScore = (int) ((disconnectedTime * 100) / totalDayTime);
        }
        
        // Asegurar que el puntaje esté entre 0 y 100
        focusScore = Math.max(0, Math.min(100, focusScore));
        
        // Actualizar el TextView del puntaje de enfoque
        if (infoFocusScoreText != null) {
            infoFocusScoreText.setText(focusScore + "%");
        }

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
        // Sacar los labels fuera del círculo
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        // Ocultar los valores numéricos de cada categoría
        pieDataSet.setDrawValues(false);

        // Configurar las líneas que conectan los labels con la gráfica
        pieDataSet.setValueLinePart1Length(0.3f); // Longitud de la línea interior
        pieDataSet.setValueLinePart2Length(0.4f); // Longitud de la línea exterior
        pieDataSet.setValueLinePart1OffsetPercentage(80f); // Offset de la línea interior
        pieDataSet.setValueLineColor(Color.WHITE); // Color de las líneas
        pieDataSet.setValueLineWidth(1f); // Ancho de las líneas

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

        // Cambiar color y tamaño de los labels de cada categoría (aumentado)
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(13f);
        
        // Configurar el offset para evitar que los labels se corten
        pieChart.setExtraOffsets(12f, 12f, 12f, 12f);
        
        // Habilitar el uso de porcentajes para los labels si es necesario
        pieChart.setUsePercentValues(false);
        
        // Configurar el ángulo mínimo para las secciones (evita secciones muy pequeñas)
        pieChart.setMinAngleForSlices(20f);
        
        // Configurar el espaciado entre las secciones para mejor visualización
        pieChart.setDrawEntryLabels(true);
        
        // Habilitar el dibujado de los labels fuera del círculo
        pieChart.setDrawSliceText(true);

        // Texto al centro
        pieChart.setCenterText(s);

        pieChart.setCenterTextColor(Color.parseColor("#FFFFFF"));

        //-----Fondo del centro (Agujero) de la gráfica. (Varias opciones)
        //pieChart.setHoleColor(Color.LTGRAY);

        pieChart.setDrawHoleEnabled(true); //En caso de que no esté activado

        // Colores personalizados
        //pieChart.setHoleColor(Color.parseColor("#000000"));
        pieChart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.bgWidgetBlack)); // Desde colors.xml
        
        // Reducir el tamaño del agujero para que la gráfica se vea más grande
        pieChart.setHoleRadius(40f); // Porcentaje del radio (por defecto es 50f)

        // Quitar círculo transparente que sirve como borde del Agujero (círculo central)
        pieChart.setTransparentCircleRadius(pieChart.getHoleRadius());

        // Redibujar
        pieChart.invalidate();

        return view;
    }

    /*
     * Método para truncar nombres de aplicaciones largos
     * Parámetros:
     * - name: El nombre de la aplicación
     * - maxLength: La longitud máxima permitida
     * Retorno: El nombre truncado con "..." si es necesario
     */
    private String truncateAppName(String name, int maxLength) {
        if (name == null) {
            return "";
        }
        if (name.length() <= maxLength) {
            return name;
        }
        return name.substring(0, maxLength - 3) + "...";
    }
    
    /*
     * Método auxiliar para convertir ArrayList<Long> a long[]
     */
    private long[] convertToLongArray(ArrayList<Long> list) {
        long[] array = new long[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

}