package com.appspot.usbhidterminal.core.events;

import android.util.Log;

public class ShowDevicesListEvent {

    private final CharSequence devicesName[];

    public ShowDevicesListEvent(CharSequence devicesName[]) {
        Log.d("showdevices list event", "CONSTRUCTOR");
        this.devicesName = devicesName;
        Log.d("show devices list event", String.valueOf(devicesName));
        String[] mEntriesString = new String[this.devicesName.length];
        Log.d("show devices list event", "before for loop" + "L.. " + mEntriesString.length);
        int i=0;
        for(CharSequence ch: this.devicesName){

            mEntriesString[i++] = ch.toString();
            //mEntriesString[] = ch.toString();
        }
        Log.d(mEntriesString[0],"Your output");
    }

    public CharSequence[] getCharSequenceArray() {
        Log.d("show devices listevent", devicesName.toString());
        return devicesName;
    }

}