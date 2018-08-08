package hu.csanyzeg.android.homealone.Interfaces;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Data.NumberData;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by tanulo on 2018. 06. 28..
 */

public interface Sensor<T> {
    public String getSensorId();

    public void setSensorId(String sensorId);

    public void setText(String text);

    public void setLastUpdateDate(Date lastUpdateDate);

    public Config getConfig();

    public void setConfig(Config config);

    public HashMap<String, T> getData();

    public void setData(HashMap<String, T> data);

    public void updateData();
}
