package hu.csanyzeg.android.homealone.Interfaces;

import hu.csanyzeg.android.homealone.Data.AlarmEvent;
import hu.csanyzeg.android.homealone.Data.AlarmType;

/**
 * Created by tanulo on 2018. 07. 05..
 */

public interface Alarm {

    public AlarmType getAlarmType();
    public void setAlarmType(AlarmType alarmType);

    public AlarmEvent getAlarmEvent();
    public void setAlarmEvent(AlarmEvent alarmEvent);

    public Sensor getAlarmSwitchSensor();
    public void setAlarmSwitchSensor(Sensor sensor);

    public void setAlarm(boolean alarm);
    public boolean getAlarm();
}
