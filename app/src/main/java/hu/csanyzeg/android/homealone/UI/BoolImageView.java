package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PictureDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

import java.util.HashMap;

import hu.csanyzeg.android.homealone.Data.BoolData;
import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Interfaces.BoolSensor;
import hu.csanyzeg.android.homealone.Interfaces.BoolValue;

public class BoolImageView extends ImageView implements BoolSensor {

    int resTrue = 0;
    int resFalse = 0;

    public BoolImageView(Context context) {
        super(context);
    }

    public BoolImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BoolImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setTrueImage(int res){
        resTrue = res;
    }

    public void setFalseImage(int res){
        resFalse = res;
    }

    public void setValue(Boolean b){
        if (b){
            setImageResource(resTrue);
        }else{
            setImageResource(resFalse);
        }
    }

    @Override
    public Boolean getValue() {
        return null;
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
    public void setConfig(Config config) {
        this.config = config;
        //TODO: Letöltést itt megkezdeni, és a letöltés végén meghívni az onDownloadComplete metódust.
        //Teszt:
        // http://tuskeb.duckdns.org/homealone/
        onDonwloadComplete();
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
        }
    }
}
