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
import hu.csanyzeg.android.homealone.Interfaces.Sensor;
import hu.csanyzeg.android.homealone.UI.BoolImageView;
import hu.csanyzeg.android.homealone.UI.HttpImageView;
import hu.csanyzeg.android.homealone.UI.NumberImageView;
import hu.csanyzeg.android.homealone.UI.NumberView;
import hu.csanyzeg.android.homealone.Utils.HttpByteArrayDownloadUtil;


public class HouseViewFragment extends SensorViewFragment {


    AbsoluteLayout absoluteLayout;
    HttpImageView mapImageView;
    private ArrayList<Sensor> sensors = new ArrayList<>();

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
                                for(Sensor f : sensors) {
                                    if (null != f.getData().get(bundle.getString(DatabaseService.BR_DATA_ID))) {
                                        f.updateData();
                                    }
                                }
                                /*
                                if (d instanceof NumberData) {
                                    NumberData n = (NumberData)d;
                                    //System.out.println(d.getConfig().display + " (Number): " + d.currentValue());
                                }
                                if (d instanceof BoolData) {
                                    BoolData b = (BoolData)d;
                                    //System.out.println(d.getConfig().display + " (Bool): " + b.currentValue());
                                }
                                */
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
        if (absoluteLayout.getWidth()==0.0 || absoluteLayout.getHeight() == 0.0){ //Abban az esetben, ha előbb frissítene, minthogy megjelenik:)
            getView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshUI(dataHashMap,configs);
                }
            }, 100); //Ezt ki lehet várni, talán...
            return;
        }
        absoluteLayout.removeAllViews();
        mapImageView = new HttpImageView(getContext()){
            @Override
            public void onImageDownloadComplete(HttpByteArrayDownloadUtil.Result bytes) {
                super.onImageDownloadComplete(bytes);
                double scaleX=(double)absoluteLayout.getWidth() / (double)getDrawable().getBounds().width();
                double scaleY=(double)absoluteLayout.getHeight() / (double)getDrawable().getBounds().height();
                    System.out.println(" Drawable pozW: " + scaleX);
                    System.out.println(" Drawable pozH: " + scaleY);
                    double scale = scaleX<scaleY?scaleX:scaleY;
                double parentWidth = ((double)getDrawable().getBounds().width()*scale);
                double parentHeight = ((double)getDrawable().getBounds().height()*scale);
                double offsetX = (absoluteLayout.getWidth() - parentWidth) / 2.0;
                double offsetY = (absoluteLayout.getHeight() - parentHeight) / 2.0;
                AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams((int)parentWidth, (int)parentHeight, (int)offsetX, (int)offsetY);
                setLayoutParams(lp);

                for (Data d:dataHashMap.values()) {
                    Config c = d.getConfig();
                    AbsoluteLayout.LayoutParams layoutParams = new AbsoluteLayout.LayoutParams((int)(c.pozW.doubleValue()*parentWidth), (int)(c.pozH.doubleValue()*parentHeight), (int)(c.pozX.doubleValue()*parentWidth) + (int)offsetX, (int)(c.pozY.doubleValue()*parentHeight) + (int)offsetY);
                    if (d instanceof NumberData) {
                        NumberData n = (NumberData)d;
                        NumberImageView numberView = new NumberImageView(getContext());
                        numberView.addData(n.getConfig().id, n);
                        //numberView.setSuffix(n.getConfig().suffix);
                        //numberView.setDecimal(n.getConfig().precision);
                        absoluteLayout.addView(numberView, layoutParams);
                        numberView.setConfig(n.getConfig());
                        sensors.add(numberView);
                    }
                    if (d instanceof BoolData) {
                        BoolData b = (BoolData)d;
                        BoolImageView boolView = new BoolImageView(getContext());
                        //boolView.setValue(b.currentValue());
                        boolView.addData(b.getConfig().id, b);
                        absoluteLayout.addView(boolView, layoutParams);
                        boolView.setConfig(b.getConfig());
                        sensors.add(boolView);
                    }
                }

            }
        };

        absoluteLayout.addView(mapImageView);
        mapImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mapImageView.setURL(Config.houseViewURL + "/" +  Config.map);

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
