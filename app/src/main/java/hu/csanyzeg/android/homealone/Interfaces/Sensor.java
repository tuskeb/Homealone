package hu.csanyzeg.android.homealone.Interfaces;

import hu.csanyzeg.android.homealone.Data.Config;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by tanulo on 2018. 06. 28..
 */

public interface Sensor<DataType> {
    public String getSensorId();

    public void setSensorId(String sensorId);

    public Config getConfig();

    public void setConfig(Config config);

    public HashMap<String, DataType> getData();

    public void addData(String id, DataType d);

    public void updateData();
}
