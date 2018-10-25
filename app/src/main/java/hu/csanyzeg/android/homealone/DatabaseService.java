package hu.csanyzeg.android.homealone;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import hu.csanyzeg.android.homealone.Data.BoolData;
import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Data.NumberData;
import hu.csanyzeg.android.homealone.Data.OnDataUpdateListener;
import hu.csanyzeg.android.homealone.Data.Options;
import hu.csanyzeg.android.homealone.Data.SensorRecord;
import hu.csanyzeg.android.homealone.Utils.HttpDownloadUtil;
import hu.csanyzeg.android.homealone.Utils.NotificationHelper;
import hu.csanyzeg.android.homealone.Utils.ParseConfigINI;
import hu.csanyzeg.android.homealone.Utils.ParseCurrentDataXML;
import hu.csanyzeg.android.homealone.Utils.ParseHistoryDataXML;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class DatabaseService extends IntentService {
    public static final int BR_DATA_UPDATE = 1;
    public static final int BR_CONFIG_UPDATE = 2;
    public static final int BR_ALARM = 6;
    public static final int BR_LOCATION_CHANGE = 3;
    public static final int BR_RPI_TIME_UPDATE = 7;
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

    private String serverURL;
    private boolean notificationEnabled;
    private String userName;
    private String userPassword;
    private SharedPreferences readPref;

    public String getServerURL() {
        return serverURL;
    }

    public boolean isRandomData(){
        return serverURL.equals("random");
    }
    //private ArrayList<Config> configs = null;
    //public static final String CONFIGS = "configs";

    public BroadcastReceiver settingsBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                switch (bundle.getInt(Options.BR_MESSAGE)) {
                    case Options.BR_UPDATE_SETTINGS:
                        //System.out.println("---------------------- Update Settings --------------");
                        String s = serverURL;
                        updateSettings();
                        if (!s.equals(serverURL)) {
                            //System.out.println("---------------------- Read Config --------------");
                            readConfig();
                        }
                        break;
                }
            }
        }
    };

    public DatabaseService() {
        super(logString);

        //super();
    }

    synchronized
    public void setRpiLastCurrentDataDate(Date rpiLastCurrentDataDate) {
        this.rpiLastCurrentDataDate = rpiLastCurrentDataDate;
    }

    synchronized
    public Date getRpiCurrentDate() {
        if (rpiLastCurrentDataDate == null) {
            return null;
        }
        return new Date(Calendar.getInstance().getTime().getTime() - getTimeDiffRpiAndroid());
    }

    synchronized
    public long getTimeDiffRpiAndroid() {
        if (rpiLastCurrentDataDate == null || androidLastCurrentDataDate == null) return 0;
        return androidLastCurrentDataDate.getTime() - rpiLastCurrentDataDate.getTime();
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


    private void generateRpiTimeUpdateNotification() {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(BR_MESSAGE, BR_RPI_TIME_UPDATE);
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

    private boolean isRefreshNeed() {
        for (Data d : dataHashMap.values()) {
            if (d.isRefreshNeed(getRpiCurrentDate())) {
                return true;
            }
        }
        return false;
    }


    private boolean forceDownloadAllData = true;

    public void setForceDownloadAllData() {
        forceDownloadAllData = true;
    }


    protected void updateFromHTTP() {
        refreshInProgress = true;


        //Régebbi adatok letöltése
        if (getRpiCurrentDate() != null) {
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
            } else {
                //System.out.println("Force update all data  --------");
                startDataTime = Collections.min(dataHashMap.values(), new Comparator<Data>() {
                    @Override
                    public int compare(Data data, Data t1) {
                        //System.out.println(data.getFromDate());
                        return data.getFromDate().compareTo(t1.getFromDate());
                    }
                }).getFromDate();
            }

            //System.out.println("Download startDataTime " + startDataTime);
            //System.out.println("Download stopDataTime " + stopDataTime);
            SimpleDateFormat simpleDateFormat = ParseHistoryDataXML.getDateParser();

            if (forceDownloadAllData || startDataTime.getTime() < stopDataTime.getTime() - Config.polling * 1500) {
                forceDownloadAllData = false;
                HashMap<String, String> get = new HashMap<>();
                get.put("format", "xml");
                get.put("full", "1");
                get.put("start", simpleDateFormat.format(startDataTime));
                get.put("stop", simpleDateFormat.format(stopDataTime));
                new HttpDownloadUtil() {
                    @Override
                    public void onDownloadStart() {
                        //System.out.println("Start downloading history...");
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
        get.put("SID", Config.session_id);
        new HttpDownloadUtil() {
            @Override
            public void onDownloadStart() {
                //System.out.println("Start downloading current data...");
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

                if (sensorRecords.size() > 0) {
                    setRpiLastCurrentDataDate(new Date(sensorRecords.get(0).ts.getTime() - getDownloadTimeMs() / 2));
                    generateRpiTimeUpdateNotification();

                    for (Data d : dataHashMap.values()) {
                        d.updateFromSensorRecords(sensorRecords, getRpiCurrentDate());
                    }

                    getDownloadTimeMs();

                    generateDownloadCompleteNotification();
                }else{
                    generateDownloadFailedNotification();
                }

            }
        }.download(new HttpDownloadUtil.HttpRequestInfo(serverURL, HttpDownloadUtil.Method.GET, get, get));

    }


    private static Random random = new Random();

    public SensorRecord randomSensorRecordField(){
        SensorRecord sensorRecord = new SensorRecord();
        int s;
        Config r;
        Data d;
        while (!(r = ( d = (Data)(dataHashMap.values().toArray()[random.nextInt(dataHashMap.size())])).getConfig()).isSensor());
        if (r.isSwitch()){
            sensorRecord.value = (double)random.nextInt(2);
        }else{
            sensorRecord.value = (double)((random.nextInt((int)((r.max - r.min) * 100))) + r.min)/100;
        }
        sensorRecord.field = r.id;
        sensorRecord.ts = new Date(random.nextLong() % (d.getToDate().getTime() - d.getFromDate().getTime()) + d.getFromDate().getTime());
        return sensorRecord;
    }

    public SensorRecord randomSensorRecordField(Data d){
        SensorRecord sensorRecord = new SensorRecord();
        if (d.getConfig().isSwitch()){
            sensorRecord.value = (double)random.nextInt(2);
        }else{
            sensorRecord.value = (double)((random.nextInt((int)((d.getConfig().max - d.getConfig().min) * 100))) + d.getConfig().min)/100;
        }
        sensorRecord.field = d.getConfig().id;
        sensorRecord.ts = new Date(random.nextLong() % (d.getToDate().getTime() - d.getFromDate().getTime()) + d.getFromDate().getTime());
        return sensorRecord;
    }


    public ArrayList<SensorRecord> randomAllSensorRecordByDate(Date date){
        ArrayList<SensorRecord> sensorRecord = new ArrayList<SensorRecord>();
        Config r;
        for(Data d: dataHashMap.values()) {
            if ((r = d.getConfig()).isSensor()) {
                SensorRecord sensor = new SensorRecord();
                if (r.isSwitch()) {
                    if (d.currentValue()!= null) {
                        if (random.nextInt(10)==0){
                            sensor.value = (double) random.nextInt(2);
                        }else{
                            sensor.value = (boolean)d.currentValue()?1.0:0.0;
                        }
                    }else{
                        sensor.value = (double) random.nextInt(2);
                    }
                } else {
                    if (d.currentValue()!= null) {
                        if (random.nextInt(10)==0) {
                            sensor.value = (double) d.currentValue() + ((double) ((random.nextInt((int) ((r.max - r.min) * 100))) - ((r.max - r.min) * 50) + r.min) / 100.0) / 20.0;
                            if (sensor.value < r.min) {
                                sensor.value = r.min;
                            }
                            if (sensor.value > r.max) {
                                sensor.value = r.max;
                            }
                        }else{
                            sensor.value = (double)d.currentValue();
                        }
                    }else{
                        sensor.value = (double) ((random.nextInt((int) ((r.max - r.min) * 100.0))) + r.min) / 100.0;
                    }
                }
                sensor.field = r.id;
                sensor.ts = date;
                sensorRecord.add(sensor);
            }
        }
        return sensorRecord;
    }

    protected void updateFromRandom() {
        refreshInProgress = true;


        //Régebbi adatok letöltése
        if (getRpiCurrentDate() != null) {
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
            } else {
                //System.out.println("Force update all data  --------");
                startDataTime = Collections.min(dataHashMap.values(), new Comparator<Data>() {
                    @Override
                    public int compare(Data data, Data t1) {
                        //System.out.println(data.getFromDate());
                        return data.getFromDate().compareTo(t1.getFromDate());
                    }
                }).getFromDate();
            }

            //System.out.println("Download startDataTime " + startDataTime);
            //System.out.println("Download stopDataTime " + stopDataTime);
            SimpleDateFormat simpleDateFormat = ParseHistoryDataXML.getDateParser();

            if (forceDownloadAllData || startDataTime.getTime() < stopDataTime.getTime() - Config.polling * 1500) {
                forceDownloadAllData = false;
                //System.out.println("Start random history...");
                ArrayList<SensorRecord> sensorRecords = new ArrayList<>();

                for(int i = 0; i<100; i++){
                    sensorRecords.add(randomSensorRecordField());
                };
                //System.out.println(sensorRecords);
                for (Data d : dataHashMap.values()) {
                    d.updateFromSensorRecords(sensorRecords, getRpiCurrentDate());
                }
            }
        }


        //Aktuális adatok letöltése

        //System.out.println("Start random current data...");
        androidLastCurrentDataDate = Calendar.getInstance().getTime();


        ArrayList<SensorRecord> sensorRecords = randomAllSensorRecordByDate(Calendar.getInstance().getTime());
        if (sensorRecords != null || sensorRecords.size()>0) {
            setRpiLastCurrentDataDate(new Date(sensorRecords.get(0).ts.getTime()));
            generateRpiTimeUpdateNotification();

            for (Data d : dataHashMap.values()) {
                d.updateFromSensorRecords(sensorRecords, getRpiCurrentDate());
            }
            //System.out.println(sensorRecords);
        }
        refreshInProgress = false;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (true) {
            if (configs == null || configs.size() == 0) {
                if (!readConfigInProgress) {
                    readConfig();
                }
            } else {
                if (!refreshInProgress && (isRefreshNeed() || forceDownloadAllData)) {
                    //System.out.println("Refresh need");
                    if (!isRandomData()) {
                        updateFromHTTP();
                    } else {
                        updateFromRandom();
                    }
                }
            /*System.out.println(refreshInProgress);
            System.out.println("RPI: " + getRpiCurrentDate());
            System.out.println("And: " + Calendar.getInstance().getTime());*/
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
                    generateAlarmNotification(alarmText = stringBuilder.toString());
                } else {
                    alarmText = null;
                }

            }

            try {
                Thread.sleep(Config.getServiceThreadSleepIntervalMs());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String alarmText = null;

    synchronized
    public String getAlarmText() {
        return alarmText;
    }

    public void generateAlarmNotification(String s) {
        if (!s.equals(lastNotificationString)) {
            lastNotificationString = s;
            //System.out.println(s);
            if (notificationEnabled) {
                NotificationHelper notificationHelper = new NotificationHelper(this);
                notificationHelper.createNotification("Riasztás", s);
            }
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


    private void updateSettings(){

        notificationEnabled = readPref.getBoolean(Options.OPTION_NOTIFICATION_ENABLE, Options.OPTION_NOTIFICATION_ENABLE_DEFAULT);
        serverURL = readPref.getString(Options.OPTION_SERVER_URL, Options.OPTION_SERVER_URL_DEFAULT);
        userName = readPref.getString(Options.OPTION_USER_NAME, Options.OPTION_USER_NAME_DEFAULT);
        userPassword = readPref.getString(Options.OPTION_USER_PASSWORD, Options.OPTION_USER_PASSWORD_DEFAULT);

    }


    private void createData(){

        for (Config c : configs) {
            if (c.isEnabled()) {
                if (c.isSwitch()) {
                    dataHashMap.put(c.id, new BoolData(c) {
                        @Override
                        public Date getFromDate() {
                            if (getRpiCurrentDate() == null) return null;
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
                            if (getRpiCurrentDate() == null) return null;
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
        //System.out.println("-------------------" + Config.getDataStoreIntervalMs());
        locationHome.setLatitude(Config.gpsLatitude);
        locationHome.setLongitude(Config.gpsLongitude);
    }

    private boolean readConfigInProgress = false;

    private void readConfig(){
        readConfigInProgress = true;
        //System.out.println("ReadConfig - --------------------------------");
        //System.out.println(serverURL);
        HashMap<String, String> get = new HashMap<>();
        get.put("format", "ini");
        get.put("user", userName);
        get.put("passwd", userPassword);


        if (isRandomData()){
            if (configs != null) {
                configs.clear();
            }
            dataHashMap.clear();

            configs = ParseConfigINI.parse(getBaseContext(),R.raw.ini_homealone_metadata);
            //System.out.println(configs);
            createData();
            generateConfigUpdateNotification();
            readConfigInProgress = false;
        } else {
            HttpDownloadUtil configHttpDownloadUtil = new HttpDownloadUtil() {
                @Override
                public void onDownloadStart() {
                    if (configs != null) {
                        configs.clear();
                    }
                    dataHashMap.clear();
                    //generateConfigUpdateNotification();
                }

                @Override
                public void onDownloadComplete(StringBuilder stringBuilder) {
                    if (stringBuilder == null) {
                        generateDownloadFailedNotification();
                        readConfigInProgress = false;
                        return;
                    }

                    configs = ParseConfigINI.parse(stringBuilder.toString());
                    //System.out.println(configs);
                    createData();
                    generateConfigUpdateNotification();
                    readConfigInProgress = false;
                }

            };
            configHttpDownloadUtil.download(new HttpDownloadUtil.HttpRequestInfo(serverURL, HttpDownloadUtil.Method.POST, get, get));
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();



        readPref = getSharedPreferences("pref", MODE_PRIVATE);



        registerReceiver(settingsBroadcastReceiver, new IntentFilter(Options.NOTIFICATION));

        //ParseConfigXML parseConfigXML = new ParseConfigXML(new File(Environment.getExternalStorageDirectory(), "Download/homealone_metadataa.xml")){
        /*ParseConfigXML parseConfigXML = new ParseConfigXML(R.raw.xml_homealone_metadata, this){
            @Override
            protected void onFileOpenError(FileNotFoundException e) {
                Log.e("Open",e.getMessage());
            }
        };
*/
        updateSettings();
        //readConfig();


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        locationProvider = locationManager.getBestProvider(criteria, false);
        locationManager.requestLocationUpdates(locationProvider, 400, 1, locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                locationLastLocation = location;
                Data.GPSDistanceInMeter = getDistanceFromHomeInMeters();
                generateLocationChangeNotification();
                /*
                System.out.println("Location changed");
                System.out.println(" New Longitude: " + location.getLongitude());
                System.out.println(" New Latitude: " + location.getLatitude());
                System.out.println(" Config Longitude: " + Config.gpsLongitude);
                System.out.println(" Config Latitude: " + Config.gpsLatitude);
                System.out.println(" Distance: " + String.format("%.2f km", location.distanceTo(locationHome) / 1000));
                */
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                //System.out.println("Enabled new provider " + locationProvider);
            }

            @Override
            public void onProviderDisabled(String s) {
                //System.out.println("Disabled provider " + locationProvider);
            }
        });

        locationLastLocation = locationManager.getLastKnownLocation(locationProvider);
        locationHome = new Location(locationProvider);



        // Initialize the location fields
        if (locationLastLocation != null) {
            //System.out.println("Provider " + locationProvider + " has been selected.");
            locationListener.onLocationChanged(locationLastLocation);
        } else {
            //System.out.println("Location not available");
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

    public Float getDistanceFromHomeInMeters() {
        if (locationLastLocation != null) {
            return locationLastLocation.distanceTo(locationHome);
        }
        return null;
    }

    public Location getLocationLastLocation() {
        return locationLastLocation;
    }
}
