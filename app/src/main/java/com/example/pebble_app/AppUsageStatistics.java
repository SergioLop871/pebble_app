package com.example.pebble_app;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.List;

public class AppUsageStatistics {

    // Definir una etiqueta (tag) para identificar mensajes en el Logcat relacionados a la clase
    private static final String TAG = AppUsageStatistics.class.getSimpleName();

    // Provee acceso al historial de uso y estadísticas del dispositivo
    private UsageStatsManager usageStatsManager;

    // Provee el contexto
    private final Context context;

    // Provee el fragmento
    private final Fragment fragment;

    // Constructor
    public AppUsageStatistics(Context context, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
        // Instanciar un objeto de la clase "UsageStatsManager"
        usageStatsManager = (UsageStatsManager) this.context.getSystemService(Context.USAGE_STATS_SERVICE);
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
        // Cambiar el campo de año de la fecha a un año atrás
        cal.add(Calendar.YEAR, -1);

        /*
          Obtener y guardar las estadísticas de tiempo de uso de un año atrás desde el
          tiempo actual. Las estadísticas se guardan en una lista de objetos "UsageStats".

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
                .queryUsageStats(intervalType, cal.getTimeInMillis(),
                        System.currentTimeMillis());

        // Verificar si se tiene el permiso para acceder a las estadísticas de uso
        if (queryUsageStats.size() == 0) {
            Log.i(TAG, "The user may not allow the access to apps usage");
            // Direccionar al permiso de acceso de uso
            UsageAccessDialogFragment dialog = new UsageAccessDialogFragment();
            dialog.show(fragment.getChildFragmentManager(), "usageAccessDialog");
        }

        // Retornar las estadísticas de uso
        return queryUsageStats;
    }

}