package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import hu.csanyzeg.android.homealone.Data.Entry;
import hu.csanyzeg.android.homealone.Data.HorizontalGraphLine;
import hu.csanyzeg.android.homealone.Data.NamedArrayList;
import hu.csanyzeg.android.homealone.Data.VerticalGrahLine;
import hu.csanyzeg.android.homealone.Interfaces.MinMax;

/**
 * Created by tanulo on 2018. 07. 30..
 */

public abstract class GraphView<T> extends SurfaceView  implements MinMax {
    protected ArrayList<NamedArrayList<Entry<T>>> entryList = new ArrayList<>();
    protected ArrayList<HorizontalGraphLine> horizontalGraphLineList = new ArrayList<HorizontalGraphLine>();
    protected ArrayList<VerticalGrahLine> verticalGraphLineList = new ArrayList<VerticalGrahLine>();
    protected ArrayList<HorizontalGraphLine> hCoordLines = new ArrayList<>();
    protected ArrayList<VerticalGrahLine> vCoordLines = new ArrayList<>();

    protected double min = 0, max = 1;
    protected int w;
    protected int h;
    protected long timeInterval;
    protected long timeMin = Calendar.getInstance().getTimeInMillis() - 365L*12L*24L*60L*60L*1000L;
    protected long timeMax = Calendar.getInstance().getTimeInMillis();
    protected double pxms;
    protected double pxunit;
    protected Canvas canvas;
    protected float fontSize;
    protected float padding;
    protected int verticalDivision = 4;
    protected int horizontalDivision = 1;

    protected String horizontal = "t";
    protected String unit = "NA";

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    protected float getFontSize(int size){
        return fontSize * (1.0f + (float)size * 0.3f);
    }

    public void setVerticalDivision(int verticalDivision) {
        this.verticalDivision = verticalDivision;
    }

    public void setHorizontalDivision(int horizontalDivision) {
        this.horizontalDivision = horizontalDivision;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        if (getParent() != null && ((View) getParent()).getBackground() instanceof ColorDrawable) {
            setBackgroundColor(((ColorDrawable) ((View) getParent()).getBackground()).getColor());
        }
        super.onDraw(canvas);
        refreshSizeValues(canvas);
        createCoordinateSystem(verticalDivision,horizontalDivision);
        if (entryList.size()>0 && entryList.get(0).size()>0) {
            onDrawVerticalLines(canvas);
            onDrawHorizontalLines(canvas);
            onDrawGraphEntries(canvas);
        }
    }

    private void refreshSizeValues(Canvas canvas){
        if (canvas != null) {
            w = canvas.getWidth();
            h = canvas.getHeight();

            pxms = getPxPerMs();
            pxunit = getPxPerUnit();

            padding = Math.max((float) w / 80f, (float) h / 60f);

            fontSize = Math.max((float) w / 40f, (float) h / 30f);
        }
    }


    public long getTimeMin() {
        return timeMin;
    }

    public void setTimeMin(long timeMin) {
        this.timeMin = timeMin;
    }

    public long getTimeMax() {
        return timeMax;
    }

    public void setTimeMax(long timeMax) {
        this.timeMax = timeMax;
    }

    protected double getPxPerMs(){
        /*long min = Long.MAX_VALUE, max = Long.MIN_VALUE;
        for(ArrayList<Entry<T>> entryArrayList : entryList)
            for (Entry e : entryArrayList) {
                //System.out.println(e.date.getTime());
                if (e.date.getTime() > max) {
                    max = e.date.getTime();
                }
                if (e.date.getTime() < min) {
                    min = e.date.getTime();
                }
            }
*/
//        timeInterval = max - min;
        timeInterval = timeMax - timeMin;
        //timeMin = min;
        return (double) w / (double) timeInterval;
    };

    protected double getPxPerUnit(){
        return  (double) h / (this.max - this.min);
    }



    protected void onDrawHorizontalLines(Canvas canvas){
        for (HorizontalGraphLine entries : horizontalGraphLineList) {
            Paint txtPaint = new Paint();
            txtPaint.setAntiAlias(true);
            txtPaint.setTextSize(getFontSize(0));
            txtPaint.setColor(entries.color);
            float y = h + (int) ((this.min - entries.value) * pxunit) - 2;
            canvas.drawLine(0, y, w, y, txtPaint);
            if (entries.text!=null){
                if (y-w/80<txtPaint.getTextSize()){
                    canvas.drawText(entries.text, padding,  y+txtPaint.getTextSize() + padding, txtPaint);
                }else
                {
                    canvas.drawText(entries.text, padding,  y - padding, txtPaint);
                }
            }
        }
    }


    protected void onDrawVerticalLines(Canvas canvas) {
        for (VerticalGrahLine entries : verticalGraphLineList) {
            Paint txtPaint = new Paint();
            txtPaint.setAntiAlias(true);
            txtPaint.setTextSize(getFontSize(0));
            txtPaint.setColor(entries.color);
            txtPaint.setAlpha(64);
            String simpleDateFormat1 = (new SimpleDateFormat("yyyy")).format(entries.date);
            String simpleDateFormat2 = (new SimpleDateFormat("MMM dd")).format(entries.date);
            String simpleDateFormat3 = (new SimpleDateFormat("HH:mm")).format(entries.date);
            //System.out.println(((entries.date.getTime() - timeMin) * pxms));
            float x = (int) ((entries.date.getTime() - timeMin) * pxms);
            float y = h-padding;

            /*
            float y = h + (int) (this.min * pxunit) - 2 - padding;


            if(y>h){
                y=h;
            }

            if (y<(int) ((double) txtPaint.getTextSize() * 3 * 1.2)){
                y = (int) ((double) txtPaint.getTextSize() * 3 * 1.2);
            }
            */

            canvas.drawLine((int) ((entries.date.getTime() - timeMin) * pxms) + 1, 0, (int) ((entries.date.getTime() - timeMin) * pxms) + 1, h, txtPaint);
            txtPaint.setAlpha(200);
            drawStringOnCanvas(simpleDateFormat1,(int)x - (int)txtPaint.measureText(simpleDateFormat1) / 2,(int)y - (int) ((double) txtPaint.getTextSize() * 2 * 1.2), txtPaint, canvas);
            drawStringOnCanvas(simpleDateFormat2,(int)x - (int)txtPaint.measureText(simpleDateFormat2) / 2,(int)y - (int) ((double) txtPaint.getTextSize() * 1 * 1.2), txtPaint, canvas);
            drawStringOnCanvas(simpleDateFormat3,(int)x - (int)txtPaint.measureText(simpleDateFormat3) / 2,(int)y , txtPaint, canvas);
            /*canvas.drawText(simpleDateFormat1.format(entries.date), x, y - (int) ((double) txtPaint.getTextSize() * 2 * 1.2), txtPaint);
            canvas.drawText(simpleDateFormat2.format(entries.date), x, y - (int) ((double) txtPaint.getTextSize() * 1 * 1.2), txtPaint);
            canvas.drawText(simpleDateFormat3.format(entries.date), x, y, txtPaint);
            */
        }
    }


    public void drawStringOnCanvas(String v, int x1, int y1, Paint p, Canvas canvas){
        if (x1<padding) x1= (int)padding;
        if (x1 + p.measureText(v) + padding>w) x1 = w - (int)p.measureText(v) - (int)padding;
        if (y1<padding + p.getTextSize()) y1= (int)padding + (int)p.getTextSize();
        if (y1 - p.getTextSize() + padding>h) y1 = h - (int)p.getTextSize() - (int)padding;
        canvas.drawText(v, x1, y1, p);
    }

    protected void onDrawGraphEntries(Canvas canvas){
        for(NamedArrayList<Entry<T>> e : entryList){
            onDrawGraphEntry(canvas, e);
        }
        //System.out.println(entryList.size());
    }

    protected abstract void onDrawGraphEntry(Canvas canvas, NamedArrayList<Entry<T>> entries);

    public ArrayList<NamedArrayList<Entry<T>>> getEntryList(){
        return entryList;
    }

    public ArrayList<HorizontalGraphLine> getHorizontalGraphLineList(){
        return horizontalGraphLineList;
    }


    public ArrayList<VerticalGrahLine> getVerticalGraphLineList(){
        return verticalGraphLineList;
    }



    public GraphView(Context context) {
        super(context);
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    public void createCoordinateSystem(int vdiv, int hdiv){

        for(HorizontalGraphLine hg : hCoordLines){
            horizontalGraphLineList.remove(hg);
        }
        for(VerticalGrahLine vg: vCoordLines){
            verticalGraphLineList.remove(vg);
        }

        HorizontalGraphLine h;
        horizontalGraphLineList.add(h = new HorizontalGraphLine(0.0, 0xff000000));
        hCoordLines.add(h);

        for (int i = 0; i < vdiv; i++) {
            VerticalGrahLine v;
            verticalGraphLineList.add(v = new VerticalGrahLine(timeMin + (timeInterval / ((long)(vdiv - 1)))*(long)i, 0xff000000));
            vCoordLines.add(v);
        }
    }

    @Override
    public Double getMin() {
        return min;
    }

    @Override
    public void setMin(Double min) {
        this.min = min;
    }

    @Override
    public Double getMax() {
        return max;
    }

    @Override
    public void setMax(Double max) {
        this.max = max;
    }

}
