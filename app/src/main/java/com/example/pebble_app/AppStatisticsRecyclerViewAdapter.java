package com.example.pebble_app;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AppStatisticsRecyclerViewAdapter
        extends RecyclerView.Adapter<AppStatisticsRecyclerViewAdapter.MyViewHolder> {

    //Contexto y ArrayList para el constructor
    private Context context;
    private ArrayList<AppStatisticsRowModel> appStatisticsRowModels;

    /*Constructor para obtener el contexto y el ArrayList con los elementos creados en
      StatisticsFragment
    */
    public AppStatisticsRecyclerViewAdapter(Context context,
                                            ArrayList<AppStatisticsRowModel> appStatisticsRowModels)
    {
        this.context = context;
        this.appStatisticsRowModels = appStatisticsRowModels;
    }

    //Metodos creados para el funcionamiento del Adaptador del RecyclerView
    @NonNull
    @Override
    public AppStatisticsRecyclerViewAdapter.MyViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Metodo creado para inflar el layout

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.statistics_recycler_view_row,
                parent, false);

        return new AppStatisticsRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppStatisticsRecyclerViewAdapter.MyViewHolder holder,
                                 int position) {
        /* Metodo para asignar los valores a las vistas que sean visibles dentro
        *  del recyclerView (en pantalla) usando el layout de statistics_recycler_view_row
        *
        *  Se basa en la posición del recycler view para mostrar o no mostrar elementos
        * */

        //Obtener el cardview (contenedor) de la fila actual
        CardView cardView = holder.itemView.findViewById(R.id.statisticsRecyclerRowCard);

        //Obtener los parametros del cardview
        ViewGroup.MarginLayoutParams params =
                (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();

        //Cambiar el bottomMargin del cardview
        if(position == getItemCount() - 1){ //Si es el ultimo cardview, bottomMargin de 0
            params.bottomMargin = 0;
        } else{ //Si es el primero o el penultimo, bottomMargin de 30
            params.bottomMargin = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 30, cardView.getResources().getDisplayMetrics());
        }

        //Asignar los valores de cada elemento creado
        holder.appNameTV.setText(appStatisticsRowModels.get(position).getAppName());
        holder.appTypeTV.setText(appStatisticsRowModels.get(position).getAppType());
        holder.appTimeTV.setText(appStatisticsRowModels.get(position).getAppTime());

        holder.appIconIV.setImageResource(appStatisticsRowModels.get(position).getAppIcon());
        holder.appStateIV.setImageResource(appStatisticsRowModels.get(position).getAppState());

        cardView.setLayoutParams(params);

    }

    @Override
    public int getItemCount() {
        //Metodo para contar los elementos que hay total dentro del recyclerView

        return appStatisticsRowModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        /*
        * Clase creada para asignar los valores a los elementos del CardView de
        * cada fila
        * */

        TextView appNameTV, appTypeTV, appTimeTV;
        ImageView appIconIV, appStateIV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            //Elementos del layout de statistics_recycler_view_row.xml
            appNameTV = itemView.findViewById(R.id.statisticsRecyclerRowName);
            appTypeTV = itemView.findViewById(R.id.statisticsRecyclerRowType);
            appTimeTV = itemView.findViewById(R.id.statisticsRecyclerRowTime);

            appIconIV = itemView.findViewById(R.id.statisticsRecyclerRowIcon);
            appStateIV = itemView.findViewById(R.id.statisticsRecyclerRowState);


        }
    }

}
