package hu.csanyzeg.android.homealone.Utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import hu.csanyzeg.android.homealone.Data.AlarmEvent;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Data.SensorRecord;
import hu.csanyzeg.android.homealone.DatabaseService;
import hu.csanyzeg.android.homealone.Interfaces.Sensor;
import hu.csanyzeg.android.homealone.UI.AlarmFullBoolSensorView;
import hu.csanyzeg.android.homealone.UI.AlarmMaxFullSensorFullNumberSensorView;
import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.UI.AlarmMinMaxFullSensorFullNumberSensorView;
import hu.csanyzeg.android.homealone.UI.AlarmMinFullSensorFullNumberSensorView;
import hu.csanyzeg.android.homealone.UI.FullBoolSensorView;
import hu.csanyzeg.android.homealone.UI.FullNumberSensorView;
import hu.csanyzeg.android.homealone.UI.InputFullNumberSensorView;
import hu.csanyzeg.android.homealone.UI.OnBoolValueChangeListener;
import hu.csanyzeg.android.homealone.UI.OnValueChangedListener;
import hu.csanyzeg.android.homealone.UI.SwitchFullBoolSensorView;

/**
 * Created by tanulo on 2018. 06. 27..
 */

public class FullSensorViewInflater {
    public static Sensor inflate(Context context, HashMap<String, Data> dataHashMap, String configID, final DatabaseService databaseService){
        Sensor sensorView = null;
        final Data data = dataHashMap.get(configID);
        if (data == null){
            return null;
        }
        final Config config = data.getConfig();
        if (!config.isVisible()){
            return null;
        }
        //System.out.println(config);
        if (!data.getConfig().isSwitch()) {
            if (data.getConfig().isWrite()){
                sensorView = new InputFullNumberSensorView(context);
                ((InputFullNumberSensorView)sensorView).setOnValueChangedListener(new OnValueChangedListener() {
                    @Override
                    public void doubleValueChanged(final Double value) {
                        HashMap<String, String> get = new HashMap<>();
                        get.put("format", "xml");
                        get.put(config.id, String.format("%.2f" , value).replace(",","."));
                        new HttpDownloadUtil() {
                            @Override
                            public void onDownloadStart() {
                                System.out.println(" Send " + config.id + " value " + value);
                            }

                            @Override
                            public void onDownloadComplete(StringBuilder stringBuilder) {
/*
                                if (stringBuilder == null) {
                                    return;
                                }
                                ArrayList<SensorRecord> sensorRecords = ParseCurrentDataXML.parse(stringBuilder.toString());
                                if (sensorRecords.size() > 0) {
                                    data.updateFromSensorRecords(sensorRecords, sensorRecords.get(0).ts);
                                }*/
                            }
                        }.download(new HttpDownloadUtil.HttpRequestInfo(databaseService.getServerURL(), HttpDownloadUtil.Method.GET, get, get));
                    }
                });
            }else {
                if (config.getAlarmEvent() != AlarmEvent.never) {
                    switch (config.getAlarmType()) {
                        case max:
                            sensorView = new AlarmMaxFullSensorFullNumberSensorView(context);
                            break;
                        case min:
                            sensorView = new AlarmMinFullSensorFullNumberSensorView(context);
                            break;
                        case minmax:
                            sensorView = new AlarmMinMaxFullSensorFullNumberSensorView(context);
                            break;
                    }
                } else {
                    sensorView = new FullNumberSensorView(context);
                }
            }
            sensorView.setConfig(config);
            if (config.isSensor()) {
                sensorView.getData().put(config.id, data);
            }
        }else {
            if (config.alarmSet) {
                sensorView = new AlarmFullBoolSensorView(context);
            } else {
                if (config.isWrite()) {
                    sensorView = new SwitchFullBoolSensorView(context);
                    ((SwitchFullBoolSensorView)sensorView).setOnCheckChangeListener(new OnBoolValueChangeListener() {
                        @Override
                        public void onChangeValueTrue() {
                            HashMap<String, String> get = new HashMap<>();
                            get.put("format", "xml");
                            get.put(config.id, "1");
                            new HttpDownloadUtil() {
                                @Override
                                public void onDownloadStart() {
                                    System.out.println(" Send " + config.id + " true ");
                                }

                                @Override
                                public void onDownloadComplete(StringBuilder stringBuilder) {
/*
                                    if (stringBuilder == null) {
                                        return;
                                    }
                                    ArrayList<SensorRecord> sensorRecords = ParseCurrentDataXML.parse(stringBuilder.toString());
                                    if (sensorRecords.size() > 0) {
                                        data.updateFromSensorRecords(sensorRecords, sensorRecords.get(0).ts);
                                    }*/
                                }
                            }.download(new HttpDownloadUtil.HttpRequestInfo(databaseService.getServerURL(), HttpDownloadUtil.Method.GET, get, get));
                        }

                        @Override
                        public void onChangeValueFalse() {
                            HashMap<String, String> get = new HashMap<>();
                            get.put("format", "xml");
                            get.put(config.id, "0");
                            new HttpDownloadUtil() {
                                @Override
                                public void onDownloadStart() {
                                    System.out.println(" Send " + config.id + " false ");
                                }

                                @Override
                                public void onDownloadComplete(StringBuilder stringBuilder) {
/*
                                    if (stringBuilder == null) {
                                        return;
                                    }
                                    ArrayList<SensorRecord> sensorRecords = ParseCurrentDataXML.parse(stringBuilder.toString());
                                    if (sensorRecords.size() > 0) {
                                        data.updateFromSensorRecords(sensorRecords, sensorRecords.get(0).ts);
                                    }*/
                                }
                            }.download(new HttpDownloadUtil.HttpRequestInfo(databaseService.getServerURL(), HttpDownloadUtil.Method.GET, get, get));
                        }

                        @Override
                        public void onChangeValueNull() {

                        }
                    });

                } else {
                    sensorView = new FullBoolSensorView(context);
                }
            }
            sensorView.setConfig(config);
            if (config.isSensor()) {
                sensorView.getData().put(config.id, data);
            }

        }
        for (Data c : dataHashMap.values()) {
            if (c.getConfig().parent !=null && dataHashMap.get(c.getConfig().parent) != null && !c.getConfig().isVisible() && c.getConfig().parent.equals(sensorView.getSensorId())) {
                //System.out.println(c.getConfig().parent);
                sensorView.getData().put(c.getConfig().id, dataHashMap.get(c.getConfig().id));
            }
        }
        return sensorView;
    }
}
