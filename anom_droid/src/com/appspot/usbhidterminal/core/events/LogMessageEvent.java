package com.appspot.usbhidterminal.core.events;

import android.util.Log;

public class LogMessageEvent {

    private final String data;

    public LogMessageEvent(String data) {
        Log.d("LogmessagesEvent", "CONSTRUCTOR");
        this.data = data;
    }

    public String getData() {
        Log.d("LOGMESSAGEEVENT GETTER", data);
        return data;
    }
}