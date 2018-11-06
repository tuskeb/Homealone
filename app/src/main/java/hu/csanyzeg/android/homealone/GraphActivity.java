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

import hu.csanyzeg.android.homealone.Data.BoolData;
import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Data.Entry;
import hu.csanyzeg.android.homealone.Data.NamedArrayList;
import hu.csanyzeg.android.homealone.Data.NumberData;
import hu.csanyzeg.android.homealone.Data.OnDataUpdateListener;
import hu.csanyzeg.android.homealone.Data.SensorRecord;
import hu.csanyzeg.android.homealone.UI.GraphView;
import hu.csanyzeg.android.homealone.UI.NumberGraphView;
import hu.csanyzeg.android.homealone.Utils.HttpDownloadUtil;
import hu.csanyzeg.android.homealone.Utils.ParseHistoryDataXML;

public class GraphActivity extends AppCompatActivity implements ServiceConnection {

    TextView intervalTextView;
    GraphView graphView;
    SeekBar intervalSeekBar;
    RelativeLayout progressBar;
    SeekBar timeSeekBar;
    TextView timeTextView;
    protected int progressCount = 0;

    protected int timeInterval;
    protected long timeIntervalMs;
    protected ArrayList<Data> numberDataArrayList = new ArrayList<>();
    protected Config config = null;
    protected DatabaseService databaseService = null;
    protected long startGraphTime;

    protected void setTimeInterval(int value){
        switch (value){
            case 0:
                timeIntervalMs = 60L*60L*1000L;
                intervalTextView.setText("Óra");
                break;
            case 1:
                timeIntervalMs = 3L*60L*60L*1000L;
                intervalTextView.setText("3 óra");
                break;
            case 2:
                timeIntervalMs = 6L*60L*60L*1000L;
                intervalTextView.setText("6 Óra");
                break;
            case 3:
                timeIntervalMs = 12L*60L*60L*1000L;
                intervalTextView.setText("12 Óra");
                break;
            case 4:
                timeIntervalMs = 24L*60L*60L*1000L;
                intervalTextView.setText("Nap");
                break;
            case 5:
                timeIntervalMs = 7L*24L*60L*60L*1000L;
                intervalTextView.setText("Hét");
                break;
        }
        timeInterval = value;
        //System.out.println(timeIntervalMs);
        intervalSeekBar.setProgress(value);

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
        intervalSeekBar = findViewById(R.id.agIntervaLengthSB);

        intervalTextView = findViewById(R.id.agIntervaLengthTV);
        progressBar = findViewById(R.id.agPR);
        timeSeekBar = findViewById(R.id.agIntervalSB);
        timeTextView = findViewById(R.id.agIntervalTV);


        setTimeInterval(1);

        intervalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        timeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateStartGraphTime();
                timeTextView.setText(new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date(startGraphTime)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                refreshData();
            }
        });
    }


    protected void updateStartGraphTime(){
        long end = databaseService.getRpiCurrentDate().getTime() - timeIntervalMs;
        startGraphTime = Config.timeMin + (long)((double)(end - Config.timeMin) * (double)timeSeekBar.getProgress() / (double)timeSeekBar.getMax());
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
                final Data data;
                if (cfg.isSwitch()){
                    data = new BoolData(cfg) {
                        @Override
                        public Date getFromDate() {
                            if (databaseService.getRpiCurrentDate() == null) return null;
                            return new Date(startGraphTime);

                        }

                        @Override
                        public Date getToDate() {
                            if (databaseService.getRpiCurrentDate() == null) return null;
                            return new Date(startGraphTime  + getTimeIntervalMs());
                        }
                    };
                }else{
                    data = new NumberData(cfg) {
                        @Override
                        public Date getFromDate() {
                            if (databaseService.getRpiCurrentDate() == null) return null;
                            return new Date(startGraphTime);

                        }

                        @Override
                        public Date getToDate() {
                            if (databaseService.getRpiCurrentDate() == null) return null;
                            return new Date(startGraphTime  + getTimeIntervalMs());
                        }
                    };
                }



                data.setOnDataUpdateListener(new OnDataUpdateListener() {
                    @Override
                    public void onBeginUpdate(Data data) {
                        //graphView.getEntryList().remove(data.getGraphEntries());
                    }

                    @Override
                    public void onEndUpdate(Data data) {

                        //graphView.getEntryList().add(data.getGraphEntries());
                        //graphView.invalidate();
                    }
                });
                if (cfg.isSensor()) {
                    numberDataArrayList.add(data);
                }
            }
        }
        if (numberDataArrayList.get(0).getConfig().isSwitch()){
            graphView = findViewById(R.id.agBoolGraph);
        }else{
            graphView = findViewById(R.id.agNumberGraph);
        }

        //System.out.println(config);
        setTitle(config.display);

        graphView.setBackgroundColor(Color.WHITE);
        graphView.invalidate();
        if (graphView instanceof NumberGraphView) {
            ((NumberGraphView) graphView).setDecimal(config.precision);
            graphView.setMax(config.max);
            graphView.setMin(config.min);
            graphView.setUnit(config.suffix);
        }

        graphView.getEntryList().clear();
        for (Data n : numberDataArrayList) {
            graphView.getEntryList().add(n.getGraphEntries());

        }
        //System.out.println(numberData.getGraphEntries().size());
    }


    public void refreshData() {
        setProgress(true);
        updateStartGraphTime();
        timeTextView.setText(new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date(startGraphTime)));
        graphView.setTimeMax(startGraphTime+timeIntervalMs);
        graphView.setTimeMin(startGraphTime);
        //timeTextView.setText(new Date(Config.timeMin).toString() + " - " + new Date(databaseService.getRpiCurrentDate().getTime() - timeIntervalMs));


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
            for (final Data n : numberDataArrayList) {
                Date startDataTime = n.getFromDate();
                Date stopDataTime = n.getToDate();

                if (!databaseService.isRandomData()) {
                    //System.out.println("Download startDataTime " + startDataTime);
                    //System.out.println("Download stopDataTime " + stopDataTime);
                    SimpleDateFormat simpleDateFormat = ParseHistoryDataXML.getDateParser();

                    HashMap<String, String> get = new HashMap<>();
                    get.put("format", "xml");
                    get.put("SID", Config.session_id);
                    get.put("field", n.getConfig().id);
                    get.put("full", "1");
                    get.put("start", simpleDateFormat.format(startDataTime));
                    get.put("stop", simpleDateFormat.format(stopDataTime));

                    new HttpDownloadUtil() {
                        @Override
                        public void onDownloadStart() {
                            progressCount++;
                            //System.out.println("Start downloading history...");
                        }

                        @Override
                        public void onDownloadComplete(StringBuilder stringBuilder) {
                            progressCount--;
                            if (stringBuilder == null) {
                                return;
                            }
                            n.getGraphEntries().clear();
                            ArrayList<SensorRecord> sensorRecords = ParseHistoryDataXML.parse(stringBuilder.toString());
                            //System.out.println(sensorRecords);
                            n.updateFromSensorRecords(sensorRecords, databaseService.getRpiCurrentDate());
                            NamedArrayList<Entry> asd = databaseService.getDataHashMap().get(n.getConfig().id).getGraphEntries();
                            n.getGraphEntries().add((Entry) asd.get(asd.size() - 1));
                            //generateDownloadCompleteNotification();
                            if (progressCount == 0) {
                                setProgress(false);
                                graphView.invalidate();
                            }
                        }
                    }.download(new HttpDownloadUtil.HttpRequestInfo(databaseService.getServerURL(), HttpDownloadUtil.Method.POST, get, get));
                } else {
                    ArrayList<SensorRecord> sensorRecords = new ArrayList<>();
                    for (int i = 0; i < 100; i++) {
                        sensorRecords.add(databaseService.randomSensorRecordField(n));
                    }
                    n.getGraphEntries().clear();
                    n.updateFromSensorRecords(sensorRecords, Calendar.getInstance().getTime());
                    NamedArrayList<Entry> asd = databaseService.getDataHashMap().get(n.getConfig().id).getGraphEntries();
                    n.getGraphEntries().add((Entry) asd.get(asd.size() - 1));
                    if (progressCount == 0) {
                        setProgress(false);
                        graphView.invalidate();
                     }
                }
            }
            //n.updateFromRandom(databaseService.getRpiCurrentDate());
        }
    }

    public void setProgress(boolean b){
        if (b){
            graphView.setAlpha(0.2f);
            graphView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }else
        {

            progressBar.setVisibility(View.GONE);
            graphView.setVisibility(View.VISIBLE);
            graphView.setAlpha(1f);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        DatabaseService.MyBinder b = (DatabaseService.MyBinder) binder;
        databaseService = b.getService();
        graphView = findViewById(R.id.agNumberGraph);
        updateStartGraphTime();
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
