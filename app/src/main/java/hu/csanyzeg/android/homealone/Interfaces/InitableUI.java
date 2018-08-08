package hu.csanyzeg.android.homealone.Interfaces;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by tanulo on 2018. 06. 26..
 */

public interface InitableUI {
    public void init();
    public void inflate();
    public void inflateparams(Context context, @Nullable AttributeSet attrs);
}
