package hu.csanyzeg.android.homealone.Data;

/**
 * Created by tanulo on 2018. 08. 02..
 */

public abstract class OnDataUpdateListener {
    public abstract void onBeginUpdate(Data data);
    public abstract void onEndUpdate(Data data);
}
