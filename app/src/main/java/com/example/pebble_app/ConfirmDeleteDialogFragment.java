package com.example.pebble_app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class ConfirmDeleteDialogFragment extends DialogFragment {

    Button cancelBtn, deleteBtn;

    TextView titleTV, descriptionTV;
    /*Se crea una interfaz para poder comunicarse con
     * FocusFragmentContainer a traves de FocusFragment
     * y poder realizar la transacción hacia
     * CreateFocusSessionFragment y
     * CreateFocusTimerFragment (Aun no implementado)
     *  */
    public interface OnDeleteListener{
        void onDeleteClick();
    }

    /*
     * Este atributo/variable sirve para guardar una referencia
     * a la instancia FocusFragment, cuando se crea en
     * tiempo de ejecución
     * */
    private ConfirmDeleteDialogFragment.OnDeleteListener listener;


    //Metodo para cambiar el titulo, mensaje y color de acuerdo a quien llama a este dialogo
    public void setDeleteDialogFragmentInfo(String title, String description, String mode){
        if(mode.equals("session")){
            cancelBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pibbleLogoWater));
        } else if(mode.equals("timer")){
            cancelBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pibbleLogoSand));
        }

        titleTV.setText(title);
        descriptionTV.setText(description);
    }

    public static ConfirmDeleteDialogFragment newInstance(String title, String description, String mode) {
        ConfirmDeleteDialogFragment fragment = new ConfirmDeleteDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("description", description);
        args.putString("mode", mode);
        fragment.setArguments(args);
        return fragment;
    }

    /*Sobrescribir el metodo onAttach
     * de la clase Fragment para obtener el contexto,
     * el contexo sera el Fragmento que contiene al
     * DialogFragment, que es "FragmentFocusSessionVisualizer" o "FragmentTimerVisualizer"
     * */
    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        Fragment parent = getParentFragment(); //Obtener la instancia de FragmentTimerVisualizer creada

        //Verificar si la instancia implementa la interfaz
        if(parent instanceof ConfirmDeleteDialogFragment.OnDeleteListener){
            //Se castea la instancia para indicar al compilador que esta usa la interfaz
            listener = (ConfirmDeleteDialogFragment.OnDeleteListener) parent;
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_confirm_delete_mode, container, false);

        //Obtener los elementos a usar
        cancelBtn = view.findViewById(R.id.cancelButton);
        deleteBtn = view.findViewById(R.id.setTimerHourButton);
        titleTV = view.findViewById(R.id.confirmDeleteTitleTextView);
        descriptionTV = view.findViewById(R.id.confirmDeleteDescriptionTextView);

        // Obtener argumentos
        if (getArguments() != null) {
            String title = getArguments().getString("title");
            String description = getArguments().getString("description");
            String mode = getArguments().getString("mode");

            titleTV.setText(title);
            descriptionTV.setText(description);

            if (mode.equals("session")) {
                cancelBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pibbleLogoWater));
            } else if (mode.equals("timer")) {
                cancelBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pibbleLogoSand));
            }
        }

        //Listeners para los botones a usar
        cancelBtn.setOnClickListener(v -> {
            dismiss();
        });

        deleteBtn.setOnClickListener(v -> {
            if(listener != null){
                listener.onDeleteClick();
            } else{
                Toast.makeText(getContext(), "Null listener",
                        Toast.LENGTH_SHORT).show();
            }

            dismiss();
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

