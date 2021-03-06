package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import hu.csanyzeg.android.homealone.Data.BoolData;
import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Interfaces.Switch;
import hu.csanyzeg.android.homealone.R;

/**
 * Created by tanulo on 2018. 07. 30..
 */

public class SwitchFullBoolSensorView extends GraphBoolSensorView implements Switch {
    private OnBoolValueChangeListener onCheckChangeListener =null;

    TextView stabinfoTextView;
    CheckBox onoffCheckBox;

    public SwitchFullBoolSensorView(Context context) {
        super(context);
    }

    public SwitchFullBoolSensorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchFullBoolSensorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnCheckChangeListener(OnBoolValueChangeListener onCheckChangeListener) {
        this.onCheckChangeListener = onCheckChangeListener;
    }

    @Override
    public OnBoolValueChangeListener getOnCheckChangeListener() {
        return onCheckChangeListener;
    }

    @Override
    public void inflate() {
        LayoutInflater.from(getContext()).inflate(R.layout.switch_bool_sensor_view, this);
    }
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            final boolean[] cancel = {false};
            if (b) {
                if (getConfig().distance!=null && getConfig().distance < Data.GPSDistanceInMeter) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                    alert.setTitle(getConfig().display);
                    alert.setMessage("A légvonalban mért távolság otthonról nagyobb, mint " + String.format("%.0f", getConfig().distance) + " m. Folytatja a műveletet?");

                    alert.setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            cancel[0] = false;
                            setValue(true);
                            if (onCheckChangeListener != null) {
                                onCheckChangeListener.onChangeValueTrue();
                            }
                        }
                    });

                    alert.setNegativeButton("Nem", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            cancel[0] = true;
                            setValue(false);
                        }
                    });

                    alert.show();
                }else {
                    if (onCheckChangeListener != null) {
                        onCheckChangeListener.onChangeValueTrue();
                    }
                }
            } else {
                if (onCheckChangeListener != null) {
                    onCheckChangeListener.onChangeValueFalse();
                }
            }
            if (!cancel[0]){
                setFastPolling();
            }
        }
    };
    @Override
    public void init() {
        super.init();
        stabinfoTextView = getRootView().findViewById(R.id.stabinfo);
        onoffCheckBox = getRootView().findViewById(R.id.onoffbutton);
        onoffCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public void setValue(Boolean value) {

        if (value!=null) {
            onoffCheckBox.setOnCheckedChangeListener(null);
            super.setValue(value);
            onoffCheckBox.setChecked(value);
            onoffCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
        }

    }

    public void setMonostabInfo(int sec){
        if (sec==-1){
            stabinfoTextView.setText("A kapcsoló nem kapcsol le automatikusan.");
        }else{
            stabinfoTextView.setText("A kapcsoló "+sec+" másodperc után lekapcsol.");
        }
    }

    @Override
    public void setConfig(Config config) {
        super.setConfig(config);
        if (!config.isBistabil()) {
            setMonostabInfo(-1);
        }else{
            setMonostabInfo(config.monostab);
        }
    }

    public void setFastPolling() {
        for (BoolData boolData: data.values()) {
            boolData.setFastPollingRemainCount(Config.fastPollingCount);
        }
    }

    @Override
    public void updateData(){
        super.updateData();
    }
}
