package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import hu.csanyzeg.android.homealone.Interfaces.InitableUI;
import hu.csanyzeg.android.homealone.Interfaces.MinMax;
import hu.csanyzeg.android.homealone.Interfaces.NumberValue;

/**
 * Created by tanulo on 2018. 06. 29..
 */

public class EditValueView extends LinearLayout implements InitableUI, MinMax, NumberValue {
    NumberView valueView;
    TextView textView;
    SeekBar seekBar;
    OnValueChangedListener onValueChangedListener = null;
    int decimal = 0;

    protected double min=0, max=0, value=100;
    public EditValueView(Context context) {
        super(context);
        inflate();
        init();
    }

    public EditValueView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate();
        init();
        inflateparams(context,attrs);
    }

    public EditValueView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate();
        init();
        inflateparams(context, attrs);
    }

    @Override
    public void inflate(){
        LayoutInflater.from(getContext()).inflate(hu.csanyzeg.android.homealone.R.layout.edit_value_view, this);
    }



    @Override
    public void init() {

        valueView = getRootView().findViewById(hu.csanyzeg.android.homealone.R.id.editvalueValue);
        seekBar  = getRootView().findViewById(hu.csanyzeg.android.homealone.R.id.editvalueSeekbar);
        textView = getRootView().findViewById(hu.csanyzeg.android.homealone.R.id.editvalueText);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                valueView.setValue(getValue());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                onValueChanged(getValue());
            }
        });

    }

    protected void onValueChanged(Double value){
        if (onValueChangedListener != null){
            onValueChangedListener.doubleValueChanged(value);
        }
    }

    public void setOnValueChangedListener(OnValueChangedListener onValueChangedListener){
        this.onValueChangedListener = onValueChangedListener;
    }

    public OnValueChangedListener getOnValueChangedListener() {
        return onValueChangedListener;
    }

    @Override
    public void inflateparams(Context context, @Nullable AttributeSet attrs) {

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, hu.csanyzeg.android.homealone.R.styleable.EditValueView,0, 0);

        try {
            valueView.setMultiLine(a.getBoolean(hu.csanyzeg.android.homealone.R.styleable.EditValueView_editvalueview_multiLine, false));
            valueView.setDecimal(a.getInteger(hu.csanyzeg.android.homealone.R.styleable.EditValueView_editvalueview_decimal, 1));
            valueView.setSuffix(a.getString(hu.csanyzeg.android.homealone.R.styleable.EditValueView_editvalueview_suffix));
            setText(a.getString(hu.csanyzeg.android.homealone.R.styleable.EditValueView_editvalueview_text));
        } finally {
            a.recycle();
        }
    }

    @Override
    public Double getMin() {
        return this.min;
    }

    @Override
    public Double getValue() {
        return ((double)(seekBar.getProgress())*Math.pow(10.0, -valueView.getDecimal()))+min;
    }

    @Override
    public void setMin(Double value) {
        this.min = value;
        seekBar.setMax((int)((max-min)*Math.pow(10.0, valueView.getDecimal())));
        //System.out.println(value);
        //System.out.println(seekBar.getMax());
    }

    @Override
    public void setValue(Double value) {
        //System.out.println("------------------------" + (int)((value-min)*Math.pow(10.0, valueView.getDecimal())));
        int seekvalue = (int)Math.round((value-min)*Math.pow(10.0, valueView.getDecimal()));
        if (seekvalue < 0){
            seekvalue = 0;
        }
        if (seekvalue> seekBar.getMax()){
            seekvalue = seekBar.getMax();
        }
        seekBar.setProgress(seekvalue);
        valueView.setValue(getValue());
        onValueChanged(value);
    }

    @Override
    public Double getMax() {
        return this.max;
    }

    @Override
    public void setSuffix(String unit) {
        valueView.setSuffix(unit);
    }

    @Override
    public void setMax(Double value) {
        this.max = value;
        seekBar.setMax((int)((max-min)*Math.pow(10.0, valueView.getDecimal())));
        //System.out.println(value);
        //System.out.println(seekBar.getMax());
    }

    public void setText(String text){
        textView.setText(text);
    }


    public int getDecimal() {
        return decimal;
    }


    public void setDecimal(int decimal) {
        valueView.setDecimal(decimal);
        this.decimal = decimal;
    }


}
