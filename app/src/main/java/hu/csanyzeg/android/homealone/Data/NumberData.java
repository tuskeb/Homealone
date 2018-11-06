package hu.csanyzeg.android.homealone.Data;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by tanulo on 2018. 07. 05..
 */

abstract public class NumberData extends Data<Double> {


    //protected Double settingsAlarmMinValue = Double.MIN_VALUE;
    //protected Double settingsAlarmMaxValue = Double.MAX_VALUE;
    protected boolean alarm = false;

    public NumberData(Config config) {
        super(config);
        //settingsAlarmMinValue = config.alarmMinValue;
        //settingsAlarmMaxValue = config.alarmMaxValue;
    }

    @Override
    synchronized
    protected void getDataFromSensorRecords(ArrayList<SensorRecord> sensorRecords) {
        for (SensorRecord s : sensorRecords) {
            if (s.field.equals(config.id)) {
                Entry<Double> e;
                graphEntries.add(e = new Entry<Double>(s.value, s.ts));
            }
        }
        /*
        if (getFromDate() == null || getToDate() == null){
            for (SensorRecord s : sensorRecords) {
                if (s.field.equals(config.id)) {
                    Entry<Double> e;
                    graphEntries.add(e = new Entry<Double>(s.value, s.ts));

                }
            }
        }else {
            long from = getFromDate().getTime();
            long to = getToDate().getTime();
            for (SensorRecord s : sensorRecords) {
                if (s.field.equals(config.id) && from <= s.ts.getTime() && to >= s.ts.getTime()) {
                    Entry<Double> e;
                    graphEntries.add(e = new Entry<Double>(s.value, s.ts));

                }
            }
        }
        */
        //System.out.println(graphEntries.size() + " "  + config.display);
        Collections.sort(graphEntries);
    }


    /*
    @Override
    protected NamedArrayList<Entry<Double>> getDataFromXML(String xml) {
        return null;
    }
    */

    @Override
    synchronized
    public String toString() {
        return "NumberData{" +
                "currentValue=" + currentValue() +
                ", lastUpdateDate=" + lastUpdateDate +
                ", graphEntries=" + graphEntries +
                ", refreshIntervalMs=" + refreshIntervalMs +
                '}';
    }

    @Override
    synchronized
    public boolean isAlarm() {
        if (currentValue()!= null) {
            if ((config.getAlarmEvent() == AlarmEvent.always || getAlarmSwitch() != null) && ((config.alarmMaxValue != null && currentValue() > config.alarmMaxValue) || (config.alarmMinValue != null && currentValue() < config.min))) {
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
        return config.alarmText.replace("%current", String.format("%." + config.precision + "f", currentValue()))
                .replace("%alarmmin", String.format("%." + config.precision + "f",config.alarmMinValue))
                .replace("%alarmmax", String.format("%." + config.precision + "f",config.alarmMaxValue))
                .replace("%minvalue", String.format("%." + config.precision + "f",config.alarmMinValue))
                .replace("%maxvalue", String.format("%." + config.precision + "f",config.alarmMaxValue));
    }

    @Override
    public synchronized Double currentValue() {
        return super.currentValue();
    }
}
