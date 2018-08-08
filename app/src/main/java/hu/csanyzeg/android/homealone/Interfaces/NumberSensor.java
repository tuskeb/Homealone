package hu.csanyzeg.android.homealone.Interfaces;

import java.util.Date;

import hu.csanyzeg.android.homealone.Data.NumberData;

/**
 * Created by tanulo on 2018. 06. 27..
 */

public interface NumberSensor extends Sensor<NumberData>, MinMax, NumberValue{

    public void setValue(Double value, Date lastUpdateDate);

    //public NumberData getData();

    //public void setData(NumberData data);
}
