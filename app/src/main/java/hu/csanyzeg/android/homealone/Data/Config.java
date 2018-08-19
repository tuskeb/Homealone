package hu.csanyzeg.android.homealone.Data;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by tanulo on 2018. 06. 27..
 */

public class Config implements Serializable {

    public static String name = "Config";
    public static String version = null;
    public static Integer polling = 10;
    public static Integer fastPollingCount = 8;
    public static Double fastPollingInterval = 0.5;
    public static Double serviceThreadSleepInterval = 0.4;
    public static Integer dataStoreInterval = 1200; //Mennyi adatot tároljon a grafikonokhoz. Másodpercben megadva.
    public static String map = null;
    public static Double gpsLatitude = null;
    public static Double gpsLongitude = null;
    public static String gpscomment = null;


    public Integer color = null;
    public String id = null;//
    public String device = "";//
    public String display = "";//
    public boolean write = false;//
    public String label = "";//
    public boolean enabled = false;//
    public double default_value = 0;//
    public String suffix = "";//
    public Double min = null;//
    public Double max = null;//
    public String icon = "";//poz
    public Integer posX = null;//poz
    public Integer posY = null;//poz
    public String groupValue = null; //min, max, avg, sensorID
    public boolean sensor = true;
    public Integer monostab = 0; //
    public boolean alarmSet = false;//alarm
    public Double alarmMinValue = null;//alarm
    public Double alarmMaxValue = null;//alarm
    public String alarmSwitch = null;//alarm
    public String alarmText = "";//alarm
    public boolean alarmWrite = true;
    public String alarmComment = null;//alarm
    public String alarmComment2 = null;//alarm
    public Integer precision = 0;
    public String parent = null;
    public Integer pozx = 0; //Ikon pozíció
    public Integer pozy = 0; //Ikon pozíció
    public String pozikon = null; //Ikon
    public String pozcomment = null; //Ikon
    public String comment = null;
    public Integer sensibilitypercent = 1;
    public String sensibilitycomment = null;
    public Double distance = null;


    public boolean isSwitch() {
        return min == null || max == null;
    }

    public boolean isBistabil(){
        return monostab !=0;
    }

    public boolean isWrite(){
        return write;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public static long getDataStoreIntervalMs() {
        return (long)dataStoreInterval * 1000l;
    }

    public static Integer getFastPollingCount() {
        return fastPollingCount;
    }

    public static long getServiceThreadSleepIntervalMs() {
        return (long)(serviceThreadSleepInterval * 1000L);
    }

    public static long getFastPollingIntervalMs() {
        return (long)(fastPollingInterval * 1000L);
    }

    public AlarmEvent getAlarmEvent(){
        if (alarmMaxValue !=null || alarmMinValue != null){
            if (alarmSwitch !=null) {
                return AlarmEvent.ifSwitchOn;
            }
            return AlarmEvent.always;
        }
        return AlarmEvent.never;
    }

    public AlarmType getAlarmType(){
        if (alarmMaxValue != null && alarmMinValue != null){
            return AlarmType.minmax;
        }
        if ((alarmMinValue != null) && (alarmMaxValue == null)) {
            return AlarmType.min;
        }
        if ((alarmMinValue == null) && (alarmMaxValue != null)) {
            return AlarmType.max;
        }
        return AlarmType.none;
    }

    public static Config getConfigByID(Collection<Config> configs, String id){
        for(Config c : configs){
            if (c.id.equals(id)){
                return c;
            }
        }
        return  null;
    }

    public boolean isVisible(){
        return parent == null || parent.equals(id);
    }

    public String getGroupValue() {
        if (groupValue ==null){
            return id;
        }
        return groupValue;
    }

    public boolean isSensor() {
        return sensor;
    }

    @Override
    public String toString() {
        return "Config{" +
                "id='" + id + '\'' +
                ", device='" + device + '\'' +
                ", display='" + display + '\'' +
                ", write=" + write +
                ", label='" + label + '\'' +
                ", enabled=" + enabled +
                ", default_value=" + default_value +
                ", suffix='" + suffix + '\'' +
                ", min=" + min +
                ", max=" + max +
                ", icon='" + icon + '\'' +
                ", posX=" + posX +
                ", posY=" + posY +
                ", monostab=" + monostab +
                ", alarmSet=" + alarmSet +
                ", alarmMinValue=" + alarmMinValue +
                ", alarmMaxValue=" + alarmMaxValue +
                ", alarmSwitch='" + alarmSwitch + '\'' +
                ", alarmText='" + alarmText + '\'' +
                ", precision=" + precision +
                ", parent='" + parent + '\'' +
                '}';
    }
}
