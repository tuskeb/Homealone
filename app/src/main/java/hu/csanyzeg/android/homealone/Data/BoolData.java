package hu.csanyzeg.android.homealone.Data;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by tanulo on 2018. 07. 29..
 */

public abstract class BoolData extends Data<Boolean> {

    protected Boolean settingsAlarmValue = true;
    protected boolean alarm = false;


    @Override
    protected NamedArrayList<Entry<Boolean>> getDataFromMySQL() {
        throw new UnsupportedOperationException();
    }

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
