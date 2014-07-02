package com.nelepovds.ndutils.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.nelepovds.ndutils.common.Cache;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dmitrynelepov on 01.07.14.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private final Cache cache;
    private final ImageView bmImage;

    public DownloadImageTask(ImageView bmImage, Cache cache) {
        this.bmImage = bmImage;
        this.cache = cache;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            String fileName = urldisplay.substring(urldisplay.lastIndexOf('/') + 1, urldisplay.length());
            File imageFile = null;
            if (cache.isFileExtistInCache(fileName)){
                //Load from file
                imageFile = cache.getFileFromCache(fileName);
            }else {
                //Download
                imageFile = this.downloadFile(urldisplay,fileName);
            }
            mIcon11 = BitmapFactory.decodeStream(new FileInputStream(imageFile));
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    public File downloadFile(String url, String fileName) throws IOException {
        InputStream in = new java.net.URL(url).openStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataInputStream dis = new DataInputStream(in);
        byte[] buffer = new byte[1024];// In bytes
        int realyReaded;
        while ((realyReaded = dis.read(buffer)) > -1) {
            baos.write(buffer, 0, realyReaded);
        }
        return this.cache.saveFile(fileName,baos);
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}