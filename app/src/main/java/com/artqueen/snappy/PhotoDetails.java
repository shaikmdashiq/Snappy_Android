package com.artqueen.snappy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.HashMap;


public class PhotoDetails extends ActionBarActivity {
    PhotoDb obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_details);
        File file;
        Bitmap bmp=null;

        obj = ListPhotos.myDetails;
        TextView t1 = (TextView) findViewById(R.id.txtNameDetails);
        TextView t2 = (TextView) findViewById(R.id.txtDescDetails);
        Button btnViewLocation = (Button) findViewById(R.id.btnViewLocation);
        t1.setText(obj.getName());
        t2.setText(obj.getDesc());

        final ImageView image = (ImageView) findViewById(R.id.imgDetails);

        file = new File("/sdcard/", "IMG_" +(obj.getId()-1)+ ".jpg");

        try {
            //obtaining the file from the countFileName stored
            FileInputStream fis = new FileInputStream(file);
            bmp = BitmapFactory.decodeStream(fis);
        }catch (Exception e) {
            Log.i("pics", e.toString());
        }
        image.setImageBitmap(getRoundedCornerBitmap(bmp,100));

        Double lat = obj.getLatitude();
        if(lat==0.0) {
            btnViewLocation.setVisibility(View.INVISIBLE);
        }else {
            btnViewLocation.setVisibility(View.VISIBLE);
        }
        btnViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent i = new Intent(PhotoDetails.this,MapsActivity.class);
                i.putExtra("details",(Serializable)obj);
                startActivity(i);

            }
        });

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
