package com.artqueen.snappy;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ListPhotos extends ActionBarActivity {

    final String dbFileName = "tourDb.txt";
    public static PhotoDb myDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_photos);
        final ListView list = (ListView) findViewById(R.id.listPhotolv);

        MyAdapter adapter = new MyAdapter(ListPhotos.this, R.layout.listviewrow, populatePhotoDb());
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhotoDb  s = (PhotoDb) parent.getAdapter().getItem(position);
                myDetails=s;
               //Log.d(">>>",s.getName());
                Intent intent = new Intent(ListPhotos.this, PhotoDetails.class);
                //intent.putExtra("details", (Serializable) s);
                startActivity(intent);
            }
        });
    }




    protected String[] ReadMytextFile() {
        StringBuilder buf = new StringBuilder();
        try {
            FileInputStream fos = openFileInput(dbFileName);
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

    public List<PhotoDb>  populatePhotoDb(){
    String[] RowValues = ReadMytextFile();

        List<PhotoDb> myPhotoDbList = new ArrayList<PhotoDb>();

        for(int i=0;i<RowValues.length;i++){
            String[] columnValues = RowValues[i].split(",");
            for(int j=0;j<columnValues.length;j++) {
                Log.d("test >> ",""+columnValues[j].toString());
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
