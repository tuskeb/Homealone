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
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

import hu.csanyzeg.android.homealone.Data.BoolData;
import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Data.NumberData;
import hu.csanyzeg.android.homealone.UI.BoolImageView;
import hu.csanyzeg.android.homealone.UI.HttpImageView;
import hu.csanyzeg.android.homealone.UI.NumberView;


public class HouseViewFragment extends SensorViewFragment {


    AbsoluteLayout absoluteLayout;
    HttpImageView mapImageView;

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
                                    //System.out.println(d.getConfig().display + " (Number): " + d.currentValue());
                                }
                                if (d instanceof BoolData) {
                                    BoolData b = (BoolData)d;
                                    //System.out.println(d.getConfig().display + " (Bool): " + b.currentValue());
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
    public void refreshUI(final HashMap<String, Data> dataHashMap, final ArrayList<Config> configs) {
        double parentWidth = absoluteLayout.getWidth();
        double parentHeight = absoluteLayout.getHeight();
        if (parentHeight==0.0 || parentWidth == 0.0){ //Abban az esetben, ha előbb frissítene, minthogy megjelenik:)
            getView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshUI(dataHashMap,configs);
                }
            }, 100); //Ezt ki lehet várni, talán...
            return;
        }
        absoluteLayout.removeAllViews();
        AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(absoluteLayout.getWidth(), absoluteLayout.getHeight(), 0, 0);
        absoluteLayout.addView(mapImageView = new HttpImageView(getContext()), lp);
        mapImageView.setURL(Config.map);
        System.out.println(" Szélesség: " + parentWidth);
        System.out.println(" Magasság: " + parentHeight);
        for (Data d:dataHashMap.values()) {
            //d.currentValue();
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
                    //System.out.println(d.getConfig().pozX);

                    Config c = d.getConfig();
                    AbsoluteLayout.LayoutParams layoutParams = new AbsoluteLayout.LayoutParams((int)(c.width.doubleValue()*parentWidth), (int)(c.height.doubleValue()*parentHeight), (int)(c.pozX.doubleValue()*parentWidth), (int)(c.pozY.doubleValue()*parentHeight));
                    boolView.setValue(b.currentValue());
                    //System.out.println(d.getConfig().display + " (Bool): " + b.currentValue());
                    //System.out.println(d.getGraphEntries());
                    //boolView.setVisibility(View.GONE);
                    absoluteLayout.addView(boolView, layoutParams);

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
