package hu.csanyzeg.android.homealone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;


public class HouseViewFragment extends SensorViewFragment {


    TextView belsohom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_house_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        belsohom = getView().findViewById(R.id.belsohomerseklet);
    }

    @Override
    public BroadcastReceiver getDatabaseBroadcastReceiver() {
        return null;
    }

    @Override
    public void refreshUI(HashMap<String, Data> dataHashMap, ArrayList<Config> configs) {

    }

    @Override
    public void setDatabaseService(DatabaseService databaseService) {
        super.setDatabaseService(databaseService);
        System.out.println(databaseService.getDataHashMap().get("C8").getConfig().display);
        belsohom.setText(databaseService.getDataHashMap().get("C8").currentValue() + "");
    }
}
