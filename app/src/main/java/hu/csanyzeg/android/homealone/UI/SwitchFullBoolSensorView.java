package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import hu.csanyzeg.android.homealone.Data.BoolData;
import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Data.NumberData;
import hu.csanyzeg.android.homealone.Interfaces.Switch;
import hu.csanyzeg.android.homealone.R;

/**
 * Created by tanulo on 2018. 07. 30..
 */

public class SwitchFullBoolSensorView extends FullBoolSensorView implements Switch {
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
        LayoutInflater.from(getContext()).inflate(R.layout.switch_full_bool_sensor_view, this);
    }
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            final boolean[] cancel = {false};
            if (b) {
                if (getConfig().distance!=null && getConfig().distance < Data.GPSDistanceInMeter) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                    alert.setTitle(getConfig().display);
                    alert.setMessage("A légvonalban mért távolság otthonról nagyobb, mint " + getConfig().distance + " m. Folytatja a műveletet?");

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            cancel[0] = false;
                            if (onCheckChangeListener != null) {
                                onCheckChangeListener.onChangeValueTrue();
                            }
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
        super.setValue(value);
        if (value!=null) {
            onoffCheckBox.setOnCheckedChangeListener(null);
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
            boolData.setFastPollingRemainCount(2);
        }
    }

    @Override
    public void updateData(){
        super.updateData();
    }
}
