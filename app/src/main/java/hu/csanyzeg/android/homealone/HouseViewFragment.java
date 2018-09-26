package hu.csanyzeg.android.homealone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import java.util.ArrayList;
import java.util.HashMap;

import hu.csanyzeg.android.homealone.Data.BoolData;
import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Data.NumberData;
import hu.csanyzeg.android.homealone.UI.BoolImageView;
import hu.csanyzeg.android.homealone.UI.NumberView;


public class HouseViewFragment extends SensorViewFragment {


    AbsoluteLayout absoluteLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_house_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        absoluteLayout = getView().findViewById(R.id.layout);
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
                            if (bundle.getString(DatabaseService.BR_DATA_ID) != null) {
                                Data d = databaseService.getDataHashMap().get(bundle.getString(DatabaseService.BR_DATA_ID));

                                if (d instanceof NumberData) {
                                    NumberData n = (NumberData)d;
                                    System.out.println(d.getConfig().display + " (Number): " + d.currentValue());
                                }
                                if (d instanceof BoolData) {
                                    BoolData b = (BoolData)d;
                                    System.out.println(d.getConfig().display + " (Bool): " + b.currentValue());
                                }
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
        for (Data d:dataHashMap.values()) {
            d.currentValue();
            if (d instanceof NumberData) {
                NumberData n = (NumberData)d;
                System.out.println(d.getConfig().display + " (Number): " + n.currentValue());
                System.out.println(d.getGraphEntries());
                NumberView numberView = new NumberView(getContext());
                numberView.setValue(n.currentValue());
                numberView.setSuffix(n.getConfig().suffix);
                numberView.setDecimal(n.getConfig().precision);
                numberView.setMultiLine(false);
                absoluteLayout.addView(numberView);
            }
            if (d instanceof BoolData) {
                BoolData b = (BoolData)d;
                if (d.getConfig().pozX != null && d.getConfig().pozY != null) {
                    BoolImageView boolView = new BoolImageView(getContext());
                    boolView.setFalseImage(R.drawable.lampaoff);//TODO: Itt egy http url-t kellene átadni!
                    boolView.setTrueImage(R.drawable.lampaon);//TODO: Itt egy http url-t kellene átadni, amit majd a boolView tölt le!
                    System.out.println(d.getConfig().pozX);
                    boolView.setLeft(d.getConfig().pozX);
                    boolView.setTop(d.getConfig().pozY);
                    boolView.setValue(b.currentValue());
                    //System.out.println(d.getConfig().display + " (Bool): " + b.currentValue());
                    //System.out.println(d.getGraphEntries());
                    boolView.setVisibility(View.GONE);
                    absoluteLayout.addView(boolView);

                    boolView.setConfig(b.getConfig());

                }
                //absoluteLayout.updateViewLayout(boolView, );
            }
        }

    }

    @Override
    public void setDatabaseService(DatabaseService databaseService) {
        super.setDatabaseService(databaseService);
        databaseService.getDataHashMap().values();
        refreshUI(databaseService.getDataHashMap(), databaseService.getConfigs());
       /** System.out.println(databaseService.getDataHashMap().get("C8").getConfig());
        belsohom.setText(databaseService.getDataHashMap().get("C8").currentValue() + "");
        if (databaseService.getDataHashMap().get("X1") instanceof BoolData) {
            reflektor.setChecked(((BoolData) databaseService.getDataHashMap().get("X1")).currentValue());
        }*/
    }
}
