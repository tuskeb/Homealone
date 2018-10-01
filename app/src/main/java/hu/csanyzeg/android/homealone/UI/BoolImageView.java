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
            /*AsyncTask<String, String, byte[]> task = new AsyncTask<String, String, byte[]>() {
                @Override
                protected byte[] doInBackground(String... strings) {
                    URL website = null;
                    try {
                        website = new URL(strings[0]);
                        BufferedInputStream in = new BufferedInputStream(website.openStream());
                        byte dataBuffer[] = new byte[4096];
                        int bytesRead;
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        int size = 0;
                        while ((bytesRead = in.read(dataBuffer, size, 1024)) != -1) {
                            //System.out.println(bytesRead);
                            if (dataBuffer.length - 2048 <size){
                                byte[] dataBuffer2 = new byte[dataBuffer.length + 4096];
                                System.arraycopy(dataBuffer, 0, dataBuffer2, 0, dataBuffer.length);
                                System.out.println(dataBuffer.length + " - - - - " + dataBuffer2.length);
                                dataBuffer = dataBuffer2;
                            }
                            size += bytesRead;
                        }

                        System.out.println("-----------------" + size);
                        return Arrays.copyOf(dataBuffer,size);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(byte[] bytes) {
                    super.onPostExecute(bytes);
                    setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
                    onDonwloadComplete();
                }
            };


            task.execute("");
            */
            new HttpByteArrayDownloadUtil(){
                @Override
                protected void onPostExecute(byte[] bytes) {
                    super.onPostExecute(bytes);
                    resTrue = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    if (resTrue != null && resFalse != null) {
                        onDonwloadComplete();
                    }
                }
            }.execute(config.iconOn);
            new HttpByteArrayDownloadUtil(){
                @Override
                protected void onPostExecute(byte[] bytes) {
                    super.onPostExecute(bytes);
                    resFalse = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    if (resTrue != null && resFalse != null) {
                        onDonwloadComplete();
                    }
                }
            }.execute(config.iconOff);
        }
        //TODO: Letöltést itt megkezdeni, és a letöltés végén meghívni az onDownloadComplete metódust.
        //Teszt:
        // http://tuskeb.duckdns.org/homealone/
        //onDonwloadComplete();
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
