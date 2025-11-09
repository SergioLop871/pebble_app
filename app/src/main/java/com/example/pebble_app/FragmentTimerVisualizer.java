package com.example.pebble_app;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class FragmentTimerVisualizer extends Fragment
        implements ConfirmDeleteDialogFragment.OnDeleteListener{

    //Botones para el funcionamiento del fragmento
    private ImageButton goBackBtn;
    //private CardView startTimerBtn;

    private ImageButton startTimerBtn;
    private Button deleteModeBtn, editModeBtn;

    //TextView para mostrar la información del Temporizador
    private TextView emoticonTV, nameTV, descriptionTV, timerHourTV;

    //Variables para almacenar los datos y pasarlos a la seccion de editar
    private String emoticon, name, description;

    private int startHour, startMinute;

    private ArrayList<String> selectedApps; //ArrayList para guardar las apps

    private FragmentTimerEdit fragmentTimerEdit;

    public FragmentTimerVisualizer() {
        // Required empty public constructor
    }

    //Metodo para inicializar la información al seleccionar en el recyclerview
    public void initTimerInfo(String emoticon, String name, String description,
                                int startHour, int startMinute, ArrayList<String> selectedApps){

        //Inicializar los datos del elemento seleccionado
        this.emoticon = emoticon;
        this.name = name;
        this.description = description;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.selectedApps = new ArrayList<>(selectedApps);

    }

    //Incializar la información para mostar al usuario
    public void initShowInfo(){

        //Inicializacion temporal para pruebas
        emoticon = "\uD83D\uDCDA";
        name = "Study Mode";
        description = "Inicia un temporizador que bloquea las aplicaciones, al finalizar, se terminará el bloqueo";
        startHour = 1;
        startMinute = 0;
        selectedApps = new ArrayList<>(Arrays.asList("Settings", "Chrome", "YT Music"));

        //Varaibles para iniciar el formato de timerHoursTV
        String format = "%02d";
        String startHourFormat, startMinuteFormat;
        String timerHoursString;
        startHourFormat = String.format(format, startHour);
        startMinuteFormat = String.format(format, startMinute);

        //Añadir el formato de los minutos si estos son mayores a 0
        timerHoursString = startHourFormat + " : " + startMinuteFormat;

        //Asginar los valores para mostar al usuario
        emoticonTV.setText(emoticon);
        nameTV.setText(name);
        descriptionTV.setText(description);
        timerHourTV.setText(timerHoursString);

    }

    public void updateInfo(String emoticon, String name, String description,
                           int startHour, int startMinute, ArrayList<String> selectedApps){

        this.emoticon = emoticon;
        this.name = name;
        this.description = description;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.selectedApps = new ArrayList<>(selectedApps);

        //Varaibles para iniciar el formato de timeHourTV
        String format = "%02d";
        String startHourFormat, startMinuteFormat;
        String timerHourString;
        startHourFormat = String.format(format, this.startHour);
        startMinuteFormat = String.format(format, this.startMinute);

        //Mostar el tiempo elegido en el TextView
        timerHourString = startHourFormat + " : " + startMinuteFormat;
        timerHourTV.setText(timerHourString);

        //Asginar los valores para mostar al usuario
        emoticonTV.setText(emoticon);
        nameTV.setText(name);
        descriptionTV.setText(description);
        timerHourTV.setText(timerHourString);


    }


    //Colocar la logica de eliminar la sesión
    @Override
    public void onDeleteClick(){
        getParentFragmentManager().popBackStack();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer_visualizer, container, false);

        //Obtener los elementos de la vista
        goBackBtn = view.findViewById(R.id.goBackButton);
        emoticonTV = view.findViewById(R.id.emoticonTextView);
        nameTV = view.findViewById(R.id.nameTextView);
        descriptionTV = view.findViewById(R.id.descriptionTextView);
        timerHourTV = view.findViewById(R.id.timerHoursTextView);

        startTimerBtn = view.findViewById(R.id.startTimerButton); //Boton para iniciar la cuenta
        deleteModeBtn = view.findViewById(R.id.deleteModeButton);
        editModeBtn = view.findViewById(R.id.editModeButton); //Boton para editar

        //Listener para iniciar el temporizador
        startTimerBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Comenzar temporizador", Toast.LENGTH_SHORT).show();
        });

        goBackBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        deleteModeBtn.setOnClickListener(v -> {
            String title = "Eliminar Temporizador";
            String description = "¿Seguro que deseas eliminar el temporizador "
                    + "\"" + name + "\"" + " ?";
            ConfirmDeleteDialogFragment confirmDeleteF = ConfirmDeleteDialogFragment
                    .newInstance(title, description, "timer");
            confirmDeleteF.show(getChildFragmentManager(), "confirmDeleteDialog");
        });

        editModeBtn.setOnClickListener(v -> {
            fragmentTimerEdit = new FragmentTimerEdit();
            fragmentTimerEdit.initSessionInfo(name, description, emoticon, startHour, startMinute, selectedApps);
            getParentFragmentManager().setFragmentResultListener(
                    "editTimerResult", this,
                    (requestKey, result) -> {
                        String newEmoticon = result.getString("newEmoticon");
                        String newName = result.getString("newName");
                        String newDescription = result.getString("newDescription");
                        int newStartHour = result.getInt("newStartHour");
                        int newStartMinute = result.getInt("newStartMinute");
                        ArrayList<String> newSelectedApps = result.
                                getStringArrayList("newSelectedApps");
                        updateInfo(newEmoticon, newName, newDescription,
                                newStartHour, newStartMinute, newSelectedApps);
                    }
            );
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            ft.replace(R.id.fragmentContainerView, fragmentTimerEdit)
                    .addToBackStack(null)
                    .commit();
        });

        initShowInfo();

        return view;
    }
}