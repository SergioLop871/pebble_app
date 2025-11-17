package com.example.pebble_app;

import android.graphics.drawable.Drawable;

public class AppStatisticsRowModel {
    String appName, appType, appTime;
    Drawable appIcon;
    int appState;

    public AppStatisticsRowModel(String appName, String appType, String appTime,
                                 Drawable appIcon, int appState) {
        this.appName = appName; //Nombre de app
        this.appType = appType; //Tipo de app (distractora, productivo)
        this.appTime = appTime; //Tiempo de uso de app (formato: "Xh Ym")
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

    public Drawable getAppIcon() {
        return appIcon;
    }

    public int getAppState() {
        return appState;
    }
}
