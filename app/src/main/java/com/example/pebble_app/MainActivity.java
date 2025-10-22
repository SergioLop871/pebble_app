package com.example.pebble_app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Se crea la variable para el FragmentManager
    private FragmentManager fragmentManager;

    //Se crean los botones para cada icono del navbar
    private ImageButton btnFocus, btnScreentime, btnLeaderboard, btnUserprofile;

    //Para saber en que indice del nav se encuentra y hacer una animación diferente
    private int currentFragmentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obtener acceso al FragmentManager
        fragmentManager = getSupportFragmentManager();

        //Obtener los botones del layout
        btnFocus = findViewById(R.id.nav_focus);
        btnScreentime = findViewById(R.id.nav_screetime);
        btnLeaderboard = findViewById(R.id.nav_leaderboard);
        btnUserprofile = findViewById(R.id.nav_user);

        //Asignar el onClickListener a cada boton
        btnFocus.setOnClickListener(this);
        btnScreentime.setOnClickListener(this);
        btnLeaderboard.setOnClickListener(this);
        btnUserprofile.setOnClickListener(this);

        //Para poner el icono de FocusFragment como seleccionado por default (inicial)
        btnFocus.setSelected(true);
    }

    @Override
    public void onClick(View v){
        int btnId = v.getId(); //Obtener el id del boton que ha sido seleccionado
        int newFragmentIndex = currentFragmentIndex;
        Fragment newFragment = null;

        //Para reiniciar el color de los iconos del navbar
        btnFocus.setSelected(false);
        btnScreentime.setSelected(false);
        btnLeaderboard.setSelected(false);
        btnUserprofile.setSelected(false);

        //Par colorear el icono seleccionado
        v.setSelected(true);

        //Cambiar el fragmento de acuerdo al boton presionado
        if(btnId == R.id.nav_focus){
            newFragment = new FocusFragment();
            newFragmentIndex = 0;
        } else if (btnId == R.id.nav_screetime) {
            newFragment = new ScreenTimeFragment();
            newFragmentIndex = 1;
        } else if (btnId == R.id.nav_leaderboard) {
            newFragment = new LeaderboardFragment();
            newFragmentIndex = 2;
        } else if (btnId == R.id.nav_user) {
            newFragment = new UserFragment();
            newFragmentIndex = 3;
        }
        replaceFragment(newFragment, newFragmentIndex);
    }

    private void replaceFragment(Fragment fragment, int newFragmentIndex){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
                /*Asignar animaciones personalizadas de res/anim
                *
                * Se elige la transición de acuerdo a la posición
                * del fragmento (por medio del indice)
                * */

                /*
                setCustomAnimations(enterAnim, exitAnim, porEnterAnim, popExitAnim)

                - enterAnim: se activa al hacer "replace()" en el fragmento entrante.
                - exitAnim:  se activa al hacer "replace()" en el fragmento saliente.

                - popEnterAnim: se activa al hacer "pop" al fragmento actual para
                  regresar al anterior (volver atrás), afecta al fragmento que reaparece.
                - popEnterAnim: afecta al fragmento que desaparece.

                */

                if(newFragmentIndex > currentFragmentIndex){
                    transaction.setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    );
                }else {
                    transaction.setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right,
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                    );
                }

                //Se controla la creación de la instancia mediante "FragmentFactory"
                transaction.replace(R.id.fragmentContainerView, fragment, null)
                //Optimiza cambios de estado de los fragmentos involucrados en la transaccion
                .setReorderingAllowed(true)
                //Se confirma la transaccion a la pila de actividades
                //Esto permite recuperar el fragmento anterior al presionar el botón 'atras'
                .addToBackStack("name")
                //Ejecuta el cambio visual del fragmento, reemplaza el actual por el nuevo
                .commit();

                // Actualizar el indice del fragmento actual
                currentFragmentIndex = newFragmentIndex;
    }
}