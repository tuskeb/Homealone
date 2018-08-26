package hu.csanyzeg.android.homealone;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.icu.util.Measure;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Interfaces.Sensor;
import hu.csanyzeg.android.homealone.UI.GraphNumberSensorView;
import hu.csanyzeg.android.homealone.Utils.FullSensorViewInflater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ServiceConnection{
    private ViewGroup layout;
    private ViewGroup layout2;
    private DatabaseService databaseService = null;
    private ArrayList<Sensor> sensors = new ArrayList<>();
    private TextView rpiTimeTV;
    private TextView distanceTV;
    private TextView alarmTV;
    private LinearLayout noconnection;


    private BroadcastReceiver databaseBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                switch (bundle.getInt(DatabaseService.BR_MESSAGE)){
                    case DatabaseService.BR_DATA_UPDATE:
                        //System.out.println(databaseService.getDataHashMap());
                        int hash = bundle.getInt(DatabaseService.BR_OBJECT_HASH);
                        if (noconnection.getVisibility()!=View.GONE) {
                            noconnection.setVisibility(View.GONE);
                        }
                        for(Sensor f : sensors) {
                            if (null != f.getData().get(bundle.getString(DatabaseService.BR_DATA_ID))) {
                                f.updateData();
                            }
                        }
                        break;
                    case DatabaseService.BR_CONFIG_UPDATE:
                        //databaseService.getDataHashMap();
                        if (databaseService != null) {
                            refreshUI(databaseService.getDataHashMap(), databaseService.getConfigs());
                            if (noconnection.getVisibility()!=View.GONE) {
                                noconnection.setVisibility(View.GONE);
                            }
                        }
                        break;
                    case DatabaseService.BR_LOCATION_CHANGE:
                        if (databaseService != null) {
                            Float dt = null;
                            if ((dt = databaseService.getDistanceFromHomeInMeters()) == null) {
                                distanceTV.setText(getString(R.string.locationdistance) + String.format("%.1f km", dt / 1000f));
                            }else{
                                distanceTV.setText(R.string.locationnotfound);
                            }
                        }
                        break;
                    case DatabaseService.BR_ALARM:
                        if (databaseService != null) {
                            if (databaseService.getAlarmText()!=null) {
                                alarmTV.setText(databaseService.getAlarmText());
                            }else{
                                alarmTV.setText(R.string.alarmnone);
                            }
                        }
                    break;
                    case DatabaseService.BR_DOWNLOAD_FAILED:
                        if (noconnection.getVisibility()!=View.VISIBLE) {
                            noconnection.setVisibility(View.VISIBLE);
                        }
                        break;
/*                    case DatabaseService.BR_RPI_TIME_UPDATE:

                       break;*/
                }
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.options_menu:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.exit_menu:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.mainLayout);
        layout2 = findViewById(R.id.mainLayout2);
        rpiTimeTV = findViewById(R.id.rpiTimeTV);
        distanceTV = findViewById(R.id.distanceTV);
        alarmTV = findViewById(R.id.alarmTV);
        noconnection = findViewById(R.id.noconnection);


        rpiTimeTV.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (databaseService != null){
                    if (databaseService.getRpiCurrentDate()!= null) {
                        rpiTimeTV.setText(getString(R.string.hometime) + databaseService.getRpiCurrentDate());
                    }else{
                        rpiTimeTV.setText(R.string.hometimenotfound);
                    }
                }
                rpiTimeTV.postDelayed(this,980);
            }
        }, 980);

        Intent i= new Intent(getApplicationContext(), DatabaseService.class);
        startService(i);
        //DatabaseService.enqueueWork(getBaseContext(), i);


    }

    public void refreshUI(HashMap<String, Data> dataHashMap, ArrayList<Config> configs){
        if (layout!= null) {
            layout.removeAllViews();
        }
        if (layout2!= null) {
            layout2.removeAllViews();
        }
        for (Config config : configs) {
            if (config.isEnabled()) {
                if (layout.findViewWithTag(config.id)==null) {
                    View sensorView = (View) FullSensorViewInflater.inflate(this, dataHashMap, config.id, databaseService);
                    if (sensorView != null) {
                        sensors.add((Sensor) sensorView);
                        if (layout2!=null && sensors.size()%2==0){
                            layout2.addView(sensorView);
                        }else{
                            layout.addView(sensorView);
                        }
                        /*if (layout2!=null){
                            if (((Sensor) sensorView).getConfig().isSwitch()){
                                layout2.addView(sensorView);
                            }else{
                                layout.addView(sensorView);
                            }
                        }else{
                            layout.addView(sensorView);
                        }
                        */
                        System.out.println(layout.getHeight());
                        sensorView.setTag(config.id);
                        ((Sensor) sensorView).updateData();

                    }
                }
            }
        }
/*
        for(int i = 0; i<layout.getChildCount();i++) {
            final View v = layout.getChildAt(i);
            if (v instanceof GraphNumberSensorView){
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getBaseContext(), GraphActivity.class);
                        intent.putExtra(DatabaseService.BR_DATA_ID, ((GraphNumberSensorView) v).getConfig().id);
                        startActivity(intent);
                    }
                });
            }
        }*/
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        DatabaseService.MyBinder b = (DatabaseService.MyBinder) binder;
        databaseService = b.getService();
        refreshUI(databaseService.getDataHashMap(), databaseService.getConfigs());
        /*
        for(Sensor f : sensors) {
            databaseService.getDataHashMap().add(f.getData());
        }
        */
        System.out.println("Az adatbázis szolgáltatáshoz csatlakozott");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        databaseService = null;
        System.out.println("Az adatbázis szolgáltatással a kapcsolat megszakadt.");
    }


    public void generateAlarmNotification(){
        /*
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, DatabaseService.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        Notification n  = new Notification.Builder(this)
                .setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject")
                .setSmallIcon(R.drawable.led_circle_red)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.led_circle_green, "Call", pIntent)
                .addAction(R.drawable.led_circle_red, "More", pIntent)
                .addAction(R.drawable.led_circle_grey, "And more", pIntent).build();

        notificationManager.notify(0, n);*/
       /* NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);

        int NOTIFICATION_ID = 234;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {


            String CHANNEL_ID = "my_channel_01";
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), "asdsadsads")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ASD")
                .setContentText("MSG");

        Intent resultIntent = new Intent(getBaseContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
            stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());*/
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            String groupId = "some_group_id";
            CharSequence groupName = "Some Group";
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(groupId, groupName));
            List<NotificationChannelGroup> notificationChannelGroups = new ArrayList();
            notificationChannelGroups.add(new NotificationChannelGroup("group_one", "Group One"));
            notificationChannelGroups.add(new NotificationChannelGroup("group_two", "Group Two"));
            notificationChannelGroups.add(new NotificationChannelGroup("group_three", "Group Three"));

            notificationManager.createNotificationChannelGroups(notificationChannelGroups);

            int notifyId = 1;
            String channelId = "some_channel_id";

            Notification notification = new Notification.Builder(getBaseContext())
                    .setContentTitle("Some Message")
                    .setContentText("You've received new messages!")
                    .setSmallIcon(R.drawable.led_circle_grey)
                    //.setChannel(channelId)
                    .build();

            notificationManager.notify(notifyId, notification);
        }*/

    }



    @Override
    protected void onResume() {
        super.onResume();
        Intent intent= new Intent(this, DatabaseService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
        registerReceiver(databaseBroadcastReceiver, new IntentFilter(DatabaseService.NOTIFICATION));
        //generateAlarmNotification();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
        unregisterReceiver(databaseBroadcastReceiver);
    }

}
