package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import java.util.HashMap;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.NumberData;
import hu.csanyzeg.android.homealone.Interfaces.NumberSensor;
import hu.csanyzeg.android.homealone.R;
import hu.csanyzeg.android.homealone.Utils.HttpByteArrayDownloadUtil;

public class NumberImageView extends ImageView implements NumberSensor {
    public NumberImageView(Context context) {
        super(context);
    }

    public NumberImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    boolean download = false;
    NumberData data;
    String suffix="";
    int decimal = 2;
    String id;
    Bitmap resImage = null;

    Double value = null;


    @Override
    public Double getMin() {
        return config.min;
    }

    @Override
    public void setMin(Double value) {

    }

    @Override
    public Double getMax() {
        return config.max;
    }

    @Override
    public void setMax(Double value) {

    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void setSuffix(String unit) {
        this.suffix = unit;
        updateContent();
    }

    @Override
    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }

    protected float fontSize ;
    protected float getFontSize(int size){
        return fontSize * (1.0f + (float)size * 0.3f);
    }


    public void updateContent(){
        if (value!= null) {
            if (resImage !=null) {
                setImageBitmap(resImage);
            }else{
                if (download){
                    setImageResource(R.drawable.imagesensorviewerror);
                }else{
                    setImageResource(R.drawable.imagesensorview);
                }
            }
        }
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1.5f, getResources().getDisplayMetrics());
        Paint txtPaint = new Paint();
        super.onDrawForeground(canvas);
        txtPaint.setAntiAlias(true);
        txtPaint.setTextSize(getFontSize(1));
        txtPaint.setColor(Color.BLACK);
        String w = String.format("%." + decimal + "f", value) + " " + suffix;
        canvas.drawText(w, getWidth()/2 - txtPaint.measureText(w)/2,  getHeight()-txtPaint.getTextSize()*0.8f, txtPaint);
    }

    public void setValue(Double b){
        value = b;
        invalidate();
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

        decimal = config.precision;
        suffix = config.suffix;


        if (config.icon != null && config.icon != "") {
            new HttpByteArrayDownloadUtil(){
                @Override
                protected void onPostExecute(Result bytes) {
                    super.onPostExecute(bytes);
                    if (bytes.errorCode == ErrorCode.OK) {
                        resImage = BitmapFactory.decodeByteArray(bytes.bytes, 0, bytes.bytes.length);
                        if (resImage != null) {
                            onDonwloadComplete();
                        }
                    }else{
                        onDonwloadError();
                    }
                }
            }.execute(Config.houseViewURL + "/" + config.icon);
        }
    }

    @Override
    public HashMap<String, NumberData> getData() {
        HashMap<String, NumberData> h = new HashMap<String, NumberData>();
        h.put(id, data);
        return h;
    }

    @Override
    public void addData(String id, NumberData d) {
        data = d;
        this.id = id;
        //System.out.println(d.getConfig().id);
        updateData();
    }

    @Override
    public void updateData() {
        setValue(data.currentValue());
        //invalidate();
    }
    public void onDonwloadError() {
        download = true;
        updateContent();
    }
    public void onDonwloadComplete(){
        download = true;
        updateContent();
    }
}
