package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import java.util.HashMap;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Data.HorizontalGraphLine;
import hu.csanyzeg.android.homealone.Data.NumberData;

/**
 * TODO: document your custom view class.
 */
public class AlarmMinFullSensorFullNumberSensorView extends AlarmFullNumberSensorView {
    protected EditValueView minEditValueView;
    protected HorizontalGraphLine minHorizontalGraphLine;

    public AlarmMinFullSensorFullNumberSensorView(Context context) {
        super(context);
    }

    public AlarmMinFullSensorFullNumberSensorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlarmMinFullSensorFullNumberSensorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void inflate() {
        LayoutInflater.from(getContext()).inflate(hu.csanyzeg.android.homealone.R.layout.alarm_min_full_number_sensor_view, this);
    }


    @Override
    public void init() {
        super.init();
        minEditValueView = getRootView().findViewById(hu.csanyzeg.android.homealone.R.id.alarmMinView);
        minHorizontalGraphLine = new HorizontalGraphLine(0.0, 0xff0000ff,"Minimum");

        minEditValueView.setOnValueChangedListener(new OnValueChangedListener() {
            @Override
            public void doubleValueChanged(Double value) {
                onMinValueChanged(value);
            }
        });

        graphNumberSensorView.getGraphView().getHorizontalGraphLineList().add(minHorizontalGraphLine);
    }

    @Override
    public void setSuffix(String unit) {
        super.setSuffix(unit);
        minEditValueView.setSuffix(unit);
    }


    @Override
    public void setMax(Double value) {
        super.setMax(value);
        minEditValueView.setMax(value);
    }

    @Override
    public void setMin(Double value) {
        super.setMin(value);
        minEditValueView.setMin(value);
    }

    public void setMinValueChangeListener(OnValueChangedListener minValueChangeListener){
        minEditValueView.setOnValueChangedListener(minValueChangeListener);
    }

    public EditValueView getMinEditValueView() {
        return minEditValueView;
    }


    protected void onMinValueChanged(Double value){
        minHorizontalGraphLine.value = minEditValueView.getValue();
        graphNumberSensorView.getGraphView().invalidate();
    }



    public void setAlarmMinValue(Double value){
        minEditValueView.setValue(value);
    }


    @Override
    public void setConfig(Config config) {
        super.setConfig(config);


    }

    @Override
    public void setData(HashMap<String, NumberData> data) {
        super.setData(data);

        OnValueChangedListener onValueChangedListenerMin = minEditValueView.getOnValueChangedListener();
        minEditValueView.setOnValueChangedListener(null);

        setAlarmMinValue(this.config.alarmMinValue);

        minHorizontalGraphLine.value = minEditValueView.getValue();

        minEditValueView.setOnValueChangedListener(onValueChangedListenerMin);
    }
}
