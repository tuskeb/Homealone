package hu.csanyzeg.android.homealone.Data;

import java.util.ArrayList;

/**
 * Created by tanulo on 2018. 08. 06..
 */

public class NamedArrayList<E> extends ArrayList<E> {
    private String name = null;

    synchronized
    public String getName() {
        return name;
    }

    synchronized
    public void setName(String name) {
        this.name = name;
    }
}
