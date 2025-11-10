package com.example.pebble_app;

public class FocusModesRowModel {
    String focusModeName, focusModeIcon, focusModeBeginTime, focusModeEndTime, focusModeDays, focusModeType;
    int sessionId;
    //focusModeDays

    public FocusModesRowModel(int sessionId, String focusModeName, String focusModeIcon, String focusModeBeginTime,
                              String focusModeEndTime, String focusModeDays, String focusModeType) {
        this.sessionId = sessionId;
        this.focusModeName = focusModeName;
        this.focusModeIcon = focusModeIcon;
        this.focusModeBeginTime = focusModeBeginTime;
        this.focusModeEndTime = focusModeEndTime;
        this.focusModeDays = focusModeDays;
        this.focusModeType = focusModeType;
    }

    public String getFocusModeName() {
        return focusModeName;
    }

    public void setFocusModeName(String focusModeName) {
        this.focusModeName = focusModeName;
    }

    public String getFocusModeIcon() {
        return focusModeIcon;
    }

    public void setFocusModeIcon(String focusModeIcon) {
        this.focusModeIcon = focusModeIcon;
    }

    public String getFocusModeBeginTime() {
        return focusModeBeginTime;
    }

    public void setFocusModeBeginTime(String focusModeBeginTime) {
        this.focusModeBeginTime = focusModeBeginTime;
    }

    public String getFocusModeEndTime() {
        return focusModeEndTime;
    }

    public void setFocusModeEndTime(String focusModeEndTime) {
        this.focusModeEndTime = focusModeEndTime;
    }

    public String getFocusModeDays() {
        return focusModeDays;
    }

    public void setFocusModeDays(String focusModeDays) {
        this.focusModeDays = focusModeDays;
    }

    public String getFocusModeType() {
        return focusModeType;
    }

    public void setFocusModeType(String focusModeType) {
        this.focusModeType = focusModeType;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
}