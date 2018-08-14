package hu.csanyzeg.android.homealone;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Interfaces.Sensor;
import hu.csanyzeg.android.homealone.UI.FullNumberSensorView;
import hu.csanyzeg.android.homealone.Utils.FullSensorViewInflater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements ServiceConnection{
    private ViewGroup layout;
    private DatabaseService databaseService = null;
    private ArrayList<Sensor> sensors = new ArrayList<>();


    private BroadcastReceiver databaseBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                switch (bundle.getInt(DatabaseService.BR_MESSAGE)){
                    case DatabaseService.BR_DATA_UPDATE:
                        //System.out.println(databaseService.getDataHashMap());
                        int hash = bundle.getInt(DatabaseService.BR_OBJECT_HASH);

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
                        }
                        break;
                    case DatabaseService.BR_LOCATION_CHANGE:
                        if (databaseService != null) {
                            setTitle(getString(R.string.app_name) + " (" + String.format("%.1f km", databaseService.getDistanceFromHomeInMeters() / 1000f) +")");
                        }
                        break;
                }
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.mainLayout);




        Intent i= new Intent(getApplicationContext(), DatabaseService.class);
        startService(i);
        //DatabaseService.enqueueWork(getBaseContext(), i);


    }

    public void refreshUI(HashMap<String, Data> dataHashMap, ArrayList<Config> configs){

        for (Config config : configs) {
            if (config.isEnabled()) {
                if (layout.findViewWithTag(config.id)==null) {
                    View sensorView = (View) FullSensorViewInflater.inflate(this, dataHashMap, config.id, databaseService);
                    if (sensorView != null) {
                        sensors.add((Sensor) sensorView);
                        layout.addView(sensorView);
                        sensorView.setTag(config.id);
                        ((Sensor) sensorView).updateData();

                    }
                }
            }
        }

        for(int i = 0; i<layout.getChildCount();i++) {
            final View v = layout.getChildAt(i);
            if (v instanceof FullNumberSensorView){
                ((FullNumberSensorView)v).getGraphNumberSensorView().getGraphView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getBaseContext(), GraphActivity.class);
                        intent.putExtra(DatabaseService.BR_DATA_ID, ((FullNumberSensorView) v).getConfig().id);
                        startActivity(intent);
                    }
                });
            }
        }
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
