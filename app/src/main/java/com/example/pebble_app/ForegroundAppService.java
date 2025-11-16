package com.example.pebble_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ForegroundAppService extends Service {


    DatabaseHelper myDB;

    Cursor cursor;

    Cursor cursorTemp;
    private ArrayList<Integer> sessionIDs = new ArrayList<>();

    //Apps asginadas a la sesión por el nombre de su package
    private ArrayList<String> sessionIndex0Apps = new ArrayList<>();

    private ArrayList<String> sessionIndex0Packages = new ArrayList<>();

    private String[] daysOfTheWeek = {"L","M","X","J","V","S","D"};
    private ArrayList<String> sessionIndex0Days = new ArrayList<>();

    private HashMap<String, String> installedApps = new HashMap<>();

    private String startIndex0;

    private String nameIndex0;
    private String endIndex0;

    private int startHour;
    private int startMinute;
    private int startAmPm;

    private int startTotalMinutes;

    private int endHour;
    private int endMinute;
    private int endAmPm;

    private int endTotalMinutes;

    private int sessionIdIdex0;


    Boolean activeSession = false;

    @Override
    public android.os.IBinder onBind(Intent intent) {
        return null; // No se usa binding, así que se retorna null
    }

    void setUpInstalledApps(){
        PackageManager packageManager = this.getPackageManager();

        // Crear un intent para buscar apps lanzables
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // Obtener todas las actividades que pueden ser lanzadas
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(mainIntent, 0);

        for(ResolveInfo info : resolveInfos){
            String appName = info.loadLabel(packageManager).toString();
            String packageName = info.activityInfo.packageName;
            installedApps.put(appName, packageName);
            Log.d("LAUNCHABLE_APPS", "App: " + appName + " | Package: " + packageName);
        }
    }

    void setStartAndEndTime(){
        String[] startParts = startIndex0.split(" "); // ["10:30", "PM"]

        if(startParts[0].contains(":")){
            String[] startHM = startParts[0].split(":");  // ["10", "30"]
            startHour = Integer.parseInt(startHM[0]); // 10
            startMinute = Integer.parseInt(startHM[1]); // 30
        } else{
            startHour = Integer.parseInt(startParts[0]);
            startMinute = 0;
        }
        startAmPm = startParts[1].equalsIgnoreCase("AM") ? 0 : 1;
        startHour += (startAmPm == 1) ? 12 : 0;

        startTotalMinutes = (startHour * 60) + startMinute;

        String[] endParts = endIndex0.split(" ");

        if(endParts[0].contains(":")){
            String[] endHM = endParts[0].split(":");  // ["10", "30"]
            endHour = Integer.parseInt(endHM[0]); // 10
            endMinute = Integer.parseInt(endHM[1]); // 30
        } else{
            endHour = Integer.parseInt(endParts[0]);
            endMinute = 0;
        }
        endAmPm = endParts[1].equalsIgnoreCase("AM") ? 0 : 1;
        endHour += (endAmPm == 1) ? 12 : 0;


        endTotalMinutes = (endHour * 60) + endMinute;
    }

    /*
    * Método para iniciar el servicio en segundo plano
    *    Se ejecuta al usar "startService()" o "startForegroundService()"
    *    En este caso se llama desde MainActivity.java
    * */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, createNotification()); //Crear la notificación persistente
        Log.d("ForegroundAppService", "foreground service started");


        myDB = new DatabaseHelper(this);
        /*Se cambia un Thread por un ScheduledExecutorService
        * para realizar tareas asincronas, y evitar bloqueos
        * */
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        //Datos de las sessiones de enfoque leer una primera vez
        cursor = myDB.readAllFocusData();

        setUpInstalledApps();

        //Obtener la primera sesión para pruebas
        int currentSessionID = 0;
        while (cursor.moveToNext()){
            if(currentSessionID == 0){
                sessionIdIdex0 = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                nameIndex0 = cursor.getString(1);
                startIndex0 = cursor.getString(4);
                endIndex0 = cursor.getString(5);
                Log.d("ForegroundAppService", "idIndex0: " + sessionIdIdex0);
                Log.d("ForegroundAppService", "starIndex0: " + startIndex0);
                Log.d("ForegroundAppService", "endIndex0: " + endIndex0);
                setStartAndEndTime();
                currentSessionID++;
            }
        }
        cursor.close();

        Log.d("ForegroundAppService", "startHour0 to 24h: " + startHour);
        Log.d("ForegroundAppService", "endHour0 to 24h: " + endHour);

        //Obtener las apps de la sesión
        String currentAppName;
        cursor = myDB.ReadSessionApps(sessionIdIdex0);
        while (cursor.moveToNext()){
            currentAppName = cursor.getString(0);
            sessionIndex0Apps.add(currentAppName);
        }
        cursor.close();

        //Obtener los paquetes de las apps de la sesión
        for(String appName : sessionIndex0Apps){
            sessionIndex0Packages.add(installedApps.get(appName));
        }

        //Obtener los dias de la sesion
        cursor = myDB.ReadSessionDays(sessionIdIdex0);
        while (cursor.moveToNext()){
            sessionIndex0Days.add(cursor.getString(0));
        }
        cursor.close();

        //Obtener el dia actual
        LocalDate today = LocalDate.now();
        int currentDayValue = today.getDayOfWeek().getValue() - 1;
        String currentDay = daysOfTheWeek[currentDayValue];
        Log.d("ForegroundAppService", "Current Day: " + currentDay);

        /*Se usa scheduleWithFixedDelay
         * para evitar que se solapen las ejecuciones
         * del ejecutor si tardan más de lo debido
         * */
        executor.scheduleWithFixedDelay( () ->{
            LocalTime now = LocalTime.now();

            int hourNow = now.getHour();
            int minuteNow = now.getMinute();
            int nowTotalMinutes = (hourNow * 60) + minuteNow;

            //Log.d("ForegroundAppService", "LocalTime:" + now);
            //Log.d("ForegroundAppService", "Time H/M/S: " + hourNow + ":" + minuteNow + ":" + secondsNow);

            //Actualizar la sessión si se edita
            int idCount = 0;
            cursorTemp = myDB.readAllFocusData();
            while (cursorTemp.moveToNext()){
                if(idCount == 0){
                    sessionIdIdex0 = cursorTemp.getInt(cursorTemp.getColumnIndexOrThrow("id"));
                    nameIndex0 = cursorTemp.getString(1);
                    startIndex0 = cursorTemp.getString(4);
                    endIndex0 = cursorTemp.getString(5);
                    setStartAndEndTime();
                    idCount++;
                }
            }
            cursorTemp.close();


            //Obtener las apps de la sesión
            String tempCurrentAppName;
            cursorTemp = myDB.ReadSessionApps(sessionIdIdex0);
            while (cursorTemp.moveToNext()){
                tempCurrentAppName = cursorTemp.getString(0);
                sessionIndex0Apps.add(tempCurrentAppName);
            }
            cursorTemp.close();

            //Obtener los paquetes de las apps de la sesión
            sessionIndex0Days.clear();
            for(String appName : sessionIndex0Apps){
                sessionIndex0Packages.add(installedApps.get(appName));
            }

            //Obtener los paquetes de las apps de la sesión
            for(String appName : sessionIndex0Apps){
                sessionIndex0Packages.add(installedApps.get(appName));
            }

            //Obtener los dias de la sesion
            sessionIndex0Days.clear();
            cursorTemp = myDB.ReadSessionDays(sessionIdIdex0);
            while (cursorTemp.moveToNext()){
                sessionIndex0Days.add(cursorTemp.getString(0));
            }
            cursorTemp.close();


            String currentApp = getForegroundApp(this); //Obtener la aplicación en primer plano

            if (currentApp != null) {
                Log.i("AppDetect", "App en primer plano: " + currentApp);

                //Cambiar logica para bloquear las aplicaciones en temporizador o sesión de enfoque

                activeSession = (sessionIndex0Days.contains(currentDay)) && (nowTotalMinutes >= startTotalMinutes && nowTotalMinutes < endTotalMinutes);
                //Si la sesión esta activa
                if(activeSession){
                    Log.i("ForegroundAppService", "Sesión ACTIVA!!");
                    Log.i("ForegroundAppService", "SessionDays: " + sessionIndex0Days.toString());

                    Log.i("ForegroundAppService", "Nombre: " + nameIndex0);
                    //Detectar las applicaciones a bloquear
                    if(sessionIndex0Packages.contains(currentApp)){
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