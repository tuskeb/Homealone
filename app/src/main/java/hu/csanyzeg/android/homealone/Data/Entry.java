package hu.csanyzeg.android.homealone.Data;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by tanulo on 2018. 07. 05..
 */

public class Entry<T> implements Comparable<Entry> {
        public T value = null;
        public Date date = new Date();
        public int color = 0xff000000;

        public Entry(T value, Date date) {
                this.value = value;
                this.date = date;
        }

        public Entry(T value, long date) {
                this.value = value;
                this.date = new Date();
                this.date.setTime(date);
        }

        public Entry(T value, Date date, int color) {
                this.value = value;
                this.date = date;
                this.color = color;
        }

        public Entry(T value, long date, int color) {
                this.value = value;
                this.date = new Date();
                this.date.setTime(date);
                this.color = color;
        }


        @Override
        public String toString() {
                return "Entry{" +
                        "value=" + value +
                        ", date=" + date +
                        '}';
        }

        @Override
        public int compareTo(@NonNull Entry entry)
        {
                if (this.date.getTime()==entry.date.getTime()){
                   return 0;
                }
                if (this.date.getTime()<entry.date.getTime()){
                        return -1;
                }
                return 1;
        }
}
