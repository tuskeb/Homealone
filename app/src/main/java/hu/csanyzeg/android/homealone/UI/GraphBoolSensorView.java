package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hu.csanyzeg.android.homealone.Data.BoolData;
import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Interfaces.BoolSensor;
import hu.csanyzeg.android.homealone.Interfaces.InitableUI;
import hu.csanyzeg.android.homealone.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by tanulo on 2018. 07. 29..
 */

public class GraphBoolSensorView extends RelativeLayout implements InitableUI, BoolSensor {
    protected BoolGraphView graphView;
    protected TextView displayView;
    protected BoolView valueView;
    protected ImageView iconView;
    protected BoolIndicatorView boolIndicatorView;


    protected String sensorId;


    protected final HashMap<String, BoolData> data = new HashMap<>();
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
        for(BoolData d : data.values()){
            return d.currentValue();
        }
        return null;
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
        iconView  = getRootView().findViewById(R.id.icon_view);
        displayView = getRootView().findViewById(R.id.txt);
        graphView = getRootView().findViewById(R.id.graph);
        boolIndicatorView = getRootView().findViewById(R.id.boolindicator);
    }

    public void setValue(Boolean value){
        boolIndicatorView.setValue(value);
    }

    public BoolGraphView getGraphView() {
        return graphView;
    }

    @Override
    public HashMap<String, BoolData> getData() {
        return data;
    }

    @Override
    public void addData(String id, BoolData d) {
        this.data.put(id, d);
        updateData();
    }

    @Override
    public void updateData() {
        BoolData boolData = this.data.get(config.id);
        if (boolData != null) {
            setValue(boolData.currentValue());
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
        if (graphView.getEntryList().size()>0) {
            try {
                graphView.setTimeMin(Collections.min(data.values(), new Comparator<BoolData>() {
                    @Override
                    public int compare(BoolData numberData, BoolData t1) {
                        return numberData.getFromDate().compareTo(t1.getFromDate());
                    }
                }).getFromDate().getTime());

                graphView.setTimeMax(Collections.max(data.values(), new Comparator<BoolData>() {
                    @Override
                    public int compare(BoolData numberData, BoolData t1) {
                        return numberData.getToDate().compareTo(t1.getToDate());
                    }
                }).getToDate().getTime());
            } catch (NullPointerException e) {

            }
        }
        graphView.invalidate();


    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public void setConfig(Config config) {
        this.config = config;
        setSensorId(config.id);
        displayView.setText(config.display);
    }

}
