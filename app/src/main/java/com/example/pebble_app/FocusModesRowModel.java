package com.example.pebble_app;

public class FocusModesRowModel {
    String focusModeName, focusModeTime, focusModeDays, focusModeIcon, focusModeType;

    public FocusModesRowModel(String focusModeName, String focusModeTime
            , String focusModeDays, String focusModeIcon, String focusModeType){

        this.focusModeName = focusModeName;
        this.focusModeTime = focusModeTime;
        this.focusModeDays = focusModeDays;
        this.focusModeIcon = focusModeIcon;
        this.focusModeType = focusModeType;
    }



    public String getFocusModeName() {
        return focusModeName;
    }

    public String getFocusModeTime() {
        return focusModeTime;
    }

    public String getFocusModeDays() {
        return focusModeDays;
    }

    public String getFocusModeIcon() {
        return focusModeIcon;
    }

    public String getFocusModeType() {
        return focusModeType;
    }
}
