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

    protected class RandomAsyncTask extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (onDataUpdateListener != null) {
                onDataUpdateListener.onBeginUpdate(Data.this);
            }
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            NamedArrayList<Entry<T>> entries = getDataFromRandom();
            synchronized (graphEntries) {
                graphEntries = entries;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (onDataUpdateListener != null) {
                onDataUpdateListener.onEndUpdate(Data.this);
            }
        }
    }
/*
    protected class XMLAsyncTask extends AsyncTask<String, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (onDataUpdateListener != null) {
                onDataUpdateListener.onBeginUpdate(Data.this);
            }
        }

        @Override
        protected Integer doInBackground(String... strings) {
            NamedArrayList<Entry<T>> entries = getDataFromXML(strings[0]);
            synchronized (graphEntries) {
                graphEntries = entries;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (onDataUpdateListener != null) {
                onDataUpdateListener.onEndUpdate(Data.this);
            }
        }
    }

    protected class MySQLAsyncTask extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected Integer doInBackground(Integer... integers) {

            if (onDataUpdateListener != null) {
                onDataUpdateListener.onBeginUpdate(Data.this);
            }

            NamedArrayList<Entry<T>> entries =  getDataFromMySQL();


            synchronized (graphEntries) {
                graphEntries = entries;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (onDataUpdateListener != null) {
                onDataUpdateListener.onEndUpdate(Data.this);
            }
        }
    }
*/


    protected void fetchDataFromRandom() {
        RandomAsyncTask task = new RandomAsyncTask();
        task.execute(1);
    }
/*
    protected void fetchDataFromMySQL() {
        MySQLAsyncTask task = new MySQLAsyncTask();
        task.execute(1);
    }

    protected void fetchDataFromXML(String xml) {
        XMLAsyncTask task = new XMLAsyncTask();
        task.execute(xml);
    }
*/
    protected void fetchDataFromSensorRecords(ArrayList<SensorRecord> sensorRecords) {
        getDataFromSensorRecords(sensorRecords);
    }

    //abstract protected NamedArrayList<Entry<T>> getDataFromMySQL();
    abstract protected NamedArrayList<Entry<T>> getDataFromRandom();
    //abstract protected NamedArrayList<Entry<T>> getDataFromXML(String xml);
    abstract protected void getDataFromSensorRecords(ArrayList<SensorRecord>  sensorRecords);

    abstract public String getAlarmText();
    abstract public boolean isAlarm();

    protected static Random random = new Random();

    public Data(Config config) {
        this.config = config;
        graphEntries.setName(config.display);
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


        if (lastRemoved!= null) {
            lastRemoved.date = getLastUpdateFromDate();
        }else{
            if (graphEntries.size()>=1){
                Entry<T> next = graphEntries.get(0);
                lastRemoved =  new Entry<T>(next.value, getLastUpdateFromDate(), next.color);
            }
        }

        if (lastRemoved!=null){
            graphEntries.add(lastRemoved);
            Collections.sort(graphEntries);
        }

        //Ismétlődések eltávolítása
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
    }
/*
    synchronized
    public void updateFromMysql(){
        fetchDataFromMySQL();
        updateUpdateDates();
    }

    synchronized
    public void updateFromXML(String xml){
        fetchDataFromXML(xml);
        updateUpdateDates();
    }
*/
    synchronized
    public void updateFromRandom(Date now){
        fetchDataFromRandom();
        updateUpdateDates(now);
    }

    synchronized
    public void updateFromSensorRecords(ArrayList<SensorRecord> sensorRecords, Date now) {
        if (onDataUpdateListener != null) {
            onDataUpdateListener.onBeginUpdate(Data.this);
        }

        fetchDataFromSensorRecords(sensorRecords);
        if (getFromDate() != null && getToDate() != null)
        {
            updateUpdateDates(now);
        }
        if (onDataUpdateListener != null) {
            onDataUpdateListener.onEndUpdate(Data.this);
        }
    }

    @Override
    synchronized
    public String toString() {
        return "Data{" +
                "lastUpdateDate=" + lastUpdateDate +
                ", refreshIntervalMs=" + refreshIntervalMs +
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
        System.out.println(" Set Fast polling !!!!!!!!!!!!!!!");
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
