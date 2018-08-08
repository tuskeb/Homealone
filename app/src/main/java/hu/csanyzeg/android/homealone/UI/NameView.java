package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.Data;
import hu.csanyzeg.android.homealone.Interfaces.InitableUI;
import hu.csanyzeg.android.homealone.Interfaces.Sensor;

import java.util.Date;

/**
 * Created by tanulo on 2018. 06. 29..
 */

public class NameView extends LinearLayout implements InitableUI {
    private TextView textView;
    private String sensorId;
    protected Config config = null;

    public NameView(Context context) {
        super(context);
        inflate();
        init();
    }

    public NameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate();
        init();
    }

    public NameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate();
        init();
    }

    @Override
    public void inflate() {
        LayoutInflater.from(getContext()).inflate(hu.csanyzeg.android.homealone.R.layout.name_view, this);
    }

    @Override
    public void inflateparams(Context context, @Nullable AttributeSet attrs) {

    }

    @Override
    public void init() {

        textView  = getRootView().findViewById(hu.csanyzeg.android.homealone.R.id.txt);
    }

    public void setText(String text) {
        textView.setText(text);
    }


    public void setConfig(Config config) {
        this.config = config;
        this.sensorId = config.id;
        this.setText(config.display);
    }
}
