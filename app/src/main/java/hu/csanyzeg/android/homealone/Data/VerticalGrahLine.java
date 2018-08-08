package hu.csanyzeg.android.homealone.Data;

import java.util.Date;

/**
 * Created by tanulo on 2018. 07. 06..
 */

public class VerticalGrahLine {
    public int color;
    public Date date;

    public VerticalGrahLine(Date date, int color) {
        this.color = color;
        this.date = date;
    }

    public VerticalGrahLine(long date, int color) {
        this.color = color;
        this.date = new Date();
        this.date.setTime(date);
    }

    @Override
    public String toString() {
        return "VerticalGrahLine{" +
                "color=" + color +
                ", date=" + date +
                '}';
    }
}
