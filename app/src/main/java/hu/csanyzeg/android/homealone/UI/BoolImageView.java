package hu.csanyzeg.android.homealone.UI;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.CompoundButton;
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
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Interfaces.BoolSensor;
import hu.csanyzeg.android.homealone.Interfaces.BoolValue;
import hu.csanyzeg.android.homealone.Interfaces.Switch;
import hu.csanyzeg.android.homealone.R;
import hu.csanyzeg.android.homealone.Utils.HttpByteArrayDownloadUtil;
import hu.csanyzeg.android.homealone.Utils.HttpDownloadUtil;

public class BoolImageView extends ImageView implements BoolSensor, Switch {
    private OnBoolValueChangeListener onCheckChangeListener =null;
    boolean download = false;
    BoolData data;
    String id;
    Bitmap resTrue = null;
    Bitmap resFalse = null;

    Boolean value = null;

    public BoolImageView(Context context) {
        super(context);
        updateContent();
    }

    public BoolImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        updateContent();
    }

    public BoolImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        updateContent();
    }


    @Override
    public void setOnCheckChangeListener(OnBoolValueChangeListener onCheckChangeListener) {
        this.onCheckChangeListener = onCheckChangeListener;
    }

    @Override
    public OnBoolValueChangeListener getOnCheckChangeListener() {
        return onCheckChangeListener;
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            final boolean[] cancel = {false};
            if (!value) {
                if (getConfig().distance!=null && getConfig().distance < Data.GPSDistanceInMeter) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                    alert.setTitle(getConfig().display);
                    alert.setMessage("A légvonalban mért távolság otthonról nagyobb, mint " + String.format("%.0f", getConfig().distance) + " m. Folytatja a műveletet?");

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            cancel[0] = false;
                            setValue(true);
                            if (onCheckChangeListener != null) {
                                onCheckChangeListener.onChangeValueTrue();
                            }
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            cancel[0] = true;
                            setValue(false);
                        }
                    });

                    alert.show();
                }else {
                    if (onCheckChangeListener != null) {
                        onCheckChangeListener.onChangeValueTrue();
                    }
                }
            } else {
                if (onCheckChangeListener != null) {
                    onCheckChangeListener.onChangeValueFalse();
                }
            }
            if (!cancel[0]){
                setFastPolling();
            }

        }

    };

    public void setFastPolling() {
        data.setFastPollingRemainCount(Config.fastPollingCount);
    }
    public void updateContent(){
        if (value!= null) {
            if (resTrue!=null && resFalse != null) {
                if (value) {
                    setImageBitmap(resTrue);
                } else {
                    setImageBitmap(resFalse);
                }
            }else{
                if (download){
                    setImageResource(R.drawable.imagesensorviewerror);
                }else{
                    //setImageResource(R.drawable.imagesensorview);
                }
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
        if (this.config.isWrite()) {
            this.setOnClickListener(onClickListener);
        }
        if (config.iconOn != null && config.iconOff != null) {
            new HttpByteArrayDownloadUtil(){
                @Override
                protected void doAfterPostExecute(Result bytes) {
                    if (bytes.errorCode == ErrorCode.OK) {
                        resTrue = BitmapFactory.decodeByteArray(bytes.bytes, 0, bytes.bytes.length);
                        if (resTrue != null && resFalse != null) {
                            onDonwloadComplete();
                        }
                    }else{
                        onDonwloadError();
                    }
                }
            }.execute(Config.houseViewURL + "/" + config.iconOn);
            new HttpByteArrayDownloadUtil(){
                @Override
                protected void doAfterPostExecute(Result bytes) {
                    if (bytes.errorCode == ErrorCode.OK) {
                        resFalse = BitmapFactory.decodeByteArray(bytes.bytes, 0, bytes.bytes.length);
                        if (resTrue != null && resFalse != null) {
                            onDonwloadComplete();
                        }
                    }else{
                        onDonwloadError();
                    }
                }
            }.execute(Config.houseViewURL + "/" + config.iconOff);
        }
        //Teszt:
        // http://tuskeb.duckdns.org/homealone/
    }

    @Override
    public HashMap<String, BoolData> getData() {
        HashMap<String, BoolData> h = new HashMap<String, BoolData>();
        h.put(id, data);
        return h;
    }

    @Override
    public void addData(String id, BoolData d) {
        data = d;
        this.id = id;
        updateData();
    }

    @Override
    public void updateData() {
        setValue(data.currentValue());
    }
    public void onDonwloadError() {
        download = true;
        updateContent();
    }
    public void onDonwloadComplete(){
        download = true;
        updateContent();
        /*
        if (((AbsoluteLayout)getParent()) != null) {
            AbsoluteLayout.LayoutParams layoutParams = new AbsoluteLayout.LayoutParams(config.pozW, config.pozH, config.pozX, config.pozY);
            ((AbsoluteLayout) getParent()).updateViewLayout(this, layoutParams);
            setVisibility(VISIBLE);
            updateContent();
        }
        */
    }


}
