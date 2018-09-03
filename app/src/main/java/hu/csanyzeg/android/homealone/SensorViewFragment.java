package hu.csanyzeg.android.homealone;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;

/**
 * Created by tanulo on 2018. 09. 03..
 */

public abstract class SensorViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    protected DatabaseService databaseService = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    abstract public BroadcastReceiver getDatabaseBroadcastReceiver();

    public DatabaseService getDatabaseService() {
        return databaseService;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    abstract public void refreshUI(HashMap<String, Data> dataHashMap, ArrayList<Config> configs);


    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

}