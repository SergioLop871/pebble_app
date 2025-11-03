package com.example.pebble_app;

import android.graphics.drawable.Drawable;

public class AddAplicationDialogRowModel {

    private String appName;
    private Drawable appIcon;
    private Boolean appSelected;

    public AddAplicationDialogRowModel(String appName, Drawable appIcon, Boolean appSelected){
        this.appName = appName;
        this.appIcon = appIcon;
        this.appSelected = appSelected;
    }

    public String getAppName() {
        return appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public Boolean getAppSelected() {
        return appSelected;
    }

    public void setAppSelected(Boolean appSelected) {
        this.appSelected = appSelected;
    }
}
