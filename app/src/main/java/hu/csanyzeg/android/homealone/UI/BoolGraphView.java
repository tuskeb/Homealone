package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import hu.csanyzeg.android.homealone.Data.Entry;
import hu.csanyzeg.android.homealone.Data.NamedArrayList;

import java.util.ArrayList;

/**
 * Created by tanulo on 2018. 07. 29..
 */

public class BoolGraphView extends GraphView<Boolean> {

    public BoolGraphView(Context context) {
        super(context);
    }

    public BoolGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BoolGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDrawGraphEntry(Canvas canvas, NamedArrayList<Entry<Boolean>> entries) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(padding);
        for (int i = 0; i < entries.size()-1; i++) {
            if (entries.get(i).value) {
                //p.setStrokeWidth((int) (((double) i / (double) entries.size()) * h/7.0 + h/50));
                //p.setColor(entries.get(i).color - (((int) (((double) i / (double) entries.size()) * 228.0)) << 24));
                p.setColor(entries.get(0).color);
                int x1 = (int) ((double) (entries.get(i).date.getTime() - timeMin) * pxms);
                int y1 = h - (int) (0.655 * pxunit) + (int) (this.min * pxunit);
                int x2 = (int) ((double) (entries.get(i + 1).date.getTime() - timeMin) * pxms);
                int y2 = h - (int) (0.655 * pxunit) + (int) (this.min * pxunit);
                canvas.drawLine(x1, y1, x2, y2, p);
            }
        }
    }
}
