package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import hu.csanyzeg.android.homealone.Data.BoolData;
import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Interfaces.BoolSensor;
import hu.csanyzeg.android.homealone.Interfaces.InitableUI;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by tanulo on 2018. 07. 29..
 */

public class FullBoolSensorView  extends LinearLayout implements BoolSensor, InitableUI {

    protected GraphBoolSensorView graphBoolSensorView;
    protected NameView nameView;
    protected HashMap<String, BoolData> data = new HashMap<>();
    protected Config config = null;


    public FullBoolSensorView(Context context) {
        super(context);
        inflate();
        init();
    }

    public FullBoolSensorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate();
        init();
    }

    public FullBoolSensorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate();
        init();
    }

    @Override
    public void inflate(){
        LayoutInflater.from(getContext()).inflate(hu.csanyzeg.android.homealone.R.layout.full_bool_sensor_view, this);
    }


    @Override
    public void inflateparams(Context context, @Nullable AttributeSet attrs) {

    }

    @Override
    public void init() {
        graphBoolSensorView = getRootView().findViewById(hu.csanyzeg.android.homealone.R.id.fullSensorGraphView);
        nameView = getRootView().findViewById(hu.csanyzeg.android.homealone.R.id.fullSensorNameView);
    }

    @Override
    public Boolean getValue() {
        return graphBoolSensorView.getValue();
    }

    @Override
    public String getSensorId() {
        return graphBoolSensorView.getSensorId();
    }

    @Override
    public void setValue(Boolean value) {
        graphBoolSensorView.setValue(value);
    }

    @Override
    public void setSensorId(String sensorId) {
        graphBoolSensorView.setSensorId(sensorId);
    }

    @Override
    public void setValue(Boolean value, Date lastUpdateDate) {
        graphBoolSensorView.setValue(value);
    }

    @Override
    public void setText(String text) {
        nameView.setText(text);
    }

    @Override
    public void setLastUpdateDate(Date lastUpdateDate) {
        graphBoolSensorView.setLastUpdateDate(lastUpdateDate);
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public void setConfig(Config config) {
        this.config = config;
        graphBoolSensorView.setConfig(config);
        nameView.setConfig(config);
        setSensorId(config.id);
        setText(config.display);
    }

    @Override
    public HashMap<String, BoolData> getData() {
        return data;
    }

    @Override
    public void updateData() {
        setData(getData());
    }

    @Override
    public void setData(HashMap<String, BoolData> data) {
        this.data = data;
        graphBoolSensorView.setData(data);
        setValue(graphBoolSensorView.getValue());
    }


    public GraphBoolSensorView getGraphBoolSensorView() {
        return graphBoolSensorView;
    }

    public NameView getNameView() {
        return nameView;
    }
}
