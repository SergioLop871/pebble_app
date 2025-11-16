package com.example.pebble_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BlockedAppActivity extends AppCompatActivity {

    ImageButton goBackBtn;

    TextView blockedAppTV, timeRangeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_app);

        Intent infoIntent = getIntent();

        blockedAppTV = findViewById(R.id.blockedAppNameTextView);
        timeRangeTV = findViewById(R.id.timeRangeTextView);

        if(infoIntent != null){
            //Asignar el nombre de la app a la pantalla de bloqueo
            String blockedAppText = infoIntent.getStringExtra("appName") + " se encuentra "
                    + "bloqueado por Pebble";
            blockedAppTV.setText(blockedAppText);

            //Asignar el de bloqueo de las apps a la pantalal de bloqueo
            String timeRangeText = infoIntent.getStringExtra("startTime") + " - "
                    + infoIntent.getStringExtra("endTime");
            timeRangeTV.setText(timeRangeText);
        }

        goBackBtn = findViewById(R.id.goBackButton);
        goBackBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }
}