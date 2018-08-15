package hu.csanyzeg.android.homealone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Data.NumberData;
import hu.csanyzeg.android.homealone.Data.OnDataUpdateListener;
import hu.csanyzeg.android.homealone.Data.SensorRecord;
import hu.csanyzeg.android.homealone.UI.NumberGraphView;
import hu.csanyzeg.android.homealone.Utils.HttpDownloadUtil;
import hu.csanyzeg.android.homealone.Utils.ParseHistoryDataXML;

public class GraphActivity extends AppCompatActivity implements ServiceConnection {

    TextView textView;
    NumberGraphView numberGraphView;
    SeekBar seekBar;
    RelativeLayout progressBar;
    int progressCount = 0;

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
                timeIntervalMs = 7L*24L*60L*60L*1000L;
                textView.setText("Utolsó hét");
                break;
            case 3:
                timeIntervalMs = 31L*24L*60L*60L*1000L;

                textView.setText("Utolsó hónap");
                break;
            case 4:
                timeIntervalMs = 365L*24L*60L*60L*1000L;
                textView.setText("Utolsó év");
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
                refreshData();
            }
        });
    }

    public void refreshUI() {
        ArrayList<Config> configs = databaseService.getConfigs();
        numberDataArrayList.clear();

        String iid = getIntent().getExtras().getString(DatabaseService.BR_DATA_ID);
        for (Config cfg : configs) {
            if ((cfg.parent != null && cfg.parent.equals(iid)) || cfg.id.equals(iid)) {

                if (cfg.id.equals(iid)) {
                    config = cfg;
                }

                final NumberData numberData = new NumberData(cfg) {
                    @Override
                    public Date getFromDate() {
                        if (databaseService.getRpiCurrentDate() == null) return null;
                        return new Date(databaseService.getRpiCurrentDate().getTime() - getTimeIntervalMs());

                    }

                    @Override
                    public Date getToDate() {
                        if (databaseService.getRpiCurrentDate() == null) return null;
                        return new Date(databaseService.getRpiCurrentDate().getTime());
                    }
                };


                numberData.setOnDataUpdateListener(new OnDataUpdateListener() {
                    @Override
                    public void onBeginUpdate(Data data) {
                        //numberGraphView.getEntryList().remove(data.getGraphEntries());
                    }

                    @Override
                    public void onEndUpdate(Data data) {

                        //numberGraphView.getEntryList().add(data.getGraphEntries());
                        //numberGraphView.invalidate();
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
    }
    public void refreshData() {
        setProgress(true);

        if (databaseService.getRpiCurrentDate() == null) {
            AsyncTask asyncTask = new AsyncTask() {
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    refreshUI();
                }

                @Override
                protected Object doInBackground(Object[] objects) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        else {
            for (final NumberData n : numberDataArrayList) {
                Date startDataTime = n.getFromDate();
                Date stopDataTime = n.getToDate();
                System.out.println("Download startDataTime " + startDataTime);
                System.out.println("Download stopDataTime " + stopDataTime);
                SimpleDateFormat simpleDateFormat = ParseHistoryDataXML.getDateParser();

                HashMap<String, String> get = new HashMap<>();
                get.put("format", "xml");
                get.put("start", simpleDateFormat.format(startDataTime));
                get.put("stop", simpleDateFormat.format(stopDataTime));
                new HttpDownloadUtil() {
                    @Override
                    public void onDownloadStart() {
                        progressCount++;
                        System.out.println("Start downloading history...");
                    }

                    @Override
                    public void onDownloadComplete(StringBuilder stringBuilder) {
                        progressCount--;
                        if (stringBuilder == null) {
                            return;
                        }
                        ArrayList<SensorRecord> sensorRecords = ParseHistoryDataXML.parse(stringBuilder.toString());
                        //System.out.println(sensorRecords);
                        n.updateFromSensorRecords(sensorRecords, databaseService.getRpiCurrentDate());
                        //generateDownloadCompleteNotification();
                        if (progressCount==0) {
                            setProgress(false);
                            numberGraphView.invalidate();
                        }
                    }
                }.download(new HttpDownloadUtil.HttpRequestInfo(databaseService.getServerURL(), HttpDownloadUtil.Method.POST, get, get));
            }
            //n.updateFromRandom(databaseService.getRpiCurrentDate());
        }
    }

    public void setProgress(boolean b){
        if (b){
            numberGraphView.setAlpha(0.2f);
            numberGraphView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }else
        {

            progressBar.setVisibility(View.GONE);
            numberGraphView.setVisibility(View.VISIBLE);
            numberGraphView.setAlpha(1f);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        DatabaseService.MyBinder b = (DatabaseService.MyBinder) binder;
        databaseService = b.getService();
        refreshUI();
        refreshData();
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
