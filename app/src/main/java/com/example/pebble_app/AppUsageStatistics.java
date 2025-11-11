package com.example.pebble_app;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
           de tiempo establecido
        */
        queryUsageStats = queryUsageStats.stream().filter(app -> app.getTotalTimeInForeground() > 0)
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
}