package com.example.pebble_app;

import android.content.Context;
import android.util.Log;
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

public class FocusModesRecyclerViewAdapter
        extends RecyclerView.Adapter<FocusModesRecyclerViewAdapter.MyViewHolder> {

    //Contexto y ArrayList para el constructor
    private Context context;
    private ArrayList<FocusModesRowModel> focusModesRowModels;

    //Interfaz para avisar a FocusFragment, y este se comunique con FocusFragmentContainer y cambien en fragmento
    public interface OnCardViewClickListener{
        void onCardViewClicked(String mode);
    }

    private OnCardViewClickListener listener;

    /*Constructor para obtener el contexto y el ArrayList con los elementos creados en
      *FocusFragment (cambiar por el nuevo fragmento)
    */
    public FocusModesRecyclerViewAdapter(Context context,
                                            ArrayList<FocusModesRowModel> focusModesRowModels,
                                         OnCardViewClickListener listener)
    {
        this.context = context;
        this.focusModesRowModels = focusModesRowModels;
        this.listener = listener;
    }

    //Metodos creados para el funcionamiento del Adaptador del RecyclerView
    @NonNull
    @Override
    public FocusModesRecyclerViewAdapter.MyViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Metodo creado para inflar el layout

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.mode_focus_recycler_view_row,
                parent, false);

        return new FocusModesRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FocusModesRecyclerViewAdapter.MyViewHolder holder,
                                 int position) {
        /* Metodo para asignar los valores a las vistas que sean visibles dentro
         *  del recyclerView (en pantalla) usando el layout de mode_focus_recycler_view_row
         *
         *  Se basa en la posición del recycler view para mostrar o no mostrar elementos
         * */

        //Obtener el cardview (contenedor) de la fila actual
        CardView cardView = holder.itemView.findViewById(R.id.recyclerFocusModeRowCard);

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

        //Abrir el fragmento para visualizar el modo creado (ver detalles)
        cardView.setOnClickListener(v -> {
            listener.onCardViewClicked(focusModesRowModels.get(position).getFocusModeType());
            try{
                Log.d("FocusModesRecyclerViewAdapter", "cardViewClicked: " + focusModesRowModels.get(position).getFocusModeType());
            }catch (Exception e){
                e.printStackTrace();
            }

        });

        //Asignar los valores de cada elemento creado
        holder.focusModeName.setText(focusModesRowModels.get(position).getFocusModeName());
        holder.focusModeTime.setText(focusModesRowModels.get(position).getFocusModeTime());
        holder.focusModeDays.setText(focusModesRowModels.get(position).getFocusModeDays());
        holder.focusModeIcon.setText(focusModesRowModels.get(position).getFocusModeIcon());


        cardView.setLayoutParams(params);

    }

    @Override
    public int getItemCount() {
        //Metodo para contar los elementos que hay total dentro del recyclerView

        return focusModesRowModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        /*
         * Clase creada para asignar los valores a los elementos del CardView de
         * cada fila
         * */

        TextView focusModeName, focusModeTime, focusModeDays, focusModeIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //Elementos del layout de statistics_recycler_view_row.xml
            focusModeName = itemView.findViewById(R.id.recyclerFocusModeName);
            focusModeTime = itemView.findViewById(R.id.recyclerFocusModeTime);
            focusModeDays = itemView.findViewById(R.id.recyclerFocusModeDays);
            focusModeIcon = itemView.findViewById(R.id.recyclerFocusModeIcon);

        }
    }

}
