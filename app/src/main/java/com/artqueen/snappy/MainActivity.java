package com.artqueen.snappy;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    Button snapBtn,btnGallery,btnViewMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        snapBtn = (Button) findViewById(R.id.BtnSnap);
        btnGallery = (Button) findViewById(R.id.btnGallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ListPhotos.class));
            }
        });
        btnViewMap = (Button) findViewById(R.id.btnViewAllMap);
        btnViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i = new Intent(MainActivity.this,ListAllMapsActivity.class);
                i.putExtra("listofphotos", (java.io.Serializable) populatePhotoDb());
                startActivity(i);
            }
        });

        snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CaptureActivity.class));
            }
        });

    }




    protected String[] ReadMytextFile() {
        StringBuilder buf = new StringBuilder();
        try {
            FileInputStream fos = openFileInput("tourDb.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(fos));
            String s;
            while ((s = r.readLine()) != null) {
                buf.append(s);
                buf.append("\r\n");
            }
            r.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String save = buf.toString();
        //editText.setText(save);

        String[] array = save.split(";");
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }

        return array;
    }

    public List<PhotoDb> populatePhotoDb(){
        String[] RowValues = ReadMytextFile();

        List<PhotoDb> myPhotoDbList = new ArrayList<PhotoDb>();

        for(int i=0;i<RowValues.length;i++){
            String[] columnValues = RowValues[i].split(",");
            for(int j=0;j<columnValues.length;j++) {
                Log.d("test >> ", "" + columnValues[j].toString());
            }

            try {
                PhotoDb obj = new PhotoDb(Integer.parseInt(columnValues[0].trim()),
                        columnValues[1].trim(), columnValues[2].trim(),
                        Double.parseDouble(columnValues[3].trim()),
                        Double.parseDouble(columnValues[4].trim()));
                myPhotoDbList.add(obj);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return myPhotoDbList;
    }
}
