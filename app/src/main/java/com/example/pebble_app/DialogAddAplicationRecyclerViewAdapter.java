package com.example.pebble_app;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DialogAddAplicationRecyclerViewAdapter
        extends RecyclerView.Adapter<DialogAddAplicationRecyclerViewAdapter.MyViewHolder> {

    //Se crea una interfaz para poder comunicarse con CreateFocusSessionFragment
    public interface OnCheckBoxSellected{
        void onChecked(String applicactionName);
        void onUnchecked(String applicationName);
    }

    private OnCheckBoxSellected listener;
    //Contexto y ArrayList para el constructor
    private Context context;
    private ArrayList<DialogAddAplicationRowModel> dialogAddAplicationRowModels;

    private ArrayList<String> selectedApps;

    //ArrayList para el funcionamiento del buscador en AddAplicationDialogFragment
    private  ArrayList<DialogAddAplicationRowModel> fullList;

    /*Constructor para obtener el contexto y el ArrayList con las applicaciones
    obtenidas de AddAplicationDialogFragment,
    Se pasa el listener que representa a CreateFocusSessionFragment
    */
    public DialogAddAplicationRecyclerViewAdapter(Context context,
                                            ArrayList<DialogAddAplicationRowModel> dialogAddAplicationRowModels,
                                                  ArrayList<String> selectedApps,
                                                  OnCheckBoxSellected listener)
    {
        this.context = context;
        this.dialogAddAplicationRowModels = dialogAddAplicationRowModels;
        this.selectedApps = selectedApps;
        this.listener = listener;

        //Guardar copia del la lista original
        this.fullList = new ArrayList<>(dialogAddAplicationRowModels);
    }


    //Metodos creados para el funcionamiento del Adaptador del RecyclerView
    @NonNull
    @Override
    public DialogAddAplicationRecyclerViewAdapter.MyViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Metodo creado para inflar el layout

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.add_app_dialog_recycler_view_row,
                parent, false);

        return new DialogAddAplicationRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DialogAddAplicationRecyclerViewAdapter.MyViewHolder holder,
                                 int position) {
        /* Metodo para asignar los valores a las vistas que sean visibles dentro
         *  del recyclerView (en pantalla) usando el layout de add_app_dialog_recycler_view_row
         * */

        //Obtener el cardview (contenedor) de la fila actual
        CardView cardView = holder.itemView.findViewById(R.id.dialogAddAplicationRowCard);
        CheckBox checkBox = holder.itemView.findViewById(R.id.dialogAddAplicationCheckBox);

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
        String appName = dialogAddAplicationRowModels.get(position)
                .getAppName();

        holder.appNameTV.setText(appName);
        holder.appIconIV.setImageResource(dialogAddAplicationRowModels.get(position)
                .getAppIcon());

        //Asignar el valor al checkbox como "Checked" si esta en la lista "selectedApps"
        if(selectedApps.contains(appName)){
            holder.appSelectedCB.setChecked(true);
        } else {
          holder.appSelectedCB.setChecked(false);
        }

        //Añadir un listener para identificar cuando se hace click al checkbox
        holder.appSelectedCB.setOnClickListener(v -> {
            if(holder.appSelectedCB.isChecked()){
                listener.onChecked(appName);
            }else{
                listener.onUnchecked(appName);
            }
        });

        cardView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        //Metodo para contar los elementos que hay total dentro del recyclerView
        return dialogAddAplicationRowModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        /*
         * Clase creada para asignar los valores a los elementos del CardView de
         * cada fila
         * */

        TextView appNameTV;
        ImageView appIconIV;
        CheckBox appSelectedCB;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //Elementos del layout de statistics_recycler_view_row.xml
            appNameTV = itemView.findViewById(R.id.dialogAddAplicationName);
            appIconIV = itemView.findViewById(R.id.dialogAddAplicationIcon);
            appSelectedCB = itemView.findViewById(R.id.dialogAddAplicationCheckBox);

        }
    }

    //Métdo para filtrar según el texto del buscador
    public void filter(String text){
        dialogAddAplicationRowModels.clear(); //Borrar las filas

        if(text == null || text.trim().isEmpty()){
            //Si el texto del buscador esta vacio
            dialogAddAplicationRowModels.addAll(fullList); //Añadir la lista completa
        } else {
            //Si el usuario escribio algo
            String lowerCaseText = text.toLowerCase();
            for(DialogAddAplicationRowModel rowModel : fullList){
                if(rowModel.getAppName().toLowerCase().contains(lowerCaseText)){
                    dialogAddAplicationRowModels.add(rowModel);
                }
            }
            //Cambiar el estado del rowmodel si se encuentra en la lista de apps seleccionadas
            for(DialogAddAplicationRowModel rowModel : dialogAddAplicationRowModels){
                rowModel.setAppSelected(selectedApps.contains(rowModel.getAppName()));
            }
        }

        notifyDataSetChanged();
    }

    public void updateSelectedApps(ArrayList<String> updatedList) {
        this.selectedApps = updatedList;
        notifyDataSetChanged();
    }

}
