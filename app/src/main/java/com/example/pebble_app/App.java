package com.example.pebble_app;

import android.graphics.drawable.Drawable;

public class App {
    private Drawable appIcon;
    private String appName;
    private String appCategory;
    private String appUsage;
    private int usagePercentage;

    public App(Drawable appIcon, String appName, String appCategory, String appUsage, int usagePercentage) {
        this.appIcon = appIcon;
        this.appName = appName;
        this.appCategory = appCategory;
        this.appUsage = appUsage;
        this.usagePercentage = usagePercentage;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppCategory() {
        return appCategory;
    }

    public String getAppUsage() {
        return appUsage;
    }

    public int getUsagePercentage() {
        return usagePercentage;
    }
}
