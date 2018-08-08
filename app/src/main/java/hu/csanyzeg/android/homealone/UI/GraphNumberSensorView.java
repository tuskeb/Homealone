package hu.csanyzeg.android.homealone.UI;

import android.content.Context;

import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;


import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.NumberData;
import hu.csanyzeg.android.homealone.Interfaces.InitableUI;
import hu.csanyzeg.android.homealone.Interfaces.NumberSensor;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class GraphNumberSensorView extends RelativeLayout implements InitableUI, NumberSensor {
    private NumberView valueView;
    private NumberGraphView graphView;
    private PercentView percentView;
    private TextView lastUpdateDate;
    private String sensorId;
    private static Random random = new Random();

    protected HashMap<String, NumberData> data = null;
    protected Config config = null;


    @Override
    public Double getValue() {
        return percentView.getValue();
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public GraphNumberSensorView(Context context) {
        super(context);
        inflate();
        init();
    }

    public GraphNumberSensorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate();
        init();
    }

    public GraphNumberSensorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate();
        init();
    }

    @Override
    public void inflate() {
        LayoutInflater.from(getContext()).inflate(hu.csanyzeg.android.homealone.R.layout.graph_number_sensor_view, this);
    }

    @Override
    public void inflateparams(Context context, @Nullable AttributeSet attrs) {

    }

    public void init(){

        valueView = getRootView().findViewById(hu.csanyzeg.android.homealone.R.id.value);
        graphView = getRootView().findViewById(hu.csanyzeg.android.homealone.R.id.graph);
        percentView = getRootView().findViewById(hu.csanyzeg.android.homealone.R.id.percent);
        //textView  = getRootView().findViewById(R.id.txt);
        lastUpdateDate = getRootView().findViewById(hu.csanyzeg.android.homealone.R.id.lastupdatedate);
        //mainLayout = getRootView().findViewById(R.id.graphNumberSensorMainLayout);
        setValue(0.0);
    }

    public void setValue(Double value){
        valueView.setValue(value);
        percentView.setValue(value);
    }

    public void setSuffix(String unit){
        valueView.setSuffix(unit);
        graphView.setUnit(unit);
    }

    public void setMin(Double min){
        percentView.setMin(min);
        graphView.setMin(min);
    }

    public void setMax(Double max){
        percentView.setMax(max);
        graphView.setMax(max);
    }

    @Override
    public Double getMin() {
        return percentView.getMin();
    }

    @Override
    public Double getMax() {
        return percentView.getMax();
    }

    public void setLastUpdateDate(Date lastUpdateDate){
        if (lastUpdateDate == null){
            this.lastUpdateDate.setText("-");
            return;
        }
        this.lastUpdateDate.setText((new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")).format(lastUpdateDate));
    }

    public void setValue(Double value, Date lastUpdateDate) {
        setValue(value);
        setLastUpdateDate(lastUpdateDate);
    }


    public void setValue(Double value, long lastUpdateDate) {
        setValue(value);
        Date date = new Date();
        date.setTime(lastUpdateDate);
        setLastUpdateDate(date);
    }



    public NumberView getValueView() {
        return valueView;
    }

    public NumberGraphView getGraphView() {
        return graphView;
    }

    public PercentView getPercentView() {
        return percentView;
    }

    @Override
    public void setText(String text) {

    }

    @Override
    public HashMap<String, NumberData> getData() {
        return data;
    }

    @Override
    public void setData(HashMap<String, NumberData> data) {
        this.data = data;

        if (this.data.get(config.getGroupValue())!=null){
            setValue(this.data.get(config.getGroupValue()).currentValue());
            setLastUpdateDate(this.data.get(config.getGroupValue()).currentDate());
        }else{
            switch (config.getGroupValue()){
                case "min":
                    NumberData min = Collections.min(data.values(), new Comparator<NumberData>() {
                        @Override
                        public int compare(NumberData numberData, NumberData t1) {
                            if (numberData.currentValue()==null || t1.currentValue()==null) return 0;
                            return numberData.currentValue().compareTo(t1.currentValue());
                        }
                    });

                    setValue(min.currentValue());
                    setLastUpdateDate(min.currentDate());
                    break;
                case "max":
                    NumberData max = Collections.max(data.values(), new Comparator<NumberData>() {
                        @Override
                        public int compare(NumberData numberData, NumberData t1) {
                            if (numberData.currentValue()==null || t1.currentValue()==null) return 0;
                            return numberData.currentValue().compareTo(t1.currentValue());
                        }
                    });

                    setValue(max.currentValue());
                    setLastUpdateDate(max.currentDate());
                    break;
                case "avg":
                    NumberData max2 = Collections.min(data.values(), new Comparator<NumberData>() {
                        @Override
                        public int compare(NumberData numberData, NumberData t1) {
                            if (numberData.currentValue()==null || t1.currentValue()==null) return 0;
                            return numberData.currentDate().compareTo(t1.currentDate());
                        }
                    });
                    double avg1 = 0;
                    for (NumberData numberData: data.values()) {
                        avg1+=numberData.currentValue();
                    }
                    avg1 /=data.size();
                    setValue(avg1);
                    setLastUpdateDate(max2.currentDate());
                    break;

            }
        }


        graphView.getEntryList().clear();
        for (NumberData numberData: data.values()) {
            graphView.getEntryList().add(numberData.getGraphEntries());
        }
        graphView.invalidate();
        //System.out.println("update");
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
        setMin(config.min);
        setMax(config.max);
        setText(config.display);
        setSuffix(config.suffix);
        setDecimal(config.precision);
    }

    @Override
    public void setDecimal(int decimal) {
        valueView.setDecimal(config.precision);
        graphView.setDecimal(config.precision);
    }
}
