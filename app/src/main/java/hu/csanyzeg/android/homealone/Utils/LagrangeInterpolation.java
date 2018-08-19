package hu.csanyzeg.android.homealone.Utils;

/**
 * Created by tanulo on 2018. 08. 18..
 */

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LagrangeInterpolation {


    //http://www.inf.u-szeged.hu/~kgelle/sites/default/files/upload/08_polinomok_interpolacio.pdf
    public class Point implements Comparable<Point>{

        double x;
        double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(@NonNull Point point) {
            return Double.compare(x, point.x);
        }
    }

    private int range = 3;

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setRangeToMaximum() {
        this.range = points.size();
    }

    public void setRangeToMinimum() {
        this.range = 2;
    }

    ArrayList<Point> points = new ArrayList<Point>();


    public ArrayList<Point> getPoints() {
        return points;
    }


    public void addpoint(double x, double y) {
        points.add(new Point(x, y));

        Collections.sort(points);

    }

    public double getY(double x) {
        int count = points.size();
        int startrange = 0;
        while (startrange < points.size() && points.get(startrange).x < x) {
            startrange++;
        }
        startrange -= range / 2;
        if (startrange < 0) {
            startrange = 0;
        }


        int endrange = startrange + range - 1;
        if (endrange >= points.size()) {
            endrange = points.size() - 1;
            startrange = endrange - range + 1;
        }
        //System.out.println(startrange + " - " + endrange);
        double sum = 0;
        for (int i = startrange; i <= endrange; i++) {
            double mul = 1;
            for (int j = startrange; j <= endrange; j++) {
                if (i != j) {
                    mul *= (x - points.get(j).x) / (points.get(i).x - points.get(j).x);
                }
            }
            sum += points.get(i).y * mul;
        }
        return sum;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LagrangeInterpolation l = new LagrangeInterpolation();
        l.addpoint(-1, 2);
        l.addpoint(0, 1);
        l.addpoint(1, 2);
        l.addpoint(2, 3);
        l.addpoint(3, 1);
        l.addpoint(4, 0);

        l.setRange(3);
        //l.setRangeToMaximum();
        for (Point p : l.getPoints()) {
            System.out.println(" X = " + p.x + " Y = " + l.getY(p.x) + "  Eredeti Y:" + p.y);
        }

        System.out.println("------------");
        for (double x = -1; x < 4; x += 0.1f) {
            System.out.println(" X = " + x + " Y = " + l.getY(x));
        }
    }
}
