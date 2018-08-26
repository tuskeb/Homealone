package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.HorizontalGraphLine;

/**
 * TODO: document your custom view class.
 */
public class AlarmMaxSensorNumberSensorView extends AlarmNumberSensorView {
    protected EditValueView maxEditValueView;
    protected HorizontalGraphLine maxHorizontalGraphLine;

    public AlarmMaxSensorNumberSensorView(Context context) {
        super(context);
    }

    public AlarmMaxSensorNumberSensorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlarmMaxSensorNumberSensorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void inflate() {
        LayoutInflater.from(getContext()).inflate(hu.csanyzeg.android.homealone.R.layout.alarm_max_number_sensor_view, this);
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
        graphView.getHorizontalGraphLineList().add(maxHorizontalGraphLine);
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
        graphView.invalidate();
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
        if (config.alarmWrite){
            maxEditValueView.setVisibility(VISIBLE);
        }else{
            maxEditValueView.setVisibility(GONE);
        }
    }


    @Override
    public void updateData() {
        super.updateData();

        OnValueChangedListener onValueChangedListenerMax = maxEditValueView.getOnValueChangedListener();
        maxEditValueView.setOnValueChangedListener(null);

        setAlarmMaxValue(this.config.alarmMaxValue);

        maxHorizontalGraphLine.value = maxEditValueView.getValue();

        maxEditValueView.setOnValueChangedListener(onValueChangedListenerMax);
    }
}
