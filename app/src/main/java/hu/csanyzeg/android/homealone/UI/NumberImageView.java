package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.HashMap;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.NumberData;
import hu.csanyzeg.android.homealone.Interfaces.NumberSensor;

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

    @Override
    public Double getMin() {
        return null;
    }

    @Override
    public void setMin(Double value) {

    }

    @Override
    public Double getMax() {
        return null;
    }

    @Override
    public void setMax(Double value) {

    }

    @Override
    public Double getValue() {
        return null;
    }

    @Override
    public void setValue(Double value) {

    }

    @Override
    public void setSuffix(String unit) {

    }

    @Override
    public void setDecimal(int decimal) {

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
        return null;
    }

    @Override
    public void setConfig(Config config) {

    }

    @Override
    public HashMap<String, NumberData> getData() {
        return null;
    }

    @Override
    public void addData(String id, NumberData d) {

    }

    @Override
    public void updateData() {

    }
}
