package com.example.pebble_app;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/* MODE_ALLOWED es una constante entera que indica que una operación (acceso al uso de datos)
   ha sido autorizada por el sistema.

   Es uno de los posibles valores que puede devolver el metodo:
   int mode = appOpsManager.checkOpNoThrow();
*/
import static android.app.AppOpsManager.MODE_ALLOWED;
/* OPSTR_GET_USAGE_STATS es una cadena de texto (String) constante que representa el nombre
   de una operación de permiso del sistema. Identifica la operación de “acceso a estadísticas
   de uso” (Usage Access).

    Se usa con el metodo checkOpNoThrow() para revisar si la app tiene el permiso habilitado.
*/
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

public class AppUsageStatistics {

    // Definir una etiqueta (tag) para identificar mensajes en el Logcat relacionados a la clase
    private static final String TAG = AppUsageStatistics.class.getSimpleName();

    // Provee acceso al historial de uso y estadísticas del dispositivo
    private UsageStatsManager usageStatsManager;

    // Provee el contexto
    private final Context context;

    // Constructor
    public AppUsageStatistics(Context context) {
        this.context = context;
        // Instanciar un objeto de la clase "UsageStatsManager"
        usageStatsManager = (UsageStatsManager) this.context.getSystemService(Context.USAGE_STATS_SERVICE);
    }

    /* Verificar si se tiene el permiso para acceder a las estadísticas de uso:
       - Revisar si el permiso "PACKAGE_USAGE_STATS" está permitido para la app
       @return true si el permiso está otorgado
    */
    public boolean getUsageAccessPermissionStatus() {
        AppOpsManager appOpsMng = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

        // Comprobar el estado de la operación del sistema (permiso) para la app
        int mode = appOpsMng.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(),
                context.getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            return (context.checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS)
                    == PackageManager.PERMISSION_GRANTED);
        }
        else {
            return (mode == MODE_ALLOWED);
        }
    }

    /*
      Metodo para obtener estadísticas de tiempo de uso de las apps

      Parámetros:
      - intervalType: El intervalo de tiempo del que se extraen las estadísticas
                      que corresponde al valor de las constantes de UsageStatsManager.
                      E.g., INTERVAL_DAILY, INTERVAL_WEEKLY, INTERVAL_MONTHLY,
                      INTERVAL_YEARLY.

      Retorno: Una lista de UsageStats según el lapso de tiempo especificado en el argumento
               intervalType.
    */
    public List<UsageStats> getUsageStatistics(int intervalType) {
        /*
          Instanciar un objeto de la clase "Calendar" mediante getInstance()

          Retorno: Calendario del tipo apropiado para la configuración regional, donde sus
                   campos de tiempo han sido inicializados con la fecha y hora actuales.
        */
        Calendar cal = Calendar.getInstance();
        // Cambiar la fecha al inicio del día actual (medianoche)
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long startOfDay = cal.getTimeInMillis();
        long now = System.currentTimeMillis();

        /*
          Obtener y guardar las estadísticas de tiempo de uso. Las estadísticas se guardan en una
          lista de objetos "UsageStats".

          Metodo queryUsageStats

          Obtiene las estadísticas de uso de las apps para el rango de tiempo dado,
          agrupadas por el intervalo especificado.

          public List<UsageStats> queryUsageStats (int intervalType, long beginTime, long endTime)

          Parámetros:
          - intervalType (int): El intervalo de tiempo por el cual las estadísticas son agrupadas.
          - beginTime (long): El inicio inclusivo del rango de estadísticas a incluir en los
                               resultados. Definido en "tiempo Unix".
          - endTime (long): El final exclusivo del rango de estadísticas a incluir en los
                            resultados. Definido en "tiempo Unix".

          Retorno: List<UsageStats>.
       */
        List<UsageStats> queryUsageStats = usageStatsManager
                .queryUsageStats(intervalType, startOfDay, now);

        /*
           Filtrar las aplicaciones para sólo mantener las que fueron usadas durante el intervalo
           de tiempo establecido y que tengan launcher
        */
        PackageManager packageManager = context.getPackageManager();
        
        queryUsageStats = queryUsageStats.stream()
                .filter(app -> app.getTotalTimeInForeground() > 0)
                .filter(app -> hasLauncher(packageManager, app.getPackageName()))
                .collect(Collectors.toList());

        // Agrupar cada objeto UsageStats por app y ordenarlas por tiempo total en primer plano
        if (queryUsageStats.size() > 0) {
            Map<String, UsageStats> sortedMap = new TreeMap<>();
            for (UsageStats usageStats: queryUsageStats) {
                sortedMap.put(usageStats.getPackageName(), usageStats);
            }

            List<UsageStats> usageStatsList = new ArrayList<>(sortedMap.values());

            // Ordenar las apps por tiempo en primer plano (mayor a menor)
            Collections.sort(usageStatsList, (s1, s2) -> Long.compare(s2.getTotalTimeInForeground(), s1.getTotalTimeInForeground()));
            return usageStatsList;
        }
        List<UsageStats> emptyList = new ArrayList<>();
        return emptyList;
    }

    /*
      Método para verificar si una aplicación tiene launcher (puede ser lanzada por el usuario)

      Parámetros:
      - packageManager: El PackageManager del contexto
      - packageName: El nombre del paquete de la aplicación a verificar

      Retorno: true si la aplicación tiene launcher, false en caso contrario
    */
    private boolean hasLauncher(PackageManager packageManager, String packageName) {
        try {
            // Intent para verificar si la app tiene un launcher
            Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
            return launchIntent != null;
        } catch (Exception e) {
            // Si hay algún error al verificar, asumir que no tiene launcher
            return false;
        }
    }

    /*
     * Método para obtener la cantidad de pick-ups (desbloqueos) del dispositivo
     * Detecta específicamente cuando el teléfono se desbloquea contando cuando el launcher
     * aparece después de un período significativo de inactividad (dispositivo bloqueado)
     * 
     * Retorno: El número de desbloqueos del día actual
     */
    public int getPickUpsCount() {
        Calendar cal = Calendar.getInstance();
        // Cambiar la fecha al inicio del día actual (medianoche)
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long startOfDay = cal.getTimeInMillis();
        long now = System.currentTimeMillis();

        // Obtener el nombre del paquete del launcher del dispositivo
        String launcherPackage = getLauncherPackageName();
        
        // Obtener los eventos de uso del día
        UsageEvents events = usageStatsManager.queryEvents(startOfDay, now);
        UsageEvents.Event event = new UsageEvents.Event();
        
        int pickUpsCount = 0;
        long lastEventTime = 0;
        long minInactivityForUnlock = 60000; // Mínimo 60 segundos de inactividad para considerar un desbloqueo
        boolean firstEventOfDay = true;

        // Procesar los eventos directamente para detectar desbloqueos
        while (events.hasNextEvent()) {
            events.getNextEvent(event);
            
            int eventType = event.getEventType();
            String packageName = event.getPackageName();
            long eventTime = event.getTimeStamp();
            
            // Detectar desbloqueos: cuando el launcher se mueve al primer plano
            // DESPUÉS de un período significativo de inactividad
            if (eventType == UsageEvents.Event.MOVE_TO_FOREGROUND && 
                packageName != null && 
                packageName.equals(launcherPackage)) {
                
                if (firstEventOfDay) {
                    // El primer evento del día (primer desbloqueo de la mañana)
                    pickUpsCount = 1;
                    firstEventOfDay = false;
                    lastEventTime = eventTime;
                } else {
                    // Verificar si ha pasado suficiente tiempo desde el último evento
                    // Esto indica que el dispositivo estuvo bloqueado
                    long timeSinceLastEvent = eventTime - lastEventTime;
                    
                    if (timeSinceLastEvent > minInactivityForUnlock) {
                        // Ha pasado suficiente tiempo, probablemente fue un desbloqueo
                        pickUpsCount++;
                        lastEventTime = eventTime;
                    } else {
                        // Actualizar el tiempo del último evento
                        lastEventTime = eventTime;
                    }
                }
            } else if (eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                // Actualizar el tiempo del último evento para cualquier app
                lastEventTime = eventTime;
            }
        }
        
        return Math.max(pickUpsCount, 0); // Asegurar que no sea negativo
    }
    
    /*
     * Método auxiliar para obtener el nombre del paquete del launcher del dispositivo
     * 
     * Retorno: El nombre del paquete del launcher
     */
    private String getLauncherPackageName() {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        
        // Obtener el launcher por defecto
        android.content.pm.ResolveInfo resolveInfo = packageManager.resolveActivity(intent, 
            PackageManager.MATCH_DEFAULT_ONLY);
        
        if (resolveInfo != null && resolveInfo.activityInfo != null) {
            return resolveInfo.activityInfo.packageName;
        }
        
        // Si no se encuentra, usar valores por defecto comunes
        return "com.android.launcher"; // Valor por defecto común
    }
}