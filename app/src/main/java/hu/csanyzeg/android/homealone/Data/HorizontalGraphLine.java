package hu.csanyzeg.android.homealone.Data;

/**
 * Created by tanulo on 2018. 07. 06..
 */

public class HorizontalGraphLine {
    public Double value;
    public int color = 0;
    public String text = null;

    public HorizontalGraphLine(Double value, int color) {
        this.value = value;
        this.color = color;
    }

    public HorizontalGraphLine(Double value, int color, String text) {
        this.value = value;
        this.color = color;
        this.text = text;
    }

    @Override
    public String toString() {
        return "HorizontalGraphLine{" +
                "value=" + value +
                ", color=" + color +
                '}';
    }
}
