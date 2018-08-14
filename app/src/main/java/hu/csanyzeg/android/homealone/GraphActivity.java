package hu.csanyzeg.android.homealone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Data.NumberData;
import hu.csanyzeg.android.homealone.Data.OnDataUpdateListener;
import hu.csanyzeg.android.homealone.UI.NumberGraphView;

public class GraphActivity extends AppCompatActivity implements ServiceConnection {

    TextView textView;
    NumberGraphView numberGraphView;
    SeekBar seekBar;
    RelativeLayout progressBar;

    private int timeInterval;
    private long timeIntervalMs;
    private ArrayList<NumberData> numberDataArrayList = new ArrayList<>();
    private Config config = null;
    private DatabaseService databaseService = null;

    protected void setTimeInterval(int value){
        switch (value){
            case 0:
                timeIntervalMs = 60L*60L*1000L;
                textView.setText("Utolsó óra");
                break;
            case 1:
                timeIntervalMs = 24L*60L*60L*1000L;
                textView.setText("Utolsó nap");
                break;
            case 2:
                timeIntervalMs = 31L*24L*60L*60L*1000L;

                textView.setText("Utolsó hónap");
                break;
            case 3:
                timeIntervalMs = 365L*24L*60L*60L*1000L;
                textView.setText("Utolsó év");
                break;
            case 4:
                timeIntervalMs = 5L*365L*24L*60L*60L*1000L;
                textView.setText("Utolsó 5 év");
                break;
        }
        timeInterval = value;
        System.out.println(timeIntervalMs);
        seekBar.setProgress(value);
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public long getTimeIntervalMs() {
        return timeIntervalMs;
    }

    public void setTimeIntervalMs(long timeIntervalMs) {
        this.timeIntervalMs = timeIntervalMs;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        seekBar = findViewById(R.id.agSB);
        numberGraphView = findViewById(R.id.agG);
        textView = findViewById(R.id.agTV);
        progressBar = findViewById(R.id.agPR);

        setTimeInterval(1);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setTimeInterval(seekBar.getProgress());
                for (NumberData n : numberDataArrayList) {
                    n.updateFromRandom(databaseService.getRpiCurrentDate());
                }
            }
        });
    }

    public void refreshUI(){
        ArrayList<Config> configs = databaseService.getConfigs();




        String iid = getIntent().getExtras().getString(DatabaseService.BR_DATA_ID);
        for (Config cfg : configs) {
            if ((cfg.parent!= null && cfg.parent.equals(iid)) || cfg.id.equals(iid)) {

                if (cfg.id.equals(iid)) {
                    config = cfg;
                }

                final NumberData numberData = new NumberData(cfg) {
                    @Override
                    public Date getFromDate() {
                        return new Date(databaseService.getRpiCurrentDate().getTime() - getTimeIntervalMs());

                    }

                    @Override
                    public Date getToDate() {
                        return new Date(databaseService.getRpiCurrentDate().getTime());
                    }
                };


                numberData.setOnDataUpdateListener(new OnDataUpdateListener() {
                    @Override
                    public void onBeginUpdate(Data data) {
                        numberGraphView.setAlpha(0.2f);
                        numberGraphView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        numberGraphView.getEntryList().remove(data.getGraphEntries());
                    }

                    @Override
                    public void onEndUpdate(Data data) {

                        numberGraphView.getEntryList().add(data.getGraphEntries());
                        numberGraphView.invalidate();
                        progressBar.setVisibility(View.GONE);
                        numberGraphView.setVisibility(View.VISIBLE);
                        /*try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                        numberGraphView.setAlpha(1f);
                    }
                });
                if (cfg.isSensor()) {
                    numberDataArrayList.add(numberData);
                }

            }
        }


        //System.out.println(config);
        setTitle(config.display);

        numberGraphView.setBackgroundColor(Color.WHITE);
        numberGraphView.setMax(config.max);
        numberGraphView.setMin(config.min);
        numberGraphView.invalidate();
        numberGraphView.setDecimal(config.precision);
        numberGraphView.setUnit(config.suffix);
        numberGraphView.getEntryList().clear();
        for (NumberData n : numberDataArrayList) {
            numberGraphView.getEntryList().add(n.getGraphEntries());
        }
        //System.out.println(numberData.getGraphEntries().size());
        for (NumberData n : numberDataArrayList) {
            n.updateFromRandom(databaseService.getRpiCurrentDate());
        }

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        DatabaseService.MyBinder b = (DatabaseService.MyBinder) binder;
        databaseService = b.getService();
        refreshUI();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }


}
