package com.artqueen.snappy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class MyAdapter extends ArrayAdapter<PhotoDb> {

    private List<PhotoDb> items;
    File file;
    Bitmap bmp;

    public MyAdapter(Context context, int resource, List<PhotoDb> items) {
        super(context, resource, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.listviewrow, null);
        PhotoDb p = items.get(position);

        if (p != null) {
            TextView name = (TextView) v.findViewById(R.id.txtName);
            name.setText(p.getName());
            TextView id = (TextView) v.findViewById(R.id.txtDesc);
            id.setText(p.getDesc());
            final ImageView image = (ImageView) v.findViewById(R.id.imgPreview);

            file = new File("/sdcard/", "IMG_" +(p.getId()-1)+ ".jpg");

            try {
                //obtaining the file from the countFileName stored
                FileInputStream fis = new FileInputStream(file);
                bmp = BitmapFactory.decodeStream(fis);
                image.setImageBitmap(bmp);
            }catch (Exception e) {
                Log.i("pics", e.toString());
            }


          /*  new AsyncTask<String, Void, Bitmap>() {
                Bitmap b;
                @Override
                protected Bitmap doInBackground(String... id) {
                    String url=String.format("http://dkiong.no-ip.biz/site/static/photo/%s.jpg",id[0]);
                    try {
                        InputStream in = new URL(url).openStream();
                        b = BitmapFactory.decodeStream(in);
                    } catch (Exception ex) {
                        Log.e("Bitmap Error", ex.toString());
                    }
                    return b;
                }
                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    image.setImageBitmap(bitmap);
                }
            }.execute();
*/
        }
        return v;
    }
}