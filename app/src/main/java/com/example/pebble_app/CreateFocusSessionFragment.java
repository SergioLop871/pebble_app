package com.example.pebble_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class CreateFocusSessionFragment extends Fragment
        implements DialogAddAplicationRecyclerViewAdapter.OnCheckBoxSellected {

    //Botón para volver a FocusFragment
    ImageButton backBtn;

    //LayoutInflater para colocar una chip en el chipgroup de apps
    LayoutInflater inflater;

    //Para obtener el chipGroup del layout
    ChipGroup chipGroup;

    //Botones del layout
    Button addAplicationBtn, createSessionBtn;

    //ArrayList para pasar las aplicaciones dentro del chipGroup a AddAplicationDialogFragment
    ArrayList<String> selectedApps = new ArrayList<>();

    public CreateFocusSessionFragment() {
        // Required empty public constructor
    }

    //Metodo para crear un chip en el chipGroup
    @Override
    public void onChecked(String applicationName){
        //Crear una chip
        inflater = LayoutInflater.from(requireContext());
        if(inflater != null || chipGroup == null || !isAdded()){
            Chip chip = (Chip) inflater.inflate(R.layout.item_chip, chipGroup,false);
            chip.setText(applicationName); //Asginar el nombre de la app
            chip.setTag(applicationName);
            chip.setOnCloseIconClickListener(v -> chipGroup.removeView(chip));
            chipGroup.addView(chip);
        }
    }

    //Metodo para eliminar un chip del chipGroup
    @Override
    public void onUnchecked(String applicationName){
        for(int i = 0; i < chipGroup.getChildCount(); i++){
            View child = chipGroup.getChildAt(i);
            if(child instanceof Chip){
                Chip chip = (Chip) child;
                if(applicationName.equals(chip.getTag())){
                    chipGroup.removeView(chip);
                    break;
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_focus_session, container, false);

        backBtn = view.findViewById(R.id.goBackButton);
        addAplicationBtn = view.findViewById(R.id.addAppsButton);
        createSessionBtn = view.findViewById(R.id.createModeButton);

        //Boton de volver
        backBtn.setOnClickListener(v -> {
            //Regresar al fragmento anterior
            getParentFragmentManager().popBackStack();
        });

        //Boton para crear el dialogFragment de añadir apps
        addAplicationBtn.setOnClickListener(v -> {
            AddAplicationDialogFragment dialog = new AddAplicationDialogFragment();

            selectedApps.clear();

            for(int i = 0; i < chipGroup.getChildCount(); i++){
                View child = chipGroup.getChildAt(i);
                if(child instanceof Chip){
                    Chip chip = (Chip) child;
                    selectedApps.add(chip.getText().toString());
                }
            }

            //Pasar los nombres de apps al dialogo
            dialog.setSelectedApps(selectedApps);

            dialog.show(getChildFragmentManager(), "addAplicationDialog");
        });

        chipGroup = view.findViewById(R.id.appChipGroup);

        return view;
    }
}