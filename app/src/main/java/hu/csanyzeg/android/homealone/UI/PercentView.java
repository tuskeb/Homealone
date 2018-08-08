package hu.csanyzeg.android.homealone.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

import hu.csanyzeg.android.homealone.Interfaces.MinMax;
import hu.csanyzeg.android.homealone.Interfaces.NumberValue;

import java.text.DecimalFormat;

/**
 * Created by tanulo on 2018. 06. 26..
 */

public class PercentView extends SurfaceView implements MinMax, NumberValue {
    private Double min = -10.0, max=30.0, value=0.0;
    private int countOfDivision = 10;
    private int precision = 1;
    private String pattern="#";

    public PercentView(Context context) {
        super(context);
    }

    public PercentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PercentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public Double getMin() {
        return min;
    }

    @Override
    public void setMin(Double min) {
        this.min = min;
        invalidate();
    }

    @Override
    public Double getMax() {
        return max;
    }

    @Override
    public void setMax(Double max) {
        this.max = max;
        invalidate();
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void setValue(Double value) {
        this.value = value;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getParent()!=null && ((View) getParent()).getBackground() instanceof ColorDrawable) {
            setBackgroundColor(((ColorDrawable) ((View) getParent()).getBackground()).getColor());
        }
        super.onDraw(canvas);
        double dt = (getMax() - getMin()) / (double)(countOfDivision-1);
        double v = getMin();
        for(int i=0; i<countOfDivision; i++){
            drawShape(canvas, i,  getMin() + dt*(double)i);
        }
    }

    protected void drawShape(Canvas canvas,  int index, double value){
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        int height = h/countOfDivision;
        int paddingTop = height- h / (int)((double)countOfDivision*1.3);
        int paddingLeft = (int)((double)w *0.9);
        Paint bgPaint = new Paint();
        Paint txtPaint = new Paint();
        String txt = (new DecimalFormat(pattern)).format(value).replace(".",",");
        txtPaint.setTextSize(h / (int)((double)countOfDivision*1.6));

        if (this.value != null && this.value>=value) {
            int v = (int)(256.0 * (double)index/(double)countOfDivision);
            txtPaint.setColor(0xffffffff);
            bgPaint.setColor(0xff000000 + (v*2>255?255:v*2)*65536 + 0x0000ff00 - v*256 );
        } else {
            txtPaint.setColor(0xff555555);
            bgPaint.setColor(0xffeeeeee);
        }
        canvas.drawRect(w-paddingLeft, h-height*(index+1), paddingLeft, h-height*(index+1)+height-paddingTop, bgPaint);
        canvas.drawText(txt, w / 2 - txtPaint.measureText(txt.replace("-", "--")) / 2, h - height * (index + 1) + height - (int) ((double) paddingTop * 1.8), txtPaint);
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
        if (precision <= 0){
            pattern = "#";
            return;
        }
        pattern = "#.";
        for(int i=0; i<precision;i++){
            pattern+="#";
        }
        invalidate();
    }

    @Override
    public void setSuffix(String unit) {

    }

    @Override
    public void setDecimal(int decimal) {

    }
}
