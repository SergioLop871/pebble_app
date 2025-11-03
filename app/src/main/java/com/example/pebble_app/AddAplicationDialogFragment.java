package com.example.pebble_app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AddAplicationDialogFragment extends DialogFragment {


    //Lista para mostrar las aplicaciones existentes
    private ArrayList<AddAplicationDialogRowModel> addAplicationDialogRowModels = new ArrayList<>();

    //Adaptador para manejar la lista de apps
    private AddAplicationDialogRecyclerViewAdapter adapter;

    private RecyclerView recyclerView;

    private ImageView backBtn, deleteTextBtn;

    private EditText searchET;



    //Fragmeto padre
    private Fragment parent;

    //ArrayList para recibir las aplicaciones dentro del chipgroup
    private ArrayList<String> selectedApps = new ArrayList<>();

    public void setSelectedApps(ArrayList<String> selectedApps){
        this.selectedApps = selectedApps;
    }

    private void setUpAplicationRowModels(){
        PackageManager packageManager = requireContext().getPackageManager();

        // Crear un intent para buscar apps lanzables
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // Obtener todas las actividades que pueden ser lanzadas
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(mainIntent, 0);

        for(ResolveInfo info : resolveInfos){
            Drawable appIcon = info.loadIcon(packageManager);
            String appName = info.loadLabel(packageManager).toString();
            AddAplicationDialogRowModel model =
                    new AddAplicationDialogRowModel(appName, appIcon, false);
            addAplicationDialogRowModels.add(model);
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
        searchET = view.findViewById(R.id.searchEditText);
        deleteTextBtn = view.findViewById(R.id.deleteTextButton);
        deleteTextBtn.setVisibility(View.INVISIBLE);

        //Inicializar la lista de aplicaciones
        setUpAplicationRowModels();

        //Pasar el Fragmento al construir el adaptador
        adapter = new AddAplicationDialogRecyclerViewAdapter(getContext(),
                addAplicationDialogRowModels,
                selectedApps,
                (AddAplicationDialogRecyclerViewAdapter.OnCheckBoxSellected) parent);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        backBtn.setOnClickListener(v -> {
            //Cerrar la ventana de dialogo
            dismiss();
        });

        //Para saber cuando el usuario escribe un caracter
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(searchET.length() > 0){
                    deleteTextBtn.setVisibility(View.VISIBLE);
                } else {
                    deleteTextBtn.setVisibility(View.INVISIBLE);
                }
                String text = searchET.getText().toString();
                adapter.filter(text);
                adapter.updateSelectedApps(selectedApps);
                Log.d("EditText", "Texto escrito: " + text);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        deleteTextBtn.setOnClickListener(v -> {
            searchET.setText("");
        });



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
