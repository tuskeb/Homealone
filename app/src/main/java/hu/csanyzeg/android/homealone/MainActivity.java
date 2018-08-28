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
import hu.csanyzeg.android.homealone.Utils.FullSensorViewInflater;

import java.util.ArrayList;
import java.util.HashMap;

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
                Intent intent = new Intent(this, OptionsActivity.class);
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
                        System.out.println(layout.getHeight());
                        sensorView.setTag(config.id);
                        ((Sensor) sensorView).updateData();

                    }
                }
            }
        }
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        DatabaseService.MyBinder b = (DatabaseService.MyBinder) binder;
        databaseService = b.getService();
        refreshUI(databaseService.getDataHashMap(), databaseService.getConfigs());
        System.out.println("Az adatbázis szolgáltatáshoz csatlakozott");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        databaseService = null;
        System.out.println("Az adatbázis szolgáltatással a kapcsolat megszakadt.");
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
