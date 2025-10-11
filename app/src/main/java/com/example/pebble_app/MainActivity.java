package com.example.pebble_app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Se crea la variable para el FragmentManager
    private FragmentManager fragmentManager;

    //Se crean los botones para cada icono del navbar
    private ImageButton btnFocus, btnScreentime, btnLeaderboard, btnUserprofile;

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

        //Para reiniciar el color de los iconos del navbar
        btnFocus.setSelected(false);
        btnScreentime.setSelected(false);
        btnLeaderboard.setSelected(false);
        btnUserprofile.setSelected(false);

        //Par colorear el icono seleccionado
        v.setSelected(true);

        //Cambiar el fragmento de acuerdo al boton presionado
        if(btnId == R.id.nav_focus){
            replaceFragment(new FocusFragment());

        } else if (btnId == R.id.nav_screetime) {
            replaceFragment(new ScreentimeFragment());

        } else if (btnId == R.id.nav_leaderboard) {
            replaceFragment(new LeaderboardFragment());

        } else if (btnId == R.id.nav_user) {
            replaceFragment(new UserprofileFragment());

        }
    }

    private void replaceFragment(Fragment fragment){
        fragmentManager.beginTransaction() //Empieza una nueva transaccion
                //Se controla la creación de la instancia mediante "FragmentFactory"
                .replace(R.id.fragmentContainerView, fragment, null)
                //Optimiza cambios de estado de los fragmentos involucrados en la transaccion
                .setReorderingAllowed(true)
                //Se confirma la transaccion a la pila de actividades
                //Esto permite recuperar el fragmento anterior al presionar el botón 'atras'
                .addToBackStack("name")
                //Ejecuta el cambio visual del fragmento, reemplaza el actual por el nuevo
                .commit();
    }
}