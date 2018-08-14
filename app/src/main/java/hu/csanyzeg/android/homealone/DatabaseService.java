package hu.csanyzeg.android.homealone;

import android.app.IntentService;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import hu.csanyzeg.android.homealone.Data.BoolData;
import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Data.NumberData;
import hu.csanyzeg.android.homealone.Data.OnDataUpdateListener;
import hu.csanyzeg.android.homealone.Data.SensorRecord;
import hu.csanyzeg.android.homealone.Utils.HttpDownloadUtil;
import hu.csanyzeg.android.homealone.Utils.NotificationHelper;
import hu.csanyzeg.android.homealone.Utils.ParseConfigINI;
import hu.csanyzeg.android.homealone.Utils.ParseCurrentDataXML;
import hu.csanyzeg.android.homealone.Utils.ParseHistoryDataXML;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class DatabaseService extends IntentService {
    public static final int BR_DATA_UPDATE = 1;
    public static final int BR_CONFIG_UPDATE = 2;
    public static final int BR_ALARM = 3;
    public static final int BR_LOCATION_CHANGE = 3;
    public static final int BR_DOWNLOAD_FAILED = 4;
    public static final int BR_DOWNLOAD_COMPLETE = 5;
    public static final String BR_MESSAGE = "BR_MESSAGE";
    public static final String BR_OBJECT_HASH = "BR_OBJECT_HASH";
    public static final String BR_DATA_ID = "BR_DATA_ID";
    public static final String NOTIFICATION = "com.example.tanulo.szenzor.service.receiver";
    //public static final long DATA_STORE_INTERVAL = 1000 * 60 * 60;//ms


    private static final String logString = "DatabaseService";
    private final HashMap<String, Data> dataHashMap = new HashMap<>();
    private ArrayList<Config> configs;
    private final IBinder mBinder = new MyBinder();


    private String lastNotificationString = "";
    private LocationManager locationManager;
    private String locationProvider;
    private Location locationLastLocation;
    private Location locationHome;
    private LocationListener locationListener;

    private Date rpiLastCurrentDataDate;
    private Date androidLastCurrentDataDate;

    private String serverURL = "http://zwl.strangled.net:9002";

    public String getServerURL() {
        return serverURL;
    }

    //private ArrayList<Config> configs = null;
    //public static final String CONFIGS = "configs";


    public DatabaseService() {
        super(logString);

        //super();
    }


    synchronized
    public Date getRpiCurrentDate() {
        if (rpiLastCurrentDataDate == null){
            return null;
        }
        return new Date(Calendar.getInstance().getTime().getTime() - getTimeDiffRpiAndroid());
    }

    public long getTimeDiffRpiAndroid(){
        if (rpiLastCurrentDataDate == null || androidLastCurrentDataDate == null) return 0;
        return  androidLastCurrentDataDate.getTime() - rpiLastCurrentDataDate.getTime();
    }

    public ArrayList<Config> getConfigs() {
        return configs;
    }

    public HashMap<String, Data> getDataHashMap() {
        return dataHashMap;
    }

    private void generateDataUpdateNotification(Data data) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(BR_MESSAGE, BR_DATA_UPDATE);
        intent.putExtra(BR_OBJECT_HASH, data.hashCode());
        intent.putExtra(BR_DATA_ID, data.getConfig().id);
        sendBroadcast(intent);
    }

    private void generateAlarmBroadcastNotification(Data data) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(BR_MESSAGE, BR_ALARM);
        intent.putExtra(BR_OBJECT_HASH, data.hashCode());
        intent.putExtra(BR_DATA_ID, data.getConfig().id);
        sendBroadcast(intent);
    }

    private void generateConfigUpdateNotification() {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(BR_MESSAGE, BR_CONFIG_UPDATE);
        sendBroadcast(intent);
    }


    private void generateDownloadCompleteNotification() {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(BR_MESSAGE, BR_DOWNLOAD_COMPLETE);
        sendBroadcast(intent);
    }

    private void generateDownloadFailedNotification() {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(BR_MESSAGE, BR_DOWNLOAD_FAILED);
        sendBroadcast(intent);
    }

    private void generateLocationChangeNotification() {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(BR_MESSAGE, BR_LOCATION_CHANGE);
        sendBroadcast(intent);
    }

    public static final int JOB_ID = 0x01;
    private boolean refreshInProgress = false;

/*
    private class HttpAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            refreshInProgress = true;
            super.onPreExecute();
            stopDataTime = Collections.max(dataHashMap.values(), new Comparator<Data>() {
                @Override
                public int compare(Data data, Data t1) {
                    return data.getToDate().compareTo(t1.getToDate());
                }
            }).getToDate();
            long sdt = Long.MAX_VALUE;
            for(Data data : dataHashMap.values()){
                if (data.getLastUpdateDate() == null){
                    if (data.getFromDate().getTime()<sdt){
                        sdt = data.getFromDate().getTime();
                    }
                } else{
                    if (data.getLastUpdateDate().getTime()<sdt){
                        sdt = data.getLastUpdateDate().getTime();
                    }
                }
            }
            startDataTime = new Date(sdt);
            System.out.println("Download startDataTime " + startDataTime);
            System.out.println("Download stopDataTime " + stopDataTime);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ArrayList<SensorRecord> sensorRecords = ParseHistoryDataXML.parse(s);
            //System.out.println(sensorRecords);
            for (Data d : dataHashMap.values()) {
                //d.updateFromRandom();
                d.updateFromSensorRecords(sensorRecords);
            }
            refreshInProgress = false;
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder stringBuilder = new StringBuilder();
            HashMap<String, String> get = new HashMap<>();
            SimpleDateFormat simpleDateFormat = ParseHistoryDataXML.getDateParser();
            get.put("format", "xml");
            get.put("start", simpleDateFormat.format(startDataTime));
            get.put("stop", simpleDateFormat.format(stopDataTime));
            try {
                System.out.println("Connect");
                url = new URL(strings[0] + "/" + HttpMapUtil.mapToString(get));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(2000);
                urlConnection.setReadTimeout(2000);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("GET");

                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(HttpMapUtil.mapToString(get));
                writer.flush();

                System.out.println("Waiting");
                long start = System.currentTimeMillis();

                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                long end = System.currentTimeMillis();
                System.out.println("Response " + (end - start) + " ms");

                int data;
                data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    data = reader.read();
                    stringBuilder.append(current);
                }
                System.out.println("End connection");
            } catch (Exception e) {
                if (e.getMessage() == null) {
                    Log.e("HTTP", Log.getStackTraceString(e));
                } else {
                    Log.e("HTTP", e.getMessage());
                }
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            //System.out.println(stringBuilder.toString());
            return stringBuilder.toString();
        }
    }

*/
    private boolean isRefreshNeed() {
        for (Data d : dataHashMap.values()) {
            if (d.isRefreshNeed(getRpiCurrentDate())) {
                return true;
            }
        }
        return false;
    }


    private boolean forceDownloadAllData = true;
    public void setForceDownloadAllData(){
        forceDownloadAllData = true;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (true) {
            if (!refreshInProgress && (isRefreshNeed() || forceDownloadAllData)) {
                System.out.println("Refresh need");
                refreshInProgress = true;


                //Régebbi adatok letöltése
                if (getRpiCurrentDate()!= null) {
                    Date startDataTime;
                    Date stopDataTime;
                    stopDataTime = Collections.max(dataHashMap.values(), new Comparator<Data>() {
                        @Override
                        public int compare(Data data, Data t1) {
                            return data.getToDate().compareTo(t1.getToDate());
                        }
                    }).getToDate();
                    if (!forceDownloadAllData) {

                        long sdt = Long.MAX_VALUE;
                        for (Data data : dataHashMap.values()) {
                            if (data.getLastUpdateDate() == null) {
                                if (data.getFromDate().getTime() < sdt) {
                                    sdt = data.getFromDate().getTime();
                                }
                            } else {
                                if (data.getLastUpdateDate().getTime() < sdt) {
                                    sdt = data.getLastUpdateDate().getTime();
                                }
                            }
                        }
                        startDataTime = new Date(sdt);
                    }else{
                        System.out.println("Force update all data  --------");
                        startDataTime = Collections.min(dataHashMap.values(), new Comparator<Data>() {
                            @Override
                            public int compare(Data data, Data t1) {
                                //System.out.println(data.getFromDate());
                                return data.getFromDate().compareTo(t1.getFromDate());
                            }
                        }).getFromDate();
                    }

                    System.out.println("Download startDataTime " + startDataTime);
                    System.out.println("Download stopDataTime " + stopDataTime);
                    SimpleDateFormat simpleDateFormat = ParseHistoryDataXML.getDateParser();

                    if (forceDownloadAllData || startDataTime.getTime() < stopDataTime.getTime() - Config.polling * 1500) {
                        forceDownloadAllData = false;
                        HashMap<String, String> get = new HashMap<>();
                        get.put("format", "xml");
                        get.put("start", simpleDateFormat.format(startDataTime));
                        get.put("stop", simpleDateFormat.format(stopDataTime));
                        new HttpDownloadUtil() {
                            @Override
                            public void onDownloadStart() {
                                System.out.println("Start downloading history...");
                            }

                            @Override
                            public void onDownloadComplete(StringBuilder stringBuilder) {
                                if (stringBuilder == null) {
                                    generateDownloadFailedNotification();
                                    return;
                                }
                                ArrayList<SensorRecord> sensorRecords = ParseHistoryDataXML.parse(stringBuilder.toString());
                                //System.out.println(sensorRecords);
                                for (Data d : dataHashMap.values()) {
                                    //d.updateFromRandom();
                                    d.updateFromSensorRecords(sensorRecords, getRpiCurrentDate());
                                }
                                //generateDownloadCompleteNotification();
                            }
                        }.download(new HttpDownloadUtil.HttpRequestInfo(serverURL, HttpDownloadUtil.Method.POST, get, get));
                    }
                }

                //Aktuális adatok letöltése
                HashMap<String, String> get = new HashMap<>();
                get.put("format", "xml");
                new HttpDownloadUtil() {
                    @Override
                    public void onDownloadStart() {
                        System.out.println("Start downloading current data...");
                        androidLastCurrentDataDate = Calendar.getInstance().getTime();
                    }

                    @Override
                    public void onDownloadComplete(StringBuilder stringBuilder) {
                        refreshInProgress = false;

                        if (stringBuilder == null) {
                            generateDownloadFailedNotification();
                            return;
                        }
                        ArrayList<SensorRecord> sensorRecords = ParseCurrentDataXML.parse(stringBuilder.toString());

                        if (sensorRecords.size()>0){
                            rpiLastCurrentDataDate = new Date (sensorRecords.get(0).ts.getTime() - getDownloadTimeMs() / 2);
                        }

                        for (Data d : dataHashMap.values()) {
                            d.updateFromSensorRecords(sensorRecords, getRpiCurrentDate());
                        }

                        getDownloadTimeMs();
                        generateDownloadCompleteNotification();


                    }
                }.download(new HttpDownloadUtil.HttpRequestInfo(serverURL, HttpDownloadUtil.Method.POST, get, get));

            }
            System.out.println(refreshInProgress);
            System.out.println("RPI: " + getRpiCurrentDate());
            System.out.println("And: " + Calendar.getInstance().getTime());
            //System.out.println(dataHashMap.size());
            //generateAlarmNotification();
            StringBuilder stringBuilder = new StringBuilder();
            if (!isLocationEnabled()) {
                stringBuilder.append("A helyadatok nem hozzáférhetők!");
                stringBuilder.append("\n");
            }
            for (Data d : dataHashMap.values()) {
                if (d.isAlarm()) {
                    stringBuilder.append(d.getAlarmText());
                    stringBuilder.append("\n");
                    generateAlarmBroadcastNotification(d);
                }
            }
            if (stringBuilder.length() > 0) {
                generateAlarmNotification(stringBuilder.toString());
            }

            try {
                Thread.sleep(Config.getServiceThreadSleepIntervalMs());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void generateAlarmNotification(String s) {
        if (!s.equals(lastNotificationString)) {
            lastNotificationString = s;
            //System.out.println(s);
            NotificationHelper notificationHelper = new NotificationHelper(this);
            notificationHelper.createNotification("Riasztás", s);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        DatabaseService getService() {
            return DatabaseService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //ParseConfigXML parseConfigXML = new ParseConfigXML(new File(Environment.getExternalStorageDirectory(), "Download/homealone_metadataa.xml")){
        /*ParseConfigXML parseConfigXML = new ParseConfigXML(R.raw.xml_homealone_metadata, this){
            @Override
            protected void onFileOpenError(FileNotFoundException e) {
                Log.e("Open",e.getMessage());
            }
        };
*/
        ParseConfigINI parseConfigINI = new ParseConfigINI(R.raw.ini_homealone_metadata, this) {
            @Override
            protected void onFileOpenError(FileNotFoundException e) {
                Log.e("Open", e.getMessage());
            }
        };


        //ArrayList<Config> configs = parseConfigINI.parse();

        configs = parseConfigINI.parse();
        System.out.println(configs);


        for (Config c : configs) {
            if (c.isEnabled()) {
                if (c.isSwitch()) {
                    dataHashMap.put(c.id, new BoolData(c) {
                        @Override
                        public Date getFromDate() {
                            if (getRpiCurrentDate()== null) return null;
                            return new Date(getRpiCurrentDate().getTime() - Config.getDataStoreIntervalMs());
                        }

                        @Override
                        public Date getToDate() {
                            return getRpiCurrentDate();
                        }
                    });
                } else {
                    dataHashMap.put(c.id, new NumberData(c) {
                        @Override
                        public Date getFromDate() {
                            if (getRpiCurrentDate()== null) return null;
                            return new Date(getRpiCurrentDate().getTime() - Config.getDataStoreIntervalMs());
                        }

                        @Override
                        public Date getToDate() {
                            return getRpiCurrentDate();
                        }
                    });
                }
                dataHashMap.get(c.id).setOnDataUpdateListener(new OnDataUpdateListener() {
                    @Override
                    public void onBeginUpdate(Data data) {

                    }

                    @Override
                    public void onEndUpdate(Data data) {
                        generateDataUpdateNotification(data);
                    }
                });
                dataHashMap.get(c.id).setRefreshIntervalMs(Config.polling * 1000);
            }
        }
        for (Data c : dataHashMap.values()) {
            if (c.getConfig().alarmSwitch != null) {
                c.setAlarmSwitch(dataHashMap.get(c.getConfig().alarmSwitch));
            }
        }

        generateConfigUpdateNotification();


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        locationProvider = locationManager.getBestProvider(criteria, false);
        locationManager.requestLocationUpdates(locationProvider, 400, 1, locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                locationLastLocation = location;
                Data.GPSDistanceInMeter = getDistanceFromHomeInMeters();
                generateLocationChangeNotification();
                System.out.println("Location changed");
                System.out.println(" New Longitude: " + location.getLongitude());
                System.out.println(" New Latitude: " + location.getLatitude());
                System.out.println(" Config Longitude: " + Config.gpsLongitude);
                System.out.println(" Config Latitude: " + Config.gpsLatitude);
                System.out.println(" Distance: " + String.format("%.2f km", location.distanceTo(locationHome) / 1000));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                System.out.println("Enabled new provider " + locationProvider);
            }

            @Override
            public void onProviderDisabled(String s) {
                System.out.println("Disabled provider " + locationProvider);
            }
        });

        locationLastLocation = locationManager.getLastKnownLocation(locationProvider);
        locationHome = new Location(locationProvider);
        locationHome.setLatitude(Config.gpsLatitude);
        locationHome.setLongitude(Config.gpsLongitude);


        // Initialize the location fields
        if (locationLastLocation != null) {
            System.out.println("Provider " + locationProvider + " has been selected.");
            locationListener.onLocationChanged(locationLastLocation);
        } else {
            System.out.println("Location not available");
        }
    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    public boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public float getDistanceFromHomeInMeters() {
        return locationLastLocation.distanceTo(locationHome);
    }

    public Location getLocationLastLocation() {
        return locationLastLocation;
    }
}
