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
public class AlarmMinMaxSensorNumberSensorView extends AlarmNumberSensorView {
    protected EditValueView maxEditValueView;
    protected EditValueView minEditValueView;
    private OnValueChangedListener onMaxValueChangedListener = null;
    private OnValueChangedListener onMinValueChangedListener = null;
    protected HorizontalGraphLine minHorizontalGraphLine;
    protected HorizontalGraphLine maxHorizontalGraphLine;

    public AlarmMinMaxSensorNumberSensorView(Context context) {
        super(context);
    }

    public AlarmMinMaxSensorNumberSensorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlarmMinMaxSensorNumberSensorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void inflate() {
        LayoutInflater.from(getContext()).inflate(hu.csanyzeg.android.homealone.R.layout.alarm_min_max_number_sensor_view, this);
    }


    @Override
    public void init() {
        super.init();
        maxEditValueView = getRootView().findViewById(hu.csanyzeg.android.homealone.R.id.alarmMaxView);
        minEditValueView = getRootView().findViewById(hu.csanyzeg.android.homealone.R.id.alarmMinView);
        maxEditValueView.setOnValueChangedListener(new OnValueChangedListener() {
            @Override
            public void doubleValueChanged(Double value) {
                if (maxEditValueView.getValue()<minEditValueView.getValue()){
                    minEditValueView.setValue(maxEditValueView.getValue());
                }
                onMaxValueChanged(value);
            }
        });

        minEditValueView.setOnValueChangedListener(new OnValueChangedListener() {
            @Override
            public void doubleValueChanged(Double value) {
                if (minEditValueView.getValue()>maxEditValueView.getValue()){
                    maxEditValueView.setValue(minEditValueView.getValue());
                }
                onMinValueChanged(value);
            }
        });
        minHorizontalGraphLine = new HorizontalGraphLine(0.0, 0xff0000ff, "Minimum");
        maxHorizontalGraphLine = new HorizontalGraphLine(0.0,0xffff0000,"Maximum");
        graphView.getHorizontalGraphLineList().add(minHorizontalGraphLine);
        graphView.getHorizontalGraphLineList().add(maxHorizontalGraphLine);
    }



    @Override
    public void setSuffix(String unit) {
        super.setSuffix(unit);
        maxEditValueView.setSuffix(unit);
        minEditValueView.setSuffix(unit);
    }


    @Override
    public void setMax(Double value) {
        super.setMax(value);
        maxEditValueView.setMax(value);
        minEditValueView.setMax(value);
    }

    @Override
    public void setMin(Double value) {
        super.setMin(value);
        minEditValueView.setMin(value);
        maxEditValueView.setMin(value);
    }

    protected void onMinValueChanged(Double value){
        if (onMinValueChangedListener != null){
            onMinValueChangedListener.doubleValueChanged(value);
        }
        minHorizontalGraphLine.value = minEditValueView.getValue();
        maxHorizontalGraphLine.value = maxEditValueView.getValue();
        graphView.invalidate();
        //System.out.println("Minch");
    }

    protected void onMaxValueChanged(Double value){
        if (onMaxValueChangedListener != null){
            onMaxValueChangedListener.doubleValueChanged(value);
        }
        minHorizontalGraphLine.value = minEditValueView.getValue();
        maxHorizontalGraphLine.value = maxEditValueView.getValue();
        graphView.invalidate();
        //System.out.println("Maxch");
    }

    public void setMaxValueChangeListener(OnValueChangedListener maxValueChangeListener){
        onMaxValueChangedListener = maxValueChangeListener;
    }

    public void setMinValueChangeListener(OnValueChangedListener minValueChangeListener){
        onMinValueChangedListener = minValueChangeListener;
    }

    public EditValueView getMaxEditValueView() {
        return maxEditValueView;
    }

    public EditValueView getMinEditValueView() {
        return minEditValueView;
    }

    public void setAlarmMinValue(Double value){
        minEditValueView.setValue(value);
        minHorizontalGraphLine.value = minEditValueView.getValue();
        graphView.invalidate();
    }

    public void setAlarmMaxValue(Double value){
        maxEditValueView.setValue(value);
        maxHorizontalGraphLine.value = maxEditValueView.getValue();
        graphView.invalidate();
    }

    @Override
    public void setConfig(Config config) {
        super.setConfig(config);

    }


    @Override
    public void updateData() {
        super.updateData();
        OnValueChangedListener onValueChangedListenerMin = minEditValueView.getOnValueChangedListener();
        OnValueChangedListener onValueChangedListenerMax = maxEditValueView.getOnValueChangedListener();
        minEditValueView.setOnValueChangedListener(null);
        maxEditValueView.setOnValueChangedListener(null);
/*
        setAlarmMaxValue(this.data.getSettingsAlarmMaxValue());
        setAlarmMinValue(this.data.getSettingsAlarmMinValue());
        setAlarmMaxValue(this.data.getSettingsAlarmMaxValue());
*/
        setAlarmMaxValue(this.config.alarmMaxValue);
        setAlarmMinValue(this.config.alarmMinValue);
        setAlarmMaxValue(this.config.alarmMaxValue);

        maxHorizontalGraphLine.value = maxEditValueView.getValue();
        minHorizontalGraphLine.value = minEditValueView.getValue();

        maxEditValueView.setOnValueChangedListener(onValueChangedListenerMax);
        minEditValueView.setOnValueChangedListener(onValueChangedListenerMin);
    }
}
