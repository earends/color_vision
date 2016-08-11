package com.appspot.usbhidterminal;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.usbhidterminal.core.info;

public class PatientInfo extends Activity {

    private TextView nme;
    private TextView db;
    private TextView gn;
    private TextView dte;
    private TextView loc;
    private TextView phn;
    private TextView eml;
    private TextView nmg;
    private TextView rln;
    private TextView phng;
    private TextView emg;
    private ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        nme = (TextView) findViewById(R.id.textView6);
        db = (TextView) findViewById(R.id.textView14);
        gn = (TextView) findViewById(R.id.textView15);
        phn = (TextView) findViewById(R.id.textView16);
        eml = (TextView) findViewById(R.id.textView17);
        nmg = (TextView) findViewById(R.id.textView18);
        rln = (TextView) findViewById(R.id.textView19);
        phng = (TextView) findViewById(R.id.textView20);
        emg = (TextView) findViewById(R.id.textView21);
        loc = (TextView) findViewById(R.id.textView22);
        dte = (TextView) findViewById(R.id.textView23);
        img = (ImageView) findViewById(R.id.imageView2);

        int Position = info.pos;
        String Name = "";
        String dob = "";
        String gen = "";
        String Date = "";
        String Location = "";
        String Phone = "";
        String Email = "";
        String Nameg = "";
        String Relation = "";
        String PhoneG = "";
        String EmailG = "";
        int index = 12*Position;
        Name = info.arr4[index];
        dob = info.arr4[index + 1];
        gen = info.arr4[index + 2];
        Date = info.arr4[index + 3];
        Location = info.arr4[index + 4];
        Phone = info.arr4[index + 5];
        Email = info.arr4[index + 6];
        Nameg = info.arr4[index + 7];
        Relation = info.arr4[index + 8];
        PhoneG = info.arr4[index + 9];
        EmailG = info.arr4[index + 10];
        String bitStr = info.arr4[index + 11];
        nme.setText(Name);
        db.setText(dob);
        gn.setText(gen);
        phn.setText(Phone);
        eml.setText(Email);
        nmg.setText(Nameg);
        rln.setText(Relation);
        phng.setText(PhoneG);
        emg.setText(EmailG);
        loc.setText(Location);
        dte.setText(Date);
        for (int i = 0; i < info.arr4.length; i ++) {
            Log.d("array components", info.arr4[i]);
        }

        if (!bitStr.equals("null")) {
            Bitmap bit = StringToBitmap(bitStr);
            img.setImageBitmap(bit);
        }




    }

    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (NullPointerException e) {
            e.getMessage();
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }



}
