package com.appspot.usbhidterminal.core;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.usbhidterminal.Home;
import com.appspot.usbhidterminal.PatientInfo;
import com.appspot.usbhidterminal.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class info extends Activity {

    private String lines;
    private TextView text;
    private ArrayList<String> str;
    private String [] arr3;
    public static String[] arr4;
    public static int pos;
    public static int posInArr;
    private ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        list = (ListView) findViewById(R.id.listView);
        str = new ArrayList<String>();


        splitUp();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr3);


        // Assign adapter to ListView
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;
                pos = itemPosition;

                // ListView Clicked item value
                String  itemValue = (String) list.getItemAtPosition(position);

                // Show Alert

                Intent i = new Intent(getApplicationContext(), PatientInfo.class);
                startActivity(i);

            }

        });


    }

    public void back_click(View v) {
        Intent i = new Intent(getApplicationContext(), Home.class);
        startActivity(i);
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

    public void splitUp() {
        arr4 = readFromFile().split(",");
        Log.d("YOUR TAG", Integer.toString(arr4.length));
        int length = arr4.length/12;
         arr3 = new String[length];
        for (int i = 0; i < length; i ++) {
            int index = i*12;
            arr3[i] = arr4[index];
        }

    }

}
