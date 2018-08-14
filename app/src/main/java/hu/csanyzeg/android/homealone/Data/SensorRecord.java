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

    public SensorRecord() {
    }

    public SensorRecord(Integer id, String field, Double value, Date ts) {
        this.id = id;
        this.field = field;
        this.value = value;
        this.ts = ts;
    }

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
