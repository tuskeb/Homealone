package hu.csanyzeg.android.homealone.UI;

import android.content.Context;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.NumberData;
import hu.csanyzeg.android.homealone.DatabaseService;
import hu.csanyzeg.android.homealone.GraphActivity;
import hu.csanyzeg.android.homealone.Interfaces.InitableUI;
import hu.csanyzeg.android.homealone.Interfaces.NumberSensor;
import hu.csanyzeg.android.homealone.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class GraphNumberSensorView extends RelativeLayout implements InitableUI, NumberSensor {

    protected NumberView valueView;
    protected NumberGraphView graphView;
    protected PercentView percentView;
    protected TextView displayView;
    protected ImageView iconView;


    protected String sensorId;
    protected static Random random = new Random();

    protected final HashMap<String, NumberData> data = new HashMap<>();
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

        valueView = getRootView().findViewById(R.id.value);
        graphView = getRootView().findViewById(R.id.graph);
        percentView = getRootView().findViewById(R.id.percent);
        displayView  = getRootView().findViewById(R.id.txt);
        iconView  = getRootView().findViewById(R.id.icon_view);

        graphView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), GraphActivity.class);
                intent.putExtra(DatabaseService.BR_DATA_ID, config.id);
                getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void setValue(Double value){
        valueView.setValue(value);
        percentView.setValue(value);
    }

    @Override
    public void setSuffix(String unit){
        valueView.setSuffix(unit);
        graphView.setUnit(unit);
    }

    @Override
    public void setMin(Double min){
        percentView.setMin(min);
        graphView.setMin(min);
    }

    @Override
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

    @Override
    public HashMap<String, NumberData> getData() {
        return data;
    }

    @Override
    public void addData(String id, NumberData d) {
        data.put(id,d);
        updateData();
    }

    @Override
    public void updateData() {

        if (this.data.get(config.getGroupValue())!=null){
            setValue(this.data.get(config.getGroupValue()).currentValue());
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
                    break;

            }
        }


        graphView.getEntryList().clear();
        for (NumberData numberData: data.values()) {
            graphView.getEntryList().add(numberData.getGraphEntries());
        }
        if (graphView.getEntryList().size()>0) {
            try {
                graphView.setTimeMin(Collections.min(data.values(), new Comparator<NumberData>() {
                    @Override
                    public int compare(NumberData numberData, NumberData t1) {
                        return numberData.getFromDate().compareTo(t1.getFromDate());
                    }
                }).getFromDate().getTime());

                graphView.setTimeMax(Collections.max(data.values(), new Comparator<NumberData>() {
                    @Override
                    public int compare(NumberData numberData, NumberData t1) {
                        return numberData.getToDate().compareTo(t1.getToDate());
                    }
                }).getToDate().getTime());
            }
            catch (NullPointerException e){

            }
        }
        graphView.invalidate();
        //System.out.println("update");
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
        displayView.setText(config.display);
        setSuffix(config.suffix);
        setDecimal(config.precision);
    }

    @Override
    public void setDecimal(int decimal) {
        valueView.setDecimal(config.precision);
        graphView.setDecimal(config.precision);
        percentView.setDecimal(config.precision);
    }
}
