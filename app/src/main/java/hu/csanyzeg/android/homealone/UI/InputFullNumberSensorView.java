package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.NumberData;
import hu.csanyzeg.android.homealone.R;

/**
 * Created by tanulo on 2018. 08. 06..
 */

public class InputFullNumberSensorView extends FullNumberSensorView {

    private OnValueChangedListener onValueChangedListener = null;
    private EditValueView inputValueView;

    public InputFullNumberSensorView(Context context) {
        super(context);
    }

    public InputFullNumberSensorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InputFullNumberSensorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OnValueChangedListener getOnValueChangedListener() {
        return onValueChangedListener;
    }

    public void setOnValueChangedListener(OnValueChangedListener onValueChangedListener) {
        this.onValueChangedListener = onValueChangedListener;
    }


    @Override
    public void inflate() {
        LayoutInflater.from(getContext()).inflate(R.layout.input_full_number_sensor_view, this);
    }

    private OnValueChangedListener valueChangeListener = new OnValueChangedListener() {
        @Override
        public void doubleValueChanged(Double value) {
            setFastPolling();
            if (onValueChangedListener!= null) {
                onValueChangedListener.doubleValueChanged(value);
            }
        }
    };

    @Override
    public void init() {
        super.init();
        inputValueView = getRootView().findViewById(R.id.inputNumberView);
        inputValueView.setOnValueChangedListener(valueChangeListener);
    }

    @Override
    public void setValue(Double value) {
        super.setValue(value);
        if (value!=null){
            inputValueView.setOnValueChangedListener(null);
            inputValueView.setValue(value);
            inputValueView.setOnValueChangedListener(valueChangeListener);
        }
    }

    @Override
    public void setConfig(Config config) {
        super.setConfig(config);
        inputValueView.setDecimal(config.precision);
        inputValueView.setMin(config.min);
        inputValueView.setMax(config.max);
        inputValueView.setValue(config.default_value);
        inputValueView.setSuffix(config.suffix);
    }



    public void setFastPolling() {
        for (NumberData numberData: data.values()) {
            numberData.setFastPollingRemainCount(2);
        }
    }
}
