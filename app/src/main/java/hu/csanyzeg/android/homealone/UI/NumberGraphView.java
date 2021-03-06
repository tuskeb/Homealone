package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import hu.csanyzeg.android.homealone.Data.Entry;
import hu.csanyzeg.android.homealone.Data.HorizontalGraphLine;
import hu.csanyzeg.android.homealone.Data.NamedArrayList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by tanulo on 2018. 06. 26..
 */

public class NumberGraphView extends GraphView<Double> {

    protected int decimal = 1;

    protected String pattern = "#";
    protected int nameDrawCount = 0;

    public NumberGraphView(Context context) {
        super(context);
        horizontalDivision = 4;
    }

    public NumberGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        horizontalDivision = 4;
    }

    public NumberGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        horizontalDivision = 4;
    }



    protected void onDrawSuffix(Canvas canvas){
        Paint txtPaint = new Paint();
        txtPaint.setAntiAlias(true);
        txtPaint.setTextSize(getFontSize(7));
        txtPaint.setColor(0x7f777777);
        float y =  (float) (getFontSize(1)*3.3);

        canvas.drawText(horizontal, w - txtPaint.measureText(horizontal) - padding * 3, h - y - padding*3, txtPaint);
        canvas.drawText(unit, padding + y / 2, y, txtPaint);
    }




    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
        if (decimal <= 0) {
            pattern = "#";
            return;
        }
        pattern = "#.";
        for (int i = 0; i < decimal; i++) {
            pattern += "#";
        }
    }

    @Override
    protected void onDrawGraphEntry(Canvas canvas, NamedArrayList<Entry<Double>> entries) {
        synchronized (entries) {
            if (entries.size() > 0) {
                Paint p = new Paint();
                p.setAntiAlias(true);

                for (int i = 0; i < entries.size() - 1; i++) {
                    //p.setStrokeWidth(i / 2 + 1);
                    //if (entries.get(i).value != null && entries.get(i).value != null) {
                    //p.setStrokeWidth((int) (((double) i / (double) entries.size()) * h / 80.0 + h / 150));
                    //p.setColor(entries.get(i).color + (((int) (((double) i / (double) entries.size()) * 100 + 50)) << 24));
                    p.setColor(entries.get(0).color);
                    int x1 = (int) ((double) (entries.get(i).date.getTime() - timeMin) * pxms);
                    int y1 = h - (int) (entries.get(i).value * pxunit) + (int) (this.min * pxunit);
                    int x2 = (int) ((double) (entries.get(i + 1).date.getTime() - timeMin) * pxms);
                    int y2 = h - (int) (entries.get(i + 1).value * pxunit) + (int) (this.min * pxunit);
                    canvas.drawLine(x1, y1, x2, y2, p);
                    //}
                }
                //TODO: felkutatni a hiba okát:

                Entry<Double> min = Collections.min(entries, new Comparator<Entry<Double>>() {
                    @Override
                    public int compare(Entry<Double> doubleEntry, Entry<Double> t1) {
                        return doubleEntry.value.compareTo(t1.value);
                    }
                });
                Entry<Double> max = Collections.max(entries, new Comparator<Entry<Double>>() {
                    @Override
                    public int compare(Entry<Double> doubleEntry, Entry<Double> t1) {
                        return doubleEntry.value.compareTo(t1.value);
                    }
                });
                Entry<Double> current = Collections.max(entries, new Comparator<Entry<Double>>() {
                    @Override
                    public int compare(Entry<Double> doubleEntry, Entry<Double> t1) {
                        return doubleEntry.date.compareTo(t1.date);
                    }
                });

                Entry<Double> start = Collections.min(entries, new Comparator<Entry<Double>>() {
                    @Override
                    public int compare(Entry<Double> doubleEntry, Entry<Double> t1) {
                        return doubleEntry.date.compareTo(t1.date);
                    }
                });


                double sum = 0;
                double intv = 0;
                for (int i = 0; i < entries.size() - 1; i++) {
                    intv += entries.get(i + 1).date.getTime() - entries.get(i).date.getTime();
                    sum += entries.get(i).value * (entries.get(i + 1).date.getTime() - entries.get(i).date.getTime());
                }
                double avg = sum / intv;

                p.setTextSize(getFontSize(0));
                p.setColor(current.color);
                p.setAlpha(255);
                String v = String.format("Min: %." + decimal + "f", min.value) + " " + getUnit();
                int x1 = (int) ((double) (min.date.getTime() - timeMin) * pxms) - (int) p.measureText(v) / 2;
                int y1 = h - (int) (min.value * pxunit) + (int) p.getTextSize() + (int) padding + (int) (this.min * pxunit);
                drawStringOnCanvas(v, x1, y1, p, canvas);


                v = String.format("Max: %." + decimal + "f", max.value) + " " + getUnit();
                x1 = (int) ((double) (max.date.getTime() - timeMin) * pxms) - (int) p.measureText(v) / 2;
                y1 = h - (int) (max.value * pxunit) - (int) padding + (int) (this.min * pxunit);
                drawStringOnCanvas(v, x1, y1, p, canvas);

                v = String.format("Átl: %." + decimal + "f", avg) + " " + getUnit();
                x1 = (int) ((double) (w -  p.measureText(v) / 2) / 2);
                y1 = h - (int) (avg * pxunit) - (int) padding + (int) (this.min * pxunit);
                drawStringOnCanvas(v, x1, y1, p, canvas);

                if (entries.getName() != null) {
                    nameDrawCount++;
                    p.setTextSize(getFontSize(1));
                    v = entries.getName() + ": " + String.format("%." + decimal + "f", current.value) + " " + getUnit();
                    //x1 = (int) ((double) ((current.date.getTime() - start.date.getTime()) / 2) * pxms) - (int) p.measureText(v) / 2;
                    x1 = (int) ((double) (timeInterval / 2) * pxms) - (int) p.measureText(v) / 2;
                    y1 = h - (int) (avg * pxunit) - (int) padding + (int) (this.min * pxunit);
                    //drawStringOnCanvas(v, x1, y1, p, canvas);
                    drawStringOnCanvas(v, w, (int)((padding + getFontSize(1)) * nameDrawCount), p, canvas);

                }
/*
        p.setTextSize(getFontSize(2));
        v = String.format("%." + precision + "f", current.value) + " " + getUnit();
        x1 = (int) ((double) (current.date.getTime() - timeMin) * pxms) - (int)p.measureText(v) / 2;
        y1 = h - (int) (current.value * pxunit)  - (int)padding + (int) (this.min * pxunit);
        drawStringOnCanvas(v,x1,y1,p,canvas);
*/

            }
        }
    }


    @Override
    public void createCoordinateSystem(int vdiv, int hdiv) {
        super.createCoordinateSystem(vdiv, hdiv);
        HorizontalGraphLine h;
        for (int i=0; i<=hdiv; i++) {
            horizontalGraphLineList.add(h = new HorizontalGraphLine((max - min) / (double)hdiv * (double)i, 0xaf000000, String.format("%."+decimal+"f",(max - min) / (double) hdiv* (double)i) + " " + unit));
            hCoordLines.add(h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        nameDrawCount = 0;
        super.onDraw(canvas);
        //onDrawSuffix(canvas);
    }
}
