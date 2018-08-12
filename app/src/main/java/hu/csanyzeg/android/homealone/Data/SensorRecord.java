package hu.csanyzeg.android.homealone.Data;

import java.util.Date;

/**
 * Created by tanulo on 2018. 08. 12..
 */

public class SensorRecord {
    public Integer id;
    public String field;
    public Double value;
    public Date ts;

    @Override
    public String toString() {
        return "SensorRecord{" +
                "id=" + id +
                ", field='" + field + '\'' +
                ", value=" + value +
                ", ts=" + ts +
                '}';
    }
}
