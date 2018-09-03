package hu.csanyzeg.android.homealone;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Interfaces.Sensor;
import hu.csanyzeg.android.homealone.Utils.FullSensorViewInflater;

//import hu.csanyzeg.android.homelone.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GraphListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphListFragment extends SensorViewFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ViewGroup layout;
    private ViewGroup layout2;
    private ArrayList<Sensor> sensors = new ArrayList<>();
    private TextView rpiTimeTV;
    private TextView distanceTV;
    private TextView alarmTV;
    private LinearLayout noconnection;


    private void setConnectingProgressView(boolean b){
        if (b){
            //if (noconnection.getVisibility()!=View.VISIBLE) {
            noconnection.setVisibility(View.VISIBLE);
            alarmTV.setVisibility(View.GONE);
            rpiTimeTV.setVisibility(View.GONE);
            distanceTV.setVisibility(View.GONE);
            //}
        }else{
            //if (noconnection.getVisibility()!=View.GONE) {
            noconnection.setVisibility(View.GONE);
            alarmTV.setVisibility(View.VISIBLE);
            rpiTimeTV.setVisibility(View.VISIBLE);
            distanceTV.setVisibility(View.VISIBLE);
            //}
        }
    }


    private BroadcastReceiver databaseBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                //System.out.println("Broadcast receiver message: " + bundle.getInt(DatabaseService.BR_MESSAGE));
                switch (bundle.getInt(DatabaseService.BR_MESSAGE)){
                    case DatabaseService.BR_DATA_UPDATE:
                        //System.out.println(databaseService.getDataHashMap());
                        int hash = bundle.getInt(DatabaseService.BR_OBJECT_HASH);
                        setConnectingProgressView(false);
                        for(Sensor f : sensors) {
                            if (null != f.getData().get(bundle.getString(DatabaseService.BR_DATA_ID))) {
                                f.updateData();
                            }
                        }
                        break;
                    case DatabaseService.BR_CONFIG_UPDATE:
                        //databaseService.getDataHashMap();
                        if (databaseService != null && databaseService.getConfigs()!=null) {
                            setConnectingProgressView(false);
                            refreshUI(databaseService.getDataHashMap(), databaseService.getConfigs());
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
                        setConnectingProgressView(true);
                        break;
                    case DatabaseService.BR_DOWNLOAD_COMPLETE:
                        setConnectingProgressView(false);
                        break;
                    case DatabaseService.BR_RPI_TIME_UPDATE:
                        break;
                }
            }
        }
    };


    @Override
    public BroadcastReceiver getDatabaseBroadcastReceiver() {
        return databaseBroadcastReceiver;
    }

    @Override
    public void setDatabaseService(DatabaseService databaseService) {
        super.setDatabaseService(databaseService);
        if (databaseService!=null) {
            refreshUI(databaseService.getDataHashMap(), databaseService.getConfigs());
            System.out.println("Az adatbázis szolgáltatáshoz csatlakozott");
        }else{
            System.out.println("Az adatbázis szolgáltatással a kapcsolat megszakadt.");
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GraphListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GraphListFragment newInstance(String param1, String param2) {
        GraphListFragment fragment = new GraphListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph_list, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layout = getView().findViewById(R.id.mainLayout);
        layout2 = getView().findViewById(R.id.mainLayout2);
        rpiTimeTV = getView().findViewById(R.id.rpiTimeTV);
        distanceTV = getView().findViewById(R.id.distanceTV);
        alarmTV = getView().findViewById(R.id.alarmTV);
        noconnection = getView().findViewById(R.id.noconnection);


        rpiTimeTV.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity()!= null) {
                    if (databaseService != null) {

                        if (databaseService.getRpiCurrentDate() != null) {
                            rpiTimeTV.setText(getString(R.string.hometime) + databaseService.getRpiCurrentDate());
                        } else {
                            rpiTimeTV.setText(R.string.hometimenotfound);
                        }

                    }
                    rpiTimeTV.postDelayed(this, 980);
                }
            }
        }, 980);

        if (databaseService != null){
            setConnectingProgressView(false);
            refreshUI(databaseService.getDataHashMap(), databaseService.getConfigs());
        }else{
            setConnectingProgressView(true);
        }
    }

    @Override
    public void refreshUI(HashMap<String, Data> dataHashMap, ArrayList<Config> configs){
        if (layout!= null) {
            layout.removeAllViews();
        }
        if (layout2!= null) {
            layout2.removeAllViews();
        }
        if (configs != null) {
            for (Config config : configs) {
                if (config.isEnabled()) {
                    if (layout.findViewWithTag(config.id) == null) {
                        View sensorView = (View) FullSensorViewInflater.inflate(getContext(), dataHashMap, config.id, databaseService);
                        if (sensorView != null) {
                            sensors.add((Sensor) sensorView);
                            if (layout2 != null && sensors.size() % 2 == 0) {
                                layout2.addView(sensorView);
                            } else {
                                layout.addView(sensorView);
                            }
                            //System.out.println(layout.getHeight());
                            sensorView.setTag(config.id);
                            ((Sensor) sensorView).updateData();

                        }
                    }
                }
            }
        }
    }


}
