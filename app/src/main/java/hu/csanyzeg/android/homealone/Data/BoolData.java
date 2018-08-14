package hu.csanyzeg.android.homealone.Data;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by tanulo on 2018. 07. 29..
 */

public abstract class BoolData extends Data<Boolean> {

    protected Boolean settingsAlarmValue = true;
    protected boolean alarm = false;

/*
    @Override
    protected NamedArrayList<Entry<Boolean>> getDataFromMySQL() {
        throw new UnsupportedOperationException();
    }
*/
    @Override
    protected NamedArrayList<Entry<Boolean>> getDataFromRandom() {
        NamedArrayList<Entry<Boolean>> mentries = new NamedArrayList<>();
        int c = BoolData.this.hashCode() >> 8 + 0xff000000;
        long i = getFromDate().getTime();
        long it = (getToDate().getTime() - getFromDate().getTime()) / valueCount;
        while (i + it <= getToDate().getTime()) {
            i += it;
            mentries.add(new Entry(random.nextBoolean(), i, c));
        }

        Collections.sort(mentries);
        return mentries;
    }

    @Override
    protected void getDataFromSensorRecords(ArrayList<SensorRecord> sensorRecords) {
        if (getFromDate() == null || getToDate() == null){
            for (SensorRecord s : sensorRecords) {
                if (s.field.equals(config.id)) {
                    graphEntries.add(new Entry<Boolean>(s.value != 0, s.ts));
                }
            }
        }else {
            long from = getFromDate().getTime();
            long to = getToDate().getTime();
            for (SensorRecord s : sensorRecords) {
                if (s.field.equals(config.id) && from <= s.ts.getTime() && to >= s.ts.getTime()) {
                    graphEntries.add(new Entry<Boolean>(s.value != 0, s.ts));
                }
            }
        }
        System.out.println(graphEntries.size() + " "  + config.display);
        Collections.sort(graphEntries);
    }

    public BoolData(Config config) {
        super(config);
    }

    synchronized
    public Boolean getSettingsAlarmValue() {
        return settingsAlarmValue;
    }

    synchronized
    public void setSettingsAlarmValue(Boolean settingsAlarmValue) {
        this.settingsAlarmValue = settingsAlarmValue;
    }
/*
    @Override
    protected NamedArrayList<Entry<Boolean>> getDataFromXML(String xml) {
        return null;
    }
*/
    @Override
    synchronized
    public boolean isAlarm() {
        if (currentValue()!= null) {
            if ((config.getAlarmEvent() == AlarmEvent.always || getAlarmSwitch() != null) && currentValue()) {
                if (getAlarmSwitch() != null) {
                    return getAlarmSwitch().currentValue();
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    synchronized
    public String getAlarmText() {
        return config.alarmText;
    }
}
