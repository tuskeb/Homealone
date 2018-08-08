package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import hu.csanyzeg.android.homealone.Interfaces.InitableUI;
import hu.csanyzeg.android.homealone.Interfaces.BoolValue;

/**
 * Created by tanulo on 2018. 07. 06..
 */

public class BoolView extends TextView implements InitableUI, BoolValue {
    private Boolean value=null;
    private int defaultColor;

    public BoolView(Context context) {
        super(context);
        inflate();
        init();
    }

    public BoolView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate();
        inflateparams(context, attrs);
        init();
    }

    public BoolView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate();
        inflateparams(context, attrs);
        init();
    }

    protected void onRefreshValue(){
        if (this.value == null){
            setText("-");
            return;
        }
        if (this.value == true){
            setText("Aktív");
        }else{
            setText("Inaktív");
        }
    }


    @Override
    public void setValue(Boolean value) {
        this.value = value;
        onRefreshValue();
    }

    @Override
    public Boolean getValue() {
        return this.value;
    }

    @Override
    public void init() {
        onRefreshValue();
        defaultColor = getCurrentTextColor();
    }

    @Override
    public void inflate() {

    }

    @Override
    public void inflateparams(Context context, @Nullable AttributeSet attrs) {

    }


    public void setAlarm(boolean b) {
        if (b) {
            setTextColor(Color.RED);
        } else {
            setTextColor(defaultColor);
        }
    }
}
