package hu.csanyzeg.android.homealone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import hu.csanyzeg.android.homealone.Data.BoolData;
import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;


public class HouseViewFragment extends SensorViewFragment {


    TextView belsohom;
    CheckBox reflektor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_house_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        belsohom = getView().findViewById(R.id.belsohomerseklet);
        reflektor = getView().findViewById(R.id.reflektor);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                //System.out.println("Broadcast receiver message: " + bundle.getInt(DatabaseService.BR_MESSAGE));
                switch (bundle.getInt(DatabaseService.BR_MESSAGE)) {
                    case DatabaseService.BR_DATA_UPDATE:
                        if (databaseService != null) {
                            if (bundle.getString(DatabaseService.BR_DATA_ID) != null && bundle.getString(DatabaseService.BR_DATA_ID).equals("X1")) {
                                reflektor.setChecked(((BoolData) databaseService.getDataHashMap().get("X1")).currentValue());
                                belsohom.setText(databaseService.getDataHashMap().get("C8").currentValue() + "");

                            }
                        }
                        break;
                }
            }
        }
    };

    @Override
    public BroadcastReceiver getDatabaseBroadcastReceiver() {
        return broadcastReceiver;
    }

    @Override
    public void refreshUI(HashMap<String, Data> dataHashMap, ArrayList<Config> configs) {

    }

    @Override
    public void setDatabaseService(DatabaseService databaseService) {
        super.setDatabaseService(databaseService);
        System.out.println(databaseService.getDataHashMap().get("C8").getConfig());
        belsohom.setText(databaseService.getDataHashMap().get("C8").currentValue() + "");
        if (databaseService.getDataHashMap().get("X1") instanceof BoolData) {
            reflektor.setChecked(((BoolData) databaseService.getDataHashMap().get("X1")).currentValue());
        }
    }
}
