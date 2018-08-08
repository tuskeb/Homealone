package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import hu.csanyzeg.android.homealone.Interfaces.BoolValue;
import hu.csanyzeg.android.homealone.Interfaces.InitableUI;
import hu.csanyzeg.android.homealone.R;

/**
 * Created by tanulo on 2018. 07. 30..
 */

public class BoolIndicatorView extends ImageView implements InitableUI, BoolValue {
    private Boolean value=null;

    public BoolIndicatorView(Context context) {
        super(context);
        inflate();
        init();
    }

    public BoolIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate();
        init();
    }

    public BoolIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate();
        init();
    }

    protected void onRefreshValue(){
        if (this.value == null){
            setImageResource(R.drawable.led_circle_grey);
            return;
        }
        if (this.value == true){
            setImageResource(R.drawable.led_circle_green);
        }else{
            setImageResource(R.drawable.led_circle_red);
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
    }

    @Override
    public void inflate() {

    }

    @Override
    public void inflateparams(Context context, @Nullable AttributeSet attrs) {

    }

}
