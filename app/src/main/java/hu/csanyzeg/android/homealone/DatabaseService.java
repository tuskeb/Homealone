package hu.csanyzeg.android.homealone;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import hu.csanyzeg.android.homealone.Data.BoolData;
import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Data.NumberData;
import hu.csanyzeg.android.homealone.Data.OnDataUpdateListener;
import hu.csanyzeg.android.homealone.Utils.NotificationHelper;
import hu.csanyzeg.android.homealone.Utils.ParseConfigINI;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DatabaseService extends IntentService {
    public static final int BR_DATA_UPDATE = 1;
    public static final int BR_CONFIG_UPDATE = 2;
    public static final int BR_ALARM = 3;
    public static final String BR_MESSAGE = "BR_MESSAGE";
    public static final String BR_OBJECT_HASH = "BR_OBJECT_HASH";
    public static final String BR_DATA_ID = "BR_DATA_ID";
    public static final String NOTIFICATION = "com.example.tanulo.szenzor.service.receiver";
    public static final long DATA_STORE_INTERVAL = 1000*60*60;//ms


    private static final String logString = "DatabaseService";
    private final HashMap<String, Data> dataHashMap = new HashMap<>();
    private ArrayList<Config> configs;
    private final IBinder mBinder = new MyBinder();
    private String lastNotificationString = "";

    //private ArrayList<Config> configs = null;
    //public static final String CONFIGS = "configs";


    public DatabaseService() {
        super(logString);
        //super();
    }

    public ArrayList<Config> getConfigs() {
        return configs;
    }

    public HashMap<String, Data> getDataHashMap() {
        return dataHashMap;
    }

    private void generateDataUpdateNotification(Data data){
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(BR_MESSAGE, BR_DATA_UPDATE);
        intent.putExtra(BR_OBJECT_HASH, data.hashCode());
        intent.putExtra(BR_DATA_ID, data.getConfig().id);
        sendBroadcast(intent);
    }

    private void generateAlarmBroadcastNotification(Data data){
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(BR_MESSAGE, BR_ALARM);
        intent.putExtra(BR_OBJECT_HASH, data.hashCode());
        intent.putExtra(BR_DATA_ID, data.getConfig().id);
        sendBroadcast(intent);
    }

    private void generateConfigUpdateNotification(){
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(BR_MESSAGE, BR_CONFIG_UPDATE);
        sendBroadcast(intent);
    }

    public static final int JOB_ID = 0x01;
/*
    public static void enqueueWork(Context context, Intent work) {
        System.out.println("5555555555555555555555555555555");
        enqueueWork(context, DatabaseService.class, JOB_ID, work);
        System.out.println("44444444444444444444444444444444444444444");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        System.out.println("33333333333333333333333333333333333333333333");
        while (true) {
            downloadData();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(dataHashMap.size());
            generateAlarmNotification();
        }
    }
*/

        @Override
        protected void onHandleIntent(@Nullable Intent intent) {
            while (true) {
                downloadData();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //System.out.println(dataHashMap.size());
                //generateAlarmNotification();
                StringBuilder stringBuilder = new StringBuilder();
                for(Data d : dataHashMap.values()) {
                    if (d.isAlarm()) {
                        stringBuilder.append(d.getAlarmText());
                        stringBuilder.append("\n");
                        generateAlarmBroadcastNotification(d);
                    }
                }
                if (stringBuilder.length()>0) {
                    generateAlarmNotification(stringBuilder.toString());
                }
            }
        }

    public void generateAlarmNotification(String s){
            if(!s.equals(lastNotificationString)){
                lastNotificationString = s;
                //System.out.println(s);
                NotificationHelper notificationHelper = new NotificationHelper(this);
                notificationHelper.createNotification("Riasztás",s);
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
        ParseConfigINI parseConfigINI = new ParseConfigINI(R.raw.ini_homealone_metadata, this){
            @Override
            protected void onFileOpenError(FileNotFoundException e) {
                Log.e("Open",e.getMessage());
            }
        };


        //ArrayList<Config> configs = parseConfigINI.parse();

        configs = parseConfigINI.parse();
        System.out.println(configs);


        for (Config c : configs){
            if (c.isEnabled()) {
                if (c.isSwitch()) {
                    dataHashMap.put(c.id, new BoolData(c) {
                        @Override
                        public Date getFromDate() {
                            return new Date(Calendar.getInstance().getTime().getTime() - DATA_STORE_INTERVAL);
                        }

                        @Override
                        public Date getToDate() {
                            return Calendar.getInstance().getTime();
                        }
                    });
                } else {
                    dataHashMap.put(c.id, new NumberData(c) {
                        @Override
                        public Date getFromDate() {
                            return new Date(Calendar.getInstance().getTime().getTime() - DATA_STORE_INTERVAL);
                        }

                        @Override
                        public Date getToDate() {
                            return Calendar.getInstance().getTime();
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
                dataHashMap.get(c.id).setRefreshIntervalMs(Config.polling *1000);
            }
        }
        for (Data c : dataHashMap.values()) {
            if (c.getConfig().alarmSwitch !=null) {
                c.setAlarmSwitch(dataHashMap.get(c.getConfig().alarmSwitch));
            }
        }

        generateConfigUpdateNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void downloadData(){
        for(Data d : dataHashMap.values()) {
            if (d.isRefreshNeed()) {
                d.updateFromRandom();

            }
        }
    }
}
