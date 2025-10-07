package com.example.pebble_app;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;


import java.util.ArrayList;


public class MainMenu extends AppCompatActivity {

    //Variable para grafica de pastel
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Asignar variable
        pieChart = findViewById(R.id.pie_chart);


        /*---------------------------------OBTENER LOS DATOS--------------------------------------*/
        //Inicializar una lista de datos
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        //Crear datos (Ejemplo)
        for(int i = 1; i<=4; i++){
            String nombreApp = "App" + i;

            //Crear una dato (categoria)
                                  //PieEntry(horas,nombreApp)
            PieEntry pieEntry = new PieEntry(i,nombreApp); //PieEntry(valor, label)

            //Agregar el dato a la lista
            pieEntries.add(pieEntry);

        }
        //Incializar el dataset con la lista
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Aplicaciones");

        //Asginar el dataset a la grafica
        pieChart.setData(new PieData(pieDataSet));



        /*---------------------------------PERSONALIZACION----------------------------------------*/
        //Texto para el centro de la grafica
        String screenTimeString = "4h 15m\n";
        String screenTimeLabel = "Tiempo en pantalla";

        SpannableString s = new SpannableString(screenTimeString + screenTimeLabel);

        //Tamaño mayor al texto de las tiempo
        s.setSpan(new RelativeSizeSpan(1.5f), 0, screenTimeString.length(), 0);

        //Tamaño menor al texto de descripcion del tiempo
        s.setSpan(new RelativeSizeSpan(0.8f), screenTimeLabel.length(), screenTimeLabel.length(), 0);


        //Obtener la leyenda del pie chart (cajas en la esquina inferior izquierda)
        Legend l = pieChart.getLegend();

        //Deshabilitar la leyenda
        l.setEnabled(false);

        //Evitar que la grafica se gire al seleccionarla
        pieChart.setRotationEnabled(false);

        //Quitar desplazamiento al seleccionar una sección de la grafica
        pieDataSet.setSelectionShift(0f);

        //Cambiar la posición del valor y 'label' de cada categoria
        //Sacar los valores de cada catergoriafuera del circulo
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        //Ocultar los valores numericos de cada categoria
        pieDataSet.setDrawValues(false);

        /*Asignar la longitud a las lineas que conectan a los labels
        y valores con la grafica (Si estan fuera de la circulo)*/

        //pieDataSet.setValueLinePart1Length(0.2f); //Linea interior (Rango de 0 - 1)
        //pieDataSet.setValueLinePart2Length(0.2f); //Linea exterior (Rango de 0 - 1)

        //Ocultar lineas de labels y valores (fuera del circulo)
        pieDataSet.setValueLineColor(Color.TRANSPARENT); // O o Color.TRANSPARENT

        //Asginar los colores a la grafica
        pieDataSet.setColors(
                ContextCompat.getColor(this, R.color.pibbleLogoWater),
                ContextCompat.getColor(this, R.color.pibbleLogoSand),
                ContextCompat.getColor(this, R.color.piechartGray),
                ContextCompat.getColor(this, R.color.white)
        );
        //pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS); //default

        //Animacion en X, Y en milisegundos
        //pieChart.animateXY(5000,5000);

        //Ocultar descripcion
        pieChart.getDescription().setEnabled(false);

        //Cambiar color y tamaño de los labels de cada categoria
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);

        //Texto al centro
        pieChart.setCenterText(s);

        pieChart.setCenterTextColor(Color.parseColor("#FFFFFF"));

        //-----Fondo del centro (Agujero) de la grafica. (Varias opciones)
        //pieChart.setHoleColor(Color.LTGRAY);

        pieChart.setDrawHoleEnabled(true); //En caso de que no este activado

        // Colores personalizados
        //pieChart.setHoleColor(Color.parseColor("#000000"));
        pieChart.setHoleColor(ContextCompat.getColor(this, R.color.bgWidgetBlack)); //Desde colors.xml

        //Quitar circulo transparente que sirve como borde del Agujero (circulo central)
        pieChart.setTransparentCircleRadius(pieChart.getHoleRadius());

        //Redibujar
        pieChart.invalidate();

    }
}