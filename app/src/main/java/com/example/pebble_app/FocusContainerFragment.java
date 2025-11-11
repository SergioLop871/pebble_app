package com.example.pebble_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class FocusContainerFragment extends Fragment {

    private FocusFragment focusFragment;

    public FocusContainerFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_focus_container, container, false);

        if(focusFragment == null){
            focusFragment = new FocusFragment();
        }

        //Evitar agregar otro fragmento en cambio de config. (ej. rotar la pantalla)
        if (savedInstanceState == null){
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, focusFragment, "focus")
                    .commit();
        }

        return view;
    }


    //Metodo para cambiar de fragmento a CreateFocusSessionFragment
    public void openCreateSessionFragment(){
        if (isAdded() && getView() != null) {  // Verifica que la vista exista
            //Obtener el FragmentManager propio de FocusContainerFragment
            FragmentManager fm = getChildFragmentManager();
            View container = getView().findViewById(R.id.fragmentContainerView);

            if (container != null) {  // Verifica que el contenedor exista
                FragmentTransaction ft = fm.beginTransaction();

                ft.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                );

                ft.replace(R.id.fragmentContainerView, new CreateFocusSessionFragment())
                        .addToBackStack(null)
                        .commit();
            } else {
                Log.e("FocusContainer", "No se encontró el contenedor del fragmento");
            }
        }
    }

    //Metodo para cambiar de fragmento a CreateFocusSessionFragment
    public void openCreateTimerFragment(){
        if (isAdded() && getView() != null) {  // Verifica que la vista exista
            //Obtener el FragmentManager propio de FocusContainerFragment
            FragmentManager fm = getChildFragmentManager();
            View container = getView().findViewById(R.id.fragmentContainerView);

            if (container != null) {  // Verifica que el contenedor exista
                FragmentTransaction ft = fm.beginTransaction();

                ft.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                );

                ft.replace(R.id.fragmentContainerView, new CreateTimerFragment())
                        .addToBackStack(null)
                        .commit();
            } else {
                Log.e("FocusContainer", "No se encontró el contenedor del fragmento");
            }
        }
    }

    public void openSessionFragment(int sessionId){
        FragmentManager fm = getChildFragmentManager();
        View container = getView().findViewById(R.id.fragmentContainerView);
        if (container != null) {  // Verifica que el contenedor exista
            FragmentFocusSessionVisualizer fragmentFocusSessionVisualizerFragment = new FragmentFocusSessionVisualizer();

            // Pasar el ID de la sesión al fragmento visualizador
            Bundle args = new Bundle();
            args.putInt("sessionId", sessionId);
            fragmentFocusSessionVisualizerFragment.setArguments(args);

            FragmentTransaction ft = fm.beginTransaction();

            ft.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );

            ft.replace(R.id.fragmentContainerView, fragmentFocusSessionVisualizerFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            Log.e("FocusContainer", "No se encontró el contenedor del fragmento");
        }
    }

    public void openTimerFragment(){
        FragmentManager fm = getChildFragmentManager();
        View container = getView().findViewById(R.id.fragmentContainerView);
        if (container != null) {  // Verifica que el contenedor exista
            FragmentTransaction ft = fm.beginTransaction();

            ft.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );

            ft.replace(R.id.fragmentContainerView, new FragmentTimerVisualizer())
                    .addToBackStack(null)
                    .commit();
        } else {
            Log.e("FocusContainer", "No se encontró el contenedor del fragmento");
        }
    }

}