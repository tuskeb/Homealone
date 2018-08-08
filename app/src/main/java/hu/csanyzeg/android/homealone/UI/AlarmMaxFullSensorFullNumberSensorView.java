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
public class AlarmMaxFullSensorFullNumberSensorView extends AlarmFullNumberSensorView {
    protected EditValueView maxEditValueView;
    protected HorizontalGraphLine maxHorizontalGraphLine;

    public AlarmMaxFullSensorFullNumberSensorView(Context context) {
        super(context);
    }

    public AlarmMaxFullSensorFullNumberSensorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlarmMaxFullSensorFullNumberSensorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void inflate() {
        LayoutInflater.from(getContext()).inflate(hu.csanyzeg.android.homealone.R.layout.alarm_max_full_number_sensor_view, this);
    }


    @Override
    public void init() {
        super.init();
        maxEditValueView = getRootView().findViewById(hu.csanyzeg.android.homealone.R.id.alarmMaxView);
        maxEditValueView.setOnValueChangedListener(new OnValueChangedListener() {
            @Override
            public void doubleValueChanged(Double value) {
                onMaxValueChanged(value);
            }
        });
        maxHorizontalGraphLine = new HorizontalGraphLine(0.0,0xffff0000, "Maximum");
        graphNumberSensorView.getGraphView().getHorizontalGraphLineList().add(maxHorizontalGraphLine);
    }

    @Override
    public void setSuffix(String unit) {
        super.setSuffix(unit);
        maxEditValueView.setSuffix(unit);
    }


    @Override
    public void setMax(Double value) {
        super.setMax(value);
        maxEditValueView.setMax(value);
    }

    @Override
    public void setMin(Double value) {
        super.setMin(value);
        maxEditValueView.setMin(value);
    }

    public void setMaxValueChangeListener(OnValueChangedListener maxValueChangeListener){
        maxEditValueView.setOnValueChangedListener(maxValueChangeListener);
    }

    protected void onMaxValueChanged(Double value){
        maxHorizontalGraphLine.value = maxEditValueView.getValue();
        graphNumberSensorView.getGraphView().invalidate();
    }


    public EditValueView getMaxEditValueView() {
        return maxEditValueView;
    }


    public void setAlarmMaxValue(Double value){
        maxEditValueView.setValue(value);
    }


    @Override
    public void setConfig(Config config) {
        super.setConfig(config);
    }


    @Override
    public void setData(HashMap<String, NumberData> data) {
        super.setData(data);

        OnValueChangedListener onValueChangedListenerMax = maxEditValueView.getOnValueChangedListener();
        maxEditValueView.setOnValueChangedListener(null);

        setAlarmMaxValue(this.config.alarmMaxValue);

        maxHorizontalGraphLine.value = maxEditValueView.getValue();

        maxEditValueView.setOnValueChangedListener(onValueChangedListenerMax);
    }
}
