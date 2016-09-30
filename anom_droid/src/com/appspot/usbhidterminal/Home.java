package com.appspot.usbhidterminal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.appspot.usbhidterminal.core.info;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Home extends Activity {
    private int gender; // 1 = male 2 = female
    private EditText name;
    private EditText DOB;
    private RadioGroup genda;
    private EditText date;
    private EditText location;
    private EditText phone;
    private EditText email;
    private EditText nameG;
    private EditText relation;
    private EditText phoneG;
    private EditText emailG;
    private String genderStr;
    private String bitString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();



    }

    public void data_click(View v) {
        Intent i = new Intent(getApplicationContext(), info.class);
        startActivity(i);
    }
    public void clear_click(View v) {
        Intent i = new Intent(getApplicationContext(), Home.class);
        startActivity(i);
    }

    public void male_click(View v) {
        genderStr = "male";
    }
    public void female_click(View v) {
        genderStr = "female";
    }
    private void initUI() {
        name = (EditText) findViewById(R.id.name_text);
        DOB = (EditText) findViewById(R.id.dob_text);
        genda = (RadioGroup) findViewById(R.id.gender);
        date = (EditText) findViewById(R.id.date_text);
        location = (EditText) findViewById(R.id.location_text);
        phone = (EditText) findViewById(R.id.phone_text);
        email = (EditText) findViewById(R.id.email_text);
        nameG = (EditText) findViewById(R.id.name_guardian_text);
        relation = (EditText) findViewById(R.id.relation_text);
        //phoneG = (EditText) findViewById(R.id.phone_guardian_text);
        //emailG = (EditText) findViewById(R.id.email_guardian_text);

    }




    public void test_click(View v) {
        String Name = name.getText().toString();
        String dob = DOB.getText().toString();
        String gen = genderStr;
        String Date = date.getText().toString();
        //String Location = location.getText().toString();
        String Phone = phone.getText().toString();
        String Email = email.getText().toString();
        String Nameg = nameG.getText().toString();
        String Relation = relation.getText().toString();
        //String PhoneG = phoneG.getText().toString();
        //String EmailG = emailG.getText().toString();
        String total = Name + "," + dob + "," + gen + "," + Date + "," + "," + Phone + "," + Email + "," + Nameg + "," + Relation + ","+  "," + bitString + ",";

        to_write(Name,total);


        Intent i = new Intent(getApplicationContext(),USBHIDTerminal.class);
        startActivity(i);
    }
    static final int REQUEST_IMAGE_CAPTURE = 1;

    public void image_click(View v)  {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //ImageView image = (ImageView) findViewById(R.id.patient_image);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //image.setImageBitmap(imageBitmap);
            bitString = BitmapToString(imageBitmap);
        }
    }

    public void about_click(View v) {
        Intent i = new Intent(getApplicationContext(),info.class);
        startActivity(i);
    }

    public void to_write(String file, String str) {
        String prevStr = readFromFile();

        String fileName = "config4.txt";

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(fileName, Context.MODE_PRIVATE));
            if (prevStr != null) {
                outputStreamWriter.write(prevStr);
                Log.d("YOUR TAG", "PREV STRING NOT NULL");
            }
            outputStreamWriter.write(str);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.d("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";
        try {
            InputStream inputStream = openFileInput("config4.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static String BitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);
            return temp;
        } catch (NullPointerException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }


}
