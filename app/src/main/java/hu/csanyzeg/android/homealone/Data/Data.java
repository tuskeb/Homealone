package hu.csanyzeg.android.homealone.Data;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Created by tanulo on 2018. 07. 05..
 */

abstract public class Data<T> {
    public static double GPSDistanceInMeter = 0;
    public static final long NO_UPDATE_NEED = -1;
    public static final int[] COLORS = {0xffff00ff, 0xff00ff00, 0xff0000ff, 0xffff0000};
    private static int colorCounter = 0;

    protected Config config;

    protected T settingsValue;
    protected int fastPollingRemainCount = 0;
    protected long fastPollingIntervalMs = Config.getFastPollingIntervalMs();

    protected Date lastUpdateDate = null;
    protected long refreshIntervalMs = 20000;
    protected NamedArrayList<Entry<T>> graphEntries = new NamedArrayList<>();

    abstract public Date getFromDate();
    abstract public Date getToDate();

    protected OnDataUpdateListener onDataUpdateListener = null;

    protected Data<Boolean> alarmSwitch = null;

    protected long valueCount = 50;
    private int color;



    public long getValueCount() {
        return valueCount;
    }

    public void setValueCount(long valueCount) {
        this.valueCount = valueCount;
    }

    /*
    public abstract void onDataEndUpdate();
    public abstract void onDataBeginUpdate();
    */


    public int getColor() {
        return color;
    }

    synchronized
    private static int getNextColor(){
        int c = COLORS[colorCounter %COLORS.length];
        colorCounter ++;
        return c;
    }

    synchronized
    public Data<Boolean> getAlarmSwitch() {
        return alarmSwitch;
    }

    synchronized
    public void setAlarmSwitch(Data<Boolean> alarmSwitch) {
        this.alarmSwitch = alarmSwitch;
    }

    synchronized
    public OnDataUpdateListener getOnDataUpdateListener() {
        return onDataUpdateListener;
    }

    synchronized
    public void setOnDataUpdateListener(OnDataUpdateListener onDataUpdateListener) {
        this.onDataUpdateListener = onDataUpdateListener;
    }

    synchronized
    public T currentValue(){
        if (graphEntries.size()==0) return null;
        return Collections.max(graphEntries).value;
    };

    synchronized
    public Date currentDate(){
        if (graphEntries.size()==0) return null;
        return Collections.max(graphEntries).date;
    };

    synchronized
    public T getSettingsValue() {
        return settingsValue;
    }

    synchronized
    public void setSettingsValue(T settingsValue) {
        this.settingsValue = settingsValue;
    }



    abstract protected void getDataFromSensorRecords(ArrayList<SensorRecord>  sensorRecords);

    abstract public String getAlarmText();
    abstract public boolean isAlarm();

    protected static Random random = new Random();

    public Data(Config config) {
        this.config = config;
        graphEntries.setName(config.display);
        if (config.color==null) {
            color = getNextColor();
        }else{
            color = config.color;
        }
    }

    synchronized
    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    synchronized
    public long getRefreshIntervalMs() {
        return refreshIntervalMs;
    }

    synchronized
    public void setRefreshIntervalMs(long refreshIntervalMs) {
        this.refreshIntervalMs = refreshIntervalMs;
    }

    synchronized
    public boolean isRefreshNeed(Date now){
        if (fastPollingRemainCount>0){
            if (now.getTime()-fastPollingIntervalMs > lastUpdateDate.getTime()){
                //System.out.println("FastPolling");
                fastPollingRemainCount--;
                return true;
            }
        }
        //System.out.println(fastPollingRemainCount);
        return lastUpdateDate == null || now.getTime()-refreshIntervalMs > lastUpdateDate.getTime();
    }

    synchronized
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public Date getLastUpdateFromDate() {
        return lastUpdateFromDate;
    }

    public Date getLastUpdateToDate() {
        return lastUpdateToDate;
    }

    public Date getMinDate() {
        return minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    protected Date lastUpdateFromDate;
    protected Date lastUpdateToDate;
    protected Date minDate;
    protected Date maxDate;

    synchronized
    private void updateUpdateDates(Date now){
        lastUpdateDate = now;
        lastUpdateFromDate = getFromDate();
        lastUpdateToDate = getToDate();

        // Elavult adatok törlése
        NamedArrayList<Entry<T>> a = new NamedArrayList<>();
        long lufd = lastUpdateFromDate.getTime();
        Entry<T> lastRemoved = null;
        for (Entry<T> e : graphEntries)  {
            if (e.date.getTime()<lufd){
                a.add(e);
            }
        }

        for (Entry<T> e : a)  {
            graphEntries.remove(e);
            lastRemoved = e;
        }

/*
        if (lastRemoved!= null) {

        }else{
            if (graphEntries.size()>=1){
                Entry<T> next = graphEntries.get(0);
                lastRemoved =  new Entry<T>(next.value, getLastUpdateFromDate(), next.color);
            }
        }
*/
        if (lastRemoved!=null){
            graphEntries.add(lastRemoved);
            Collections.sort(graphEntries);
        }

        //Idő ismétlődések eltávolítása
        NamedArrayList<Entry<T>> b = new NamedArrayList<>();
        Entry<T> prev = null;
        for (Entry<T> e : graphEntries)  {
            if (prev != null && e.date.getTime()==prev.date.getTime()){
                b.add(e);
            }
            prev = e;
        }

        for (Entry<T> e : b)  {
            graphEntries.remove(e);
        }


        //érték ismétlődések eltávolítása
        NamedArrayList<Entry<T>> k = new NamedArrayList<>();
        for(int i = 1; i<graphEntries.size()-1; i++) {
            if (((Comparable<T>)(graphEntries.get(i).value)).compareTo(graphEntries.get(i-1).value) == 0 && ((Comparable<T>)(graphEntries.get(i).value)).compareTo(graphEntries.get(i+1).value)==0){
                k.add(graphEntries.get(i));
                //System.out.println("----------- Remove -------------");
            }
        }

        for (Entry<T> e : k)  {
            graphEntries.remove(e);
        }


        //Színezés
        for (Entry<T> e : graphEntries)  {
            e.color = color;
        }

        try {
            minDate = Collections.min(graphEntries).date;
        }
        catch (NoSuchElementException e){
            minDate = null;
        }
        try {
            maxDate = Collections.max(graphEntries).date;
        }catch (NoSuchElementException e) {
            maxDate = null;
        }
        //System.out.println(graphEntries);
    }


    synchronized
    public void updateFromSensorRecords(ArrayList<SensorRecord> sensorRecords, Date now) {
        if (onDataUpdateListener != null) {
            onDataUpdateListener.onBeginUpdate(Data.this);
        }

        getDataFromSensorRecords(sensorRecords);
        if (getFromDate() != null && getToDate() != null)
        {
            updateUpdateDates(now);
        }
        if (onDataUpdateListener != null) {
            onDataUpdateListener.onEndUpdate(Data.this);
        }
    }


    @Override
    public String toString() {
        return "Data{" +
                "config=" + config +
                ", settingsValue=" + settingsValue +
                ", fastPollingRemainCount=" + fastPollingRemainCount +
                ", fastPollingIntervalMs=" + fastPollingIntervalMs +
                ", lastUpdateDate=" + lastUpdateDate +
                ", refreshIntervalMs=" + refreshIntervalMs +
                ", graphEntries=" + graphEntries +
                ", onDataUpdateListener=" + onDataUpdateListener +
                ", alarmSwitch=" + alarmSwitch +
                ", valueCount=" + valueCount +
                ", color=" + color +
                ", lastUpdateFromDate=" + lastUpdateFromDate +
                ", lastUpdateToDate=" + lastUpdateToDate +
                ", minDate=" + minDate +
                ", maxDate=" + maxDate +
                '}';
    }

    synchronized
    public NamedArrayList<Entry<T>> getGraphEntries() {
        return graphEntries;
    }

    synchronized
    public Config getConfig() {
        return config;
    }

    synchronized
    public int getFastPollingRemainCount() {
        return fastPollingRemainCount;
    }

    synchronized
    public void setFastPollingRemainCount(int fastPollingRemainCount) {
        /*if (this.fastPollingRemainCount==0 && fastPollingRemainCount>0){
            lastUpdateDate = now;
        }*/
        //System.out.println(" Set Fast polling !!!!!!!!!!!!!!!");
        this.fastPollingRemainCount = fastPollingRemainCount;
    }

    synchronized
    public long getFastPollingIntervalMs() {
        return fastPollingIntervalMs;
    }

    synchronized
    public void setFastPollingIntervalMs(long fastPollingIntervalMs) {
        this.fastPollingIntervalMs = fastPollingIntervalMs;
    }

    public void setGraphEntries(NamedArrayList<Entry<T>> graphEntries) {
        this.graphEntries = graphEntries;
    }
}
