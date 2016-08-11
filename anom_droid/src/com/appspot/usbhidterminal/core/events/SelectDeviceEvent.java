package com.appspot.usbhidterminal.core.events;

import android.util.Log;

public class SelectDeviceEvent {
    private int device;

    public SelectDeviceEvent(int device) {
        Log.d("SELECTDEVICEeVENT", "CONSTRUCTOR");
        this.device = device;
    }

    public int getDevice() {
        Log.d("selectDeviceevent-getDevice", Integer.toString(device));
        return device;
    }
}