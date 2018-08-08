package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import hu.csanyzeg.android.homealone.Interfaces.InitableUI;
import hu.csanyzeg.android.homealone.Interfaces.NumberValue;
import hu.csanyzeg.android.homealone.R;

import java.text.DecimalFormat;

/**
 * Created by tanulo on 2018. 06. 26..
 */

public class NumberView extends TextView implements InitableUI, NumberValue {
    private String suffix = "NA";
    private Double value = 0.0;
    private int decimal = 1;
    private String pattern="#.0";
    private boolean multiLine = true;
    private int defaultColor;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public NumberView(Context context) {
        super(context);
        init();
    }

    public NumberView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
        inflateparams(context,attrs);
    }

    public NumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        inflateparams(context,attrs);
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String unit) {
        this.suffix = unit;
        setText();
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
        setText();
    }

    public int getDecimal() {
        return decimal;
    }


    public void setDecimal(int decimal) {
        this.decimal = decimal;
        if (decimal <= 0){
            pattern = "#";
            return;
        }
        pattern = "#.";
        for(int i = 0; i< decimal; i++){
            pattern+="0";
        }
        invalidate();
    }


    public void setText() {

        if (multiLine){
            if (value == null){
                setText("-" + "\n" + suffix);
                return;
            }
            setText((new DecimalFormat(pattern)).format(value).replace(".",",") + "\n" + suffix);
        }else{
            if (value == null){
                setText("-");
                return;
            }
            setText((new DecimalFormat(pattern)).format(value).replace(".",",") + " " + suffix);
        }
    }

    @Override
    public void init() {
        setDecimal(decimal);
        setText();
        defaultColor = getCurrentTextColor();
    }

    @Override
    public void inflate() {

    }

    @Override
    public void inflateparams(Context context, @Nullable AttributeSet attrs) {

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.NumberView,0, 0);

        try {
            setMultiLine(a.getBoolean(R.styleable.NumberView_numberview_multiLine, false));
            decimal = a.getInteger(R.styleable.NumberView_numberview_decimal, 1);
            suffix = a.getString(R.styleable.NumberView_numberview_suffix);
        } finally {
            a.recycle();
        }
    }

    public boolean isMultiLine() {
        return multiLine;
    }

    public void setMultiLine(boolean multiLine) {
        this.multiLine = multiLine;
    }

    public void setAlarm(boolean b) {
        if (b) {
            setTextColor(Color.RED);
        } else {
            setTextColor(defaultColor);
        }
    }
}
