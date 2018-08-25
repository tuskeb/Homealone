package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.HashMap;

import hu.csanyzeg.android.homealone.Data.AlarmEvent;
import hu.csanyzeg.android.homealone.Data.AlarmType;
import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.NumberData;
import hu.csanyzeg.android.homealone.Interfaces.Alarm;
import hu.csanyzeg.android.homealone.Interfaces.Sensor;
import hu.csanyzeg.android.homealone.R;

/**
 * Created by tanulo on 2018. 07. 03..
 */

public class AlarmNumberSensorView extends GraphNumberSensorView implements Alarm {


    private AlarmType alarmType = AlarmType.none;
    private AlarmEvent alarmEvent = AlarmEvent.never;
    private boolean alarm = false;

    private TextView alarmTextAlarm;
    private Sensor alarmSwitchSensor= null;

    public AlarmNumberSensorView(Context context) {
        super(context);
    }

    public AlarmNumberSensorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlarmNumberSensorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init() {
        super.init();
        alarmTextAlarm = getRootView().findViewById(R.id.alarmTextAlarm);
    }

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

        for(NumberData numberData : data.values()){
            numberData.getConfig().alarmMinValue = config.alarmMinValue;
            numberData.getConfig().alarmMaxValue = config.alarmMaxValue;
        }

        for(NumberData numberData : data.values()){
            if (numberData.isAlarm()){
                al = true;
            }
        }
        setAlarm(al);

    }

}
