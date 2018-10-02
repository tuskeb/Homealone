package hu.csanyzeg.android.homealone.UI;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import hu.csanyzeg.android.homealone.Data.BoolData;
import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Interfaces.BoolSensor;
import hu.csanyzeg.android.homealone.Interfaces.BoolValue;
import hu.csanyzeg.android.homealone.Utils.HttpByteArrayDownloadUtil;
import hu.csanyzeg.android.homealone.Utils.HttpDownloadUtil;

public class BoolImageView extends ImageView implements BoolSensor {



    Bitmap resTrue = null;
    Bitmap resFalse = null;

    Boolean value = null;

    public BoolImageView(Context context) {
        super(context);
    }

    public BoolImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BoolImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void updateContent(){
        if (value!= null) {
            if (value) {
                setImageBitmap(resTrue);
            } else {
                setImageBitmap(resFalse);
            }
        }
    }

    public void setValue(Boolean b){
        value = b;
        updateContent();
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public String getSensorId() {
        return null;
    }

    @Override
    public void setSensorId(String sensorId) {

    }

    @Override
    public Config getConfig() {
        return config;
    }


    private Config config;
    @Override
    public void setConfig(final Config config) {

        this.config = config;
        if (config.iconOn != null && config.iconOff != null) {
            new HttpByteArrayDownloadUtil(){
                @Override
                protected void onPostExecute(Result bytes) {
                    super.onPostExecute(bytes);
                    if (bytes.errorCode == ErrorCode.OK) {
                        resTrue = BitmapFactory.decodeByteArray(bytes.bytes, 0, bytes.bytes.length);
                        if (resTrue != null && resFalse != null) {
                            onDonwloadComplete();
                        }
                    }
                }
            }.execute(config.iconOn);
            new HttpByteArrayDownloadUtil(){
                @Override
                protected void onPostExecute(Result bytes) {
                    super.onPostExecute(bytes);
                    if (bytes.errorCode == ErrorCode.OK) {
                        resFalse = BitmapFactory.decodeByteArray(bytes.bytes, 0, bytes.bytes.length);
                        if (resTrue != null && resFalse != null) {
                            onDonwloadComplete();
                        }
                    }
                }
            }.execute(config.iconOff);
        }
        //Teszt:
        // http://tuskeb.duckdns.org/homealone/
    }

    @Override
    public HashMap<String, BoolData> getData() {
        return null;
    }

    @Override
    public void addData(String id, BoolData d) {

    }

    @Override
    public void updateData() {

    }

    public void onDonwloadComplete(){
        if (((AbsoluteLayout)getParent()) != null) {
            AbsoluteLayout.LayoutParams layoutParams = new AbsoluteLayout.LayoutParams(100, 200, config.pozX, config.pozY);
            ((AbsoluteLayout) getParent()).updateViewLayout(this, layoutParams);
            setVisibility(VISIBLE);
            updateContent();
        }
    }
}
