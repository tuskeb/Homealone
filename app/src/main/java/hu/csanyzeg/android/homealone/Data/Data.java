package hu.csanyzeg.android.homealone.Data;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
    protected long fastPollingIntervalMs = 4000;

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



    public void fetchDataFromRandom() {
        RandomAsyncTask task = new RandomAsyncTask();
        task.execute(1);
    }

    public void fetchDataFromMySQL() {
        RandomAsyncTask task = new RandomAsyncTask();
        task.execute(1);
    }

    abstract protected NamedArrayList<Entry<T>> getDataFromMySQL();
    abstract protected NamedArrayList<Entry<T>> getDataFromRandom();

    abstract public String getAlarmText();
    abstract public boolean isAlarm();

    protected static Random random = new Random();

    public Data(Config config) {
        this.config = config;
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
    public boolean isRefreshNeed(){
        if (fastPollingRemainCount>0){
            if (Calendar.getInstance().getTime().getTime()-fastPollingIntervalMs > lastUpdateDate.getTime()){
                //System.out.println("FastPolling");
                fastPollingRemainCount--;
                return true;
            }
        }
        //System.out.println(fastPollingRemainCount);
        return lastUpdateDate == null || Calendar.getInstance().getTime().getTime()-refreshIntervalMs > lastUpdateDate.getTime();
    }

    synchronized
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    synchronized
    public void updateFromMysql(){
        fetchDataFromMySQL();
        lastUpdateDate = Calendar.getInstance().getTime();
    }

    synchronized
    public void updateFromRandom(){
        fetchDataFromRandom();
        lastUpdateDate = Calendar.getInstance().getTime();
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
        if (this.fastPollingRemainCount==0 && fastPollingRemainCount>0){
            lastUpdateDate = Calendar.getInstance().getTime();
        }
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
