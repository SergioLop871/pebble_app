package com.example.pebble_app;

public class DialogAddAplicationRowModel {

    String appName;
    int appIcon;
    Boolean appSelected;

    public DialogAddAplicationRowModel(String appName, int appIcon, Boolean appSelected){
        this.appName = appName;
        this.appIcon = appIcon;
        this.appSelected = appSelected;
    }

    public String getAppName() {
        return appName;
    }

    public int getAppIcon() {
        return appIcon;
    }

    public Boolean getAppSelected() {
        return appSelected;
    }

    public void setAppSelected(Boolean appSelected) {
        this.appSelected = appSelected;
    }
}
