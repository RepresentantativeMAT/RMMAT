/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufsc.util;

import br.ufsc.model.Point;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 *
 * @author vanes
 */
public class Util {

    /**
     * convert a number of minutes into a Date object, where the hour component
     * represents the quotient of the division by 60, and the minute component
     * represents the remainder of the division by 60
     *
     * @param min int
     * @return time in Date object
     */
    public static Date convertMinutesToDate(int min) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, ((int) min / 60));
        c.set(Calendar.MINUTE, min % 60);

        return c.getTime();
    }

    public static BitSet concatenateBitSets(BitSet vector_1_in, BitSet vector_2_in) {
        BitSet vector_1_in_clone = (BitSet) vector_1_in.clone();
        BitSet vector_2_in_clone = (BitSet) vector_2_in.clone();
        int n = vector_1_in.cardinality() - 1;//_desired length of the first (leading) vector
        int index = -1;
        while (index < (vector_2_in_clone.length() - 1)) {
            index = vector_2_in_clone.nextSetBit(index + 1);
            vector_1_in_clone.set(index + n);
        }
//        System.out.println("Concatenate: " + vector_1_in_clone);
        return vector_1_in_clone;
    }

    /**
     * calculate Euclidean distances between points in a two-dimensional space
     *
     * @param p1 Point
     * @param p2 Point
     * @return euclidean distance
     */
    public static double euclideanDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
    }

    /**
     * calculate Euclidean distances from a point p1 to the origin in a
     * two-dimensional space
     *
     * @param p1 Point
     * @return eucliden distance
     */
    public static float euclideanDistanceToZero(Point p1) {
        return (float) Math.sqrt(Math.pow(p1.getX(), 2) + Math.pow(p1.getY(), 2));
    }

    /**
     * calculates the median of a list of integers correctly.
     *
     * @param values
     * @return median value
     */
    public static <T extends Number & Comparable<? super T>> float calculateMedian(List<T> values) {
        Collections.sort(values, Comparator.naturalOrder());
        int size = values.size();
        int middleIndex = size / 2;
        if (size % 2 == 1) {
            return values.get(middleIndex).floatValue();
        } else {
            T value1 = values.get(middleIndex - 1);
            T value2 = values.get(middleIndex);
            return (value1.floatValue() + value2.floatValue()) / 2;
        }
    }

    /**
     * calculates the average of a list of integers correctly accept "float" or
     * "integer" values
     *
     * @param values
     * @return average value
     */
    public static <T extends Number & Comparable<? super T>> float calculateAverage(List<T> values) {
        float sum = 0;
        for (T val : values) {
            sum += val.floatValue();

        }
        return (float) sum / values.size();
    }
    
    
    
    public static <T extends Number & Comparable<? super T>> double calculateStandardDesviation(List<T> values) {
        double avg = calculateAverage(values);
        double sumQuadraticDif = 0.0;

        for (T val : values) {
            double dif = val.doubleValue() - avg;
            sumQuadraticDif += dif * dif;
        }

        double var = sumQuadraticDif / values.size();
        return Math.sqrt(var);
    }

    
    
    
    public static int minutesDiference(Date date1, Date date2){
        // Calendar instance
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        // dates in Calendar
        cal1.setTime(date1);
        cal2.setTime(date2);

        // difference in milliseconds 
        long diffMillis = cal2.getTimeInMillis() - cal1.getTimeInMillis();

        // Convert dif to minutes
        int diffMinutes = (int)(diffMillis / (60 * 1000));
        
        return Math.abs(diffMinutes);
    }

}
