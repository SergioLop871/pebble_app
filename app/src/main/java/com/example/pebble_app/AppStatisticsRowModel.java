package com.example.pebble_app;

public class AppStatisticsRowModel {
    String appName, appType, appTime;
    int appIcon, appState;

    public AppStatisticsRowModel(String appName, String appType, int appTime,
                                 int appIcon, int appState) {
        this.appName = appName; //Nombre de app
        this.appType = appType; //Tipo de app (distractora, productivo)
        this.appTime = appTime + "h " + "00m"; //Tiempo de uso de app (cambiar segun esquema)
        this.appIcon = appIcon; //Logo/Icono de la app
        this.appState = appState; //Icono de estado de la app (bloqueado, desbloqueado)
    }

    public String getAppName() {
        return appName;
    }

    public String getAppType() {
        return appType;
    }

    public String getAppTime() {
        return appTime;
    }

    public int getAppIcon() {
        return appIcon;
    }

    public int getAppState() {
        return appState;
    }
}
