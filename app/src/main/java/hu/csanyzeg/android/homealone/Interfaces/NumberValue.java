package hu.csanyzeg.android.homealone.Interfaces;

/**
 * Created by tanulo on 2018. 06. 26..
 */

public interface NumberValue {
    public Double getValue();
    public void setValue(Double value);
    public void setSuffix(String unit);
    public void setDecimal(int decimal);
}
