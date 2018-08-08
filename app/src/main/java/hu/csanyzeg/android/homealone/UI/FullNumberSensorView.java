package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Data.NumberData;
import hu.csanyzeg.android.homealone.Interfaces.InitableUI;
import hu.csanyzeg.android.homealone.Interfaces.NumberSensor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by tanulo on 2018. 06. 29..
 */

public class FullNumberSensorView extends LinearLayout implements NumberSensor, InitableUI {

    protected GraphNumberSensorView graphNumberSensorView;
    protected NameView nameView;
    protected HashMap<String, NumberData> data = new HashMap<>();
    protected Config config = null;


    public FullNumberSensorView(Context context) {
        super(context);
        inflate();
        init();
    }

    public FullNumberSensorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate();
        init();
    }

    public FullNumberSensorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate();
        init();
    }

    @Override
    public void inflate(){
        LayoutInflater.from(getContext()).inflate(hu.csanyzeg.android.homealone.R.layout.full_number_sensor_view, this);
    }


    @Override
    public void inflateparams(Context context, @Nullable AttributeSet attrs) {

    }

    @Override
    public void init() {
        graphNumberSensorView = getRootView().findViewById(hu.csanyzeg.android.homealone.R.id.fullSensorGraphView);
        nameView = getRootView().findViewById(hu.csanyzeg.android.homealone.R.id.fullSensorNameView);
    }

    @Override
    public Double getMin() {
        return graphNumberSensorView.getMin();
    }

    @Override
    public Double getValue() {
        return graphNumberSensorView.getValue();
    }

    @Override
    public String getSensorId() {
        return graphNumberSensorView.getSensorId();
    }

    @Override
    public void setMin(Double value) {
        graphNumberSensorView.setMin(value);
    }

    @Override
    public void setValue(Double value) {
        graphNumberSensorView.setValue(value);
    }

    @Override
    public void setSensorId(String sensorId) {
        graphNumberSensorView.setSensorId(sensorId);
    }

    @Override
    public void setValue(Double value, Date lastUpdateDate) {
        graphNumberSensorView.setValue(value);
    }

    @Override
    public Double getMax() {
        return graphNumberSensorView.getMax();
    }

    @Override
    public void setSuffix(String unit) {
        graphNumberSensorView.setSuffix(unit);
    }

    @Override
    public void setMax(Double value) {
        graphNumberSensorView.setMax(value);
    }

    @Override
    public void setText(String text) {
        nameView.setText(text);
    }

    @Override
    public void setLastUpdateDate(Date lastUpdateDate) {
        graphNumberSensorView.setLastUpdateDate(lastUpdateDate);
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public void setConfig(Config config) {
        this.config = config;
        graphNumberSensorView.setConfig(config);
        nameView.setConfig(config);
        setSensorId(config.id);
        setMin(config.min);
        setMax(config.max);
        setText(config.display);
        setSuffix(config.suffix);
    }

    @Override
    public HashMap<String,NumberData> getData() {
        return data;
    }

    @Override
    public void updateData() {
        setData(getData());
    }

    @Override
    public void setData(HashMap<String,NumberData> data) {
        this.data = data;
        graphNumberSensorView.setData(data);
        setValue(getGraphNumberSensorView().getValue());
    }

    public GraphNumberSensorView getGraphNumberSensorView() {
        return graphNumberSensorView;
    }

    public NameView getNameView() {
        return nameView;
    }

    @Override
    public void setDecimal(int decimal) {
        getGraphNumberSensorView().setDecimal(decimal);
    }
}
