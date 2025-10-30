package com.example.pebble_app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AddAplicationDialogFragment extends DialogFragment {


    //Lista para mostrar las aplicaciones existentes
    private ArrayList<DialogAddAplicationRowModel> dialogAddAplicationRowModels = new ArrayList<>();

    //Adaptador para manejar la lista de apps
    private DialogAddAplicationRecyclerViewAdapter adapter;

    private RecyclerView recyclerView;

    private ImageView backBtn;

    //Fragmeto padre
    private Fragment parent;

    //ArrayList para recibir las aplicaciones dentro del chipgroup
    private ArrayList<String> selectedApps = new ArrayList<>();

    public void setSelectedApps(ArrayList<String> selectedApps){
        this.selectedApps = selectedApps;
    }

    private void setUpAplicationRowModels(){
        for(int i = 0; i < 4; i++){
            int defaultIcon = R.drawable.outline_question_mark_24;
            String appName = "App " + (i + 1);
            DialogAddAplicationRowModel model =
                    new DialogAddAplicationRowModel(appName, defaultIcon, false);
            dialogAddAplicationRowModels.add(model);
        }
    }

    //Metodo para obtener al fragmento padre
    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        //Obtener la instancia de AddAplicationDialogFragment creada
        parent = getParentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_add_aplication_list, container, false);

        recyclerView = view.findViewById(R.id.addAppRecyclerView);

        backBtn = view.findViewById(R.id.backButton);

        backBtn.setOnClickListener(v -> {
            //Cerrar la ventana de dialogo
            dismiss();
        });

        setUpAplicationRowModels();

        //Pasar el Fragmento al construir el adaptador
        adapter = new DialogAddAplicationRecyclerViewAdapter(getContext(),
                dialogAddAplicationRowModels,
                selectedApps,
                (DialogAddAplicationRecyclerViewAdapter.OnCheckBoxSellected) parent);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
