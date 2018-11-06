package hu.csanyzeg.android.homealone;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Interfaces.Sensor;
import hu.csanyzeg.android.homealone.Utils.FullSensorViewInflater;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements ServiceConnection{


    //GraphListFragment fragment = new GraphListFragment();

    private FragmentTabHost mTabHost;
    private DatabaseService databaseService = null;

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
/*
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
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("graphview").setIndicator("Grafikon nézet"), GraphListFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("houseview").setIndicator("Ház nézet"), HouseViewFragment.class, null);


        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                updateFragment(s);
            }
        });
        Intent i= new Intent(getApplicationContext(), DatabaseService.class);
        startService(i);

        findViewById(R.id.about_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.options_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OptionsActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.exit_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                alert.setTitle("Kilépés a Homealone alkalmazásból");
                alert.setMessage("Amennyiben bezárja az alkalmazást, nem kap további figyelmeztetéseket. Folytatja a műveletet?");

                alert.setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        databaseService.onDestroy();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });

                alert.setNegativeButton("Nem", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();
            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();
        Intent intent= new Intent(this, DatabaseService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
        //registerReceiver(fragment.getDatabaseBroadcastReceiver(), new IntentFilter(DatabaseService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
        //unregisterReceiver(fragment.getDatabaseBroadcastReceiver());
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        DatabaseService.MyBinder b = (DatabaseService.MyBinder) binder;
        databaseService = b.getService();
        updateFragment(mTabHost.getCurrentTabTag());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        databaseService = null;
        //fragment.setDatabaseService(null);
    }


    public void updateFragment(String tag){
        AsyncTask<String, Integer, Integer> asyncTask = new AsyncTask<String, Integer, Integer>() {
            FragmentManager manager;
            SensorViewFragment myFragment;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                manager = getSupportFragmentManager();
            }

            @Override
            protected Integer doInBackground(String... strings) {
                while (((myFragment = (SensorViewFragment) manager.findFragmentByTag(strings[0]))==null) || (getDatabaseService() == null)){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                registerReceiver(myFragment.getDatabaseBroadcastReceiver(), new IntentFilter(DatabaseService.NOTIFICATION));
                myFragment.setDatabaseService(getDatabaseService());
            }
        };
        asyncTask.execute(tag);
    }
}
