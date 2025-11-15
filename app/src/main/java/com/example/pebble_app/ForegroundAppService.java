package com.example.pebble_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ForegroundAppService extends Service {

    private boolean running = true;

    @Override
    public android.os.IBinder onBind(Intent intent) {
        return null; // No se usa binding, así que se retorna null
    }


    /*
    * Método para iniciar el servicio en segundo plano
    *    Se ejecuta al usar "startService()" o "startForegroundService()"
    *    En este caso se llama desde MainActivity.java
    * */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, createNotification()); //Crear la notificación persistente

        /*Se cambia un Thread por un ScheduledExecutorService
        * para realizar tareas asincronas, y evitar bloqueos
        * */
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        /*Se usa scheduleWithFixedDelay
         * para evitar que se solapen las ejecuciones
         * del ejecutor si tardan más de lo debido
         * */
        executor.scheduleWithFixedDelay( () ->{
           String lastApp = "";

            //Bucle para detectar las apps en tiempo real
            while (running) {
                String currentApp = getForegroundApp(this); //Obtener la aplicación en primer plano
                if (currentApp != null && !currentApp.equals(lastApp)) {
                    Log.i("AppDetect", "App en primer plano: " + currentApp);
                    lastApp = currentApp;

                    //Cambiar logica para bloquear las aplicaciones en temporizador o sesión de enfoque
                    if (currentApp.equals("com.google.android.youtube")) {
                        Intent blockIntent = new Intent(this, BlockedAppActivity.class);
                        blockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Settings.canDrawOverlays(this)) {
                            startActivity(blockIntent);
                        } else {
                            Log.w("AppDetect", "No se puede abrir bloqueo sin permiso de superposición");
                        }
                    }
                }
            }

        }, 0, 1, TimeUnit.SECONDS); //Delay antes de comenzar 0, delay despues de terminar 1

        return START_STICKY; // mantiene el servicio vivo incluso si se cierra la app
    }


    //Metodo para crear la notificación persistente
    private Notification createNotification() {
        String channelId = "foreground_monitor"; //ID de canal para las notificaciones de este servicio

        /*
        * Crear el canal de notificaciones
        *  Recibe:
        * - el ID del canal
        * - el nombre del canal que puede ver el usuario
        * - la importancia de la notificacion (baja -> sin sonido "silenciosa")
        *
        * */
        NotificationChannel channel = new NotificationChannel(
                channelId, "App Monitor", NotificationManager.IMPORTANCE_LOW
        );

        //Obtener el servicio administrador de notificaciones
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel); //Se registra el canal en el administrador

        //Se crea la notificacion de manera grafica para que la vea el usuario en las notificaciones
        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Monitoreando apps")
                .setContentText("Detectando la app en primer plano")
                .setSmallIcon(R.drawable.outline_radar_24)
                .build();
    }


    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();
    }


    /*Metodo que usa UsageEvents, y permite
    * saber que aplicación esta en primer plano, al iniciar una aplicación
    * o volver a una aplicación ya abierta
    * */
    private String getForegroundApp(Context context) {
        long endTime = System.currentTimeMillis(); //Tiempo de fin (tiempo actual)
        long beginTime = endTime - 2000; // Tiempo de inicio (2 segundos atras)
        UsageStatsManager usageStatsManager =
                (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        /*
        * QueryEvents() devuelve el historial los eventos como abrir, cerrar, mover al primer
        * o segundo plano las aplicaciones, por lo que se requiere un rango de tiempo
        * para solo consultar unos cuantos segundos
        *
        * */
        UsageEvents events = usageStatsManager.queryEvents(beginTime, endTime); //Obtener los eventos
        UsageEvents.Event event = new UsageEvents.Event(); //Almacenar el evento de la iteración
        String currentApp = null; //Para almacenar el nombre del paquete de la app en primer plano

        //Iterar entre los eventos obtenidos
        while (events.hasNextEvent()) { //Verificar si hay un siguiente evento

            //Guarda el evento actual en "event" y mueve el puntero al siguiente evento de los obtenidos
            events.getNextEvent(event);

            //Verifica si el tipo de "event" es un movimiento al primer plano
            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                currentApp = event.getPackageName(); //Se obtiene el nombre de la app
            }
        }
        return currentApp;
    }
}