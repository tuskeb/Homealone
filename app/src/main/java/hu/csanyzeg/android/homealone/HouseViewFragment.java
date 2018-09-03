package hu.csanyzeg.android.homealone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;


public class HouseViewFragment extends SensorViewFragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_house_view, container, false);
    }

    @Override
    public BroadcastReceiver getDatabaseBroadcastReceiver() {
        return null;
    }

    @Override
    public void refreshUI(HashMap<String, Data> dataHashMap, ArrayList<Config> configs) {

    }
}
