package com.artqueen.snappy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;


public class CaptureActivity extends ActionBarActivity {

    Button captureBtn,uploadBtn;
    Uri uri = null;
    final static int CAPTURE_IMAGE_REQUEST_CODE = 101;
    File file = null;
    int count = 0;
    Bitmap bmp = null;
    final String countFileName = "count.txt";
    final String dbFileName = "tourDb.txt";
    EditText name,desc;
    GPSTracker gps;
    double latitude,longitude;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       count =  readPreference();

                capture();

        uploadBtn = (Button) findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();

                //networking
                uploadPhoto();

            }
        });
        Button BtnSnap = (Button) findViewById(R.id.BtnSnap);
        BtnSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture();
            }
        });

    }

    private int readPreference() {
        return (preferences.getInt("count",0));
    }

    //networking
    private void uploadPhoto() {
        new AsyncTask<Uri, Void, Void>(){
            @Override
            protected Void doInBackground(Uri... arg) {
                String f = getRealPathFromURI(arg[0]);
                Transfer.uploadFile(f);
                return null;
            }
            protected void onPostExecute(Void v) {
                new AsyncTask<Uri, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Uri... arg) {
                        File f = new File(getRealPathFromURI(arg[0]));
                        String n = f.getName();
                        try {
                            InputStream in = new URL(String.format("%s/images/%s", Transfer.base, n)).openStream();
                            Bitmap b = BitmapFactory.decodeStream(in);
                            return b;
                        } catch (Exception e) {
                            return null;
                        }
                    }

                    protected void onPostExecute(Bitmap b) {
                        //ImageView v = (ImageView) findViewById(R.id.imageView2);
                        //v.setImageBitmap(b);
                    }
                }.execute(uri);
            }
        }.execute(uri);
    }

    //networking
    private String getRealPathFromURI(Uri uri)
    {
        Log.d("log uri",""+uri.toString());
        String filePath;

        if (uri != null && "content".equals(uri.getScheme())) {
            Cursor cursor = getContentResolver().
                    query(uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
            cursor.moveToFirst();
            filePath = cursor.getString(0);
            cursor.close();
        } else {
            filePath = uri.getPath();
        }
        Log.d("log uri",""+filePath);
        return(filePath);
    }


    private void storeCountInSP() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("count",count);
        editor.commit();
    }

    //code to capture photo
    private void capture() {
        //checking the count value
        if(count!=0) {
            Log.e("test1","testing");
            StringBuilder buf = new StringBuilder();
            //reading count value from file
            try {
                Log.e("test2","testing");
                FileInputStream fos = openFileInput(countFileName);
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
            //saving count value
            String save = buf.toString();
            Log.e("test3",save);
            try {
                //parsing from String to int
                count = Integer.parseInt(save);
            }catch(NumberFormatException nfe){
                nfe.printStackTrace();
            } Log.e("test4", "" + count);
        }
        //opening camera intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //saving file name of image
            file = new File("/sdcard/", "IMG_" +count+ ".jpg");
            uri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, CAPTURE_IMAGE_REQUEST_CODE);
          }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.e("test", "testing");

                try {
                    //obtaining the file from the countFileName stored
                        FileInputStream fis = new FileInputStream(file);
                        bmp = BitmapFactory.decodeStream(fis);
                    }catch (Exception e) {
                    Log.i("pics", e.toString());
                }
                ImageView imgview = (ImageView) findViewById(R.id.imageView);
                imgview.setImageBitmap(getRoundedCornerBitmap(bmp,100));
                name = (EditText) findViewById(R.id.nameEditText);
                name.setText("IMG_"+count);
            }
        }
    }

    public void upload()
    {
      count++;

      write();
        writeToDb();
        storeCountInSP();
        Toast.makeText(CaptureActivity.this,"File Uploaded",Toast.LENGTH_SHORT).show();

        startActivity(new Intent(CaptureActivity.this, ListPhotos.class));
    }

    //writing the contents to dataBase
    private void writeToDb() {

        int dbId = count;
        String dbName = name.getText().toString();
        desc = (EditText) findViewById(R.id.descEditText);
        String dbDesc = desc.getText().toString();

        String db = dbId+","+dbName+","+dbDesc+","+latitude+","+longitude+";";
        if((count-1)!=0) {
            try {
                FileOutputStream f = openFileOutput(dbFileName, Context.MODE_APPEND);
                PrintWriter w = new PrintWriter(f);
                w.println(db);
                w.close();
                f.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            try {
                FileOutputStream f = openFileOutput(dbFileName, Context.MODE_PRIVATE);
                PrintWriter w = new PrintWriter(f);
                w.println(db);
                w.close();
                f.close();
            } catch (Exception e) {
                e.printStackTrace();
            }



    }

    //to write the count value to file
    public void write() {
        String contents = String.valueOf(count);
        try {
            FileOutputStream f = openFileOutput(countFileName, Context.MODE_PRIVATE);
            PrintWriter w = new PrintWriter(f);
            w.println(contents);
            w.close();
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void onToggleClick(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            getLocation();

        } else {
            latitude = 0.0;
            longitude = 0.0;

        }
    }

    private void getLocation() {

        // create class object
        gps = new GPSTracker(CaptureActivity.this);
        // check if GPS enabled
        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

    }
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

}
