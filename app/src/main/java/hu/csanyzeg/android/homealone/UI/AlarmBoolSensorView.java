package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import hu.csanyzeg.android.homealone.Data.AlarmEvent;
import hu.csanyzeg.android.homealone.Data.AlarmType;
import hu.csanyzeg.android.homealone.Data.BoolData;
import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Interfaces.Alarm;
import hu.csanyzeg.android.homealone.Interfaces.Sensor;
import hu.csanyzeg.android.homealone.R;

/**
 * Created by tanulo on 2018. 07. 30..
 */

public class AlarmBoolSensorView extends GraphBoolSensorView implements Alarm {


    private AlarmType alarmType = AlarmType.none;
    private AlarmEvent alarmEvent = AlarmEvent.never;
    private boolean alarm = false;

    private TextView alarmTextAlarm;
    private Sensor alarmSwitchSensor= null;
/*
    private RadioButton radioButtonTrue;
    private RadioButton radioButtonFalse;
    private RadioButton radioButtonNull;

    private OnBoolValueChangeListener onBoolValueChangeListener = null;
*/
    public AlarmBoolSensorView(Context context) {
        super(context);
    }

    public AlarmBoolSensorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlarmBoolSensorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void inflate() {
        LayoutInflater.from(getContext()).inflate(R.layout.alarm_bool_sensor_view, this);
    }

    @Override
    public void init() {
        super.init();
        alarmTextAlarm = getRootView().findViewById(R.id.alarmTextAlarm);
        /*
        radioButtonTrue = getRootView().findViewById(R.id.alarmTrue);
        radioButtonFalse = getRootView().findViewById(R.id.alarmFalse);
        radioButtonNull = getRootView().findViewById(R.id.alarmNull);
        radioButtonNull.setChecked(true);

        radioButtonNull.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onBoolValueChangeListener!= null) {
                    onBoolValueChangeListener.onChangeValueNull();
                }
            }
        });

        radioButtonTrue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onBoolValueChangeListener!= null) {
                    onBoolValueChangeListener.onChangeValueTrue();
                }
            }
        });

        radioButtonFalse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onBoolValueChangeListener!= null) {
                    onBoolValueChangeListener.onChangeValueFalse();
                }
            }
        });*/
    }

    /*
    public OnBoolValueChangeListener getOnCheckChangeListener() {
        return onBoolValueChangeListener;
    }

    public void setOnCheckChangeListener(OnBoolValueChangeListener onBoolValueChangeListener) {
        this.onBoolValueChangeListener = onBoolValueChangeListener;
    }
*/
    @Override
    public AlarmType getAlarmType() {
        return alarmType;
    }

    @Override
    public void setAlarmType(AlarmType alarmType) {
        this.alarmType = alarmType;
    }

    @Override
    public AlarmEvent getAlarmEvent() {
        return alarmEvent;
    }

    @Override
    public void setAlarmEvent(AlarmEvent alarmEvent) {
        this.alarmEvent = alarmEvent;
        switch (alarmEvent){
            case never: alarmTextAlarm.setText("Nincs riasztás az érték alapján."); break;
            case always: alarmTextAlarm.setText("Riasztás mindenképp."); break;
            case ifSwitchOn: alarmTextAlarm.setText("Riasztás, ha a riasztó aktív."); break;
        }
    }

    @Override
    public Sensor getAlarmSwitchSensor() {
        return alarmSwitchSensor;
    }

    @Override
    public void setAlarmSwitchSensor(Sensor sensor) {
        this.alarmSwitchSensor = sensor;
    }

    @Override
    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
        valueView.setAlarm(alarm);
    }

    @Override
    public boolean getAlarm() {
        return alarm;
    }

    @Override
    public void setConfig(Config config) {
        super.setConfig(config);
        setAlarmType(config.getAlarmType());
        setAlarmEvent(config.getAlarmEvent());
    }

    @Override
    public void updateData() {
        super.updateData();
        boolean al = false;
        for(BoolData boolData : data.values()){
            if (boolData.isAlarm()){
                al = true;
            }
        }
        setAlarm(al);
    }
}
