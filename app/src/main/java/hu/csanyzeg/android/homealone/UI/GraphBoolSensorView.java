package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hu.csanyzeg.android.homealone.Data.BoolData;
import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Interfaces.BoolSensor;
import hu.csanyzeg.android.homealone.Interfaces.InitableUI;
import hu.csanyzeg.android.homealone.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by tanulo on 2018. 07. 29..
 */

public class GraphBoolSensorView extends RelativeLayout implements InitableUI, BoolSensor {
    private BoolView valueView;
    private BoolGraphView graphView;
    private TextView lastUpdateDate;
    private BoolIndicatorView boolIndicatorView;
    private String sensorId;

    protected HashMap<String, BoolData> data = null;
    protected Config config = null;

    public GraphBoolSensorView(Context context) {
        super(context);
        inflate();
        init();
    }

    public GraphBoolSensorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate();
        init();
    }

    public GraphBoolSensorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate();
        init();
    }

    @Override
    public Boolean getValue() {
        return valueView.getValue();
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    @Override
    public void inflate() {
        LayoutInflater.from(getContext()).inflate(hu.csanyzeg.android.homealone.R.layout.graph_bool_sensor_view, this);
    }

    @Override
    public void inflateparams(Context context, @Nullable AttributeSet attrs) {

    }

    public void init(){
        valueView = getRootView().findViewById(R.id.value);
        graphView = getRootView().findViewById(R.id.graph);
        lastUpdateDate = getRootView().findViewById(R.id.lastupdatedate);
        boolIndicatorView = getRootView().findViewById(R.id.boolindicator);
    }

    public void setValue(Boolean value){
        valueView.setValue(value);
        boolIndicatorView.setValue(value);
    }

    public void setLastUpdateDate(Date lastUpdateDate){
        if (lastUpdateDate == null){
            this.lastUpdateDate.setText("-");
            return;
        }
        this.lastUpdateDate.setText((new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")).format(lastUpdateDate));
    }

    public void setValue(Boolean value, Date lastUpdateDate) {
        setValue(value);
        setLastUpdateDate(lastUpdateDate);
    }


    public void setValue(Boolean value, long lastUpdateDate) {
        setValue(value);
        Date date = new Date();
        date.setTime(lastUpdateDate);
        setLastUpdateDate(date);
    }

    public BoolView getValueView() {
        return valueView;
    }

    public BoolGraphView getGraphView() {
        return graphView;
    }

    @Override
    public void setText(String text) {

    }

    @Override
    public HashMap<String, BoolData> getData() {
        return data;
    }

    @Override
    public void setData(HashMap<String, BoolData> data) {
        this.data = data;
        BoolData boolData = this.data.get(config.id);
        if (boolData != null) {
            setValue(boolData.currentValue());
            setLastUpdateDate(boolData.currentDate());
        }else{

            boolean b = false;
            for (BoolData boolData1: data.values()) {
                if (boolData1.currentValue()){
                    b=true;
                }
            }
            setValue(b);
        }
        //graphView.refreshSizeValues(graphView.canvas);
        graphView.getEntryList().clear();
        for (BoolData boolData1: data.values()) {
            graphView.getEntryList().add(boolData1.getGraphEntries());
        }
        graphView.invalidate();

    }

    @Override
    public void updateData() {
        setData(getData());
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public void setConfig(Config config) {
        this.config = config;
        setSensorId(config.id);
        setText(config.display);
    }

}
