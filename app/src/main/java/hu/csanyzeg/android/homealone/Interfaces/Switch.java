package hu.csanyzeg.android.homealone.Interfaces;

import hu.csanyzeg.android.homealone.UI.OnBoolValueChangeListener;

/**
 * Created by tanulo on 2018. 07. 30..
 */

public interface Switch extends BoolValue{
    public void setOnCheckChangeListener(OnBoolValueChangeListener onCheckChangeListener);
    public OnBoolValueChangeListener getOnCheckChangeListener();
}
