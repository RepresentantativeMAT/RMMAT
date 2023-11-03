/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufsc.util;

import br.ufsc.model.Point;
import java.util.Arrays;
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
    
    
    
    public static <T extends Number & Comparable<? super T>> double calculateStandardDesviationAVG(List<T> values) {
        double avg = calculateAverage(values);
        double sumQuadraticDif = 0.0;

        for (T val : values) {
            double dif = val.doubleValue() - avg;
            sumQuadraticDif += dif * dif;
        }

        double var = sumQuadraticDif / values.size();
        return Math.sqrt(var);
    }

    public static <T extends Number & Comparable<? super T>> double calculateStandardDesviationMedian(List<T> values) {
        double median = calculateMedian(values);
        double sumQuadraticDif = 0.0;

        for (T val : values) {
            double dif = val.doubleValue() - median;
            sumQuadraticDif += dif * dif;
        }
        double var = sumQuadraticDif / values.size();
        return Math.sqrt(var);
    }
    
    
    
    public static <T extends Number & Comparable<? super T>> float getMinimumValue(List<T> values) {
        Collections.sort(values, Comparator.naturalOrder());
        return ((Float)values.get(0));
    }
    
    public static <T extends Number & Comparable<? super T>> float getMaximumValue(List<T> values) {
        Collections.sort(values, Comparator.naturalOrder());
        return ((Float)values.get(values.size()-1));
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
    
    /**
     * The code you provided appears to calculate the entropy for values between 0 and 1 using a specified range of pre-defined (e.g. 0.05)
     * 
     * It's important to note that the code assumes the input values are between 0 and 1, 
     * and it assumes that the values list contains valid numerical data.


     * @param <T>
     * @param values
     * @return Entropy value
     */
    public static <T extends Number & Comparable<? super T>> float calculateEntropy(List<T> values) {
        float range = 0.05f;
        int sizeRanges = ((int) (1.0/ range)) +1; // Eg: 21 faixas de 0,05 (0.00-0.05, 0.05-0.10, ..., 0.95-1.00)
        float proportions[] = new float[sizeRanges];
        

        // Calculating the probability of occurrence of each event
        for(T value: values){
            int rangeValue = (int) ( ((Double)value) / range); // Identifica a faixa correspondente ao valor (ex: 0.24 / 0.05 = 4, faixa 4)
            proportions[rangeValue]++;
        }
        
        //Normalize proportions:
        int start = 0, end = 0;
        
        float sum = 0;
        for (int i = 0; i < proportions.length; i++) {
            if (proportions[i] > 0){
                if(start == 0){
                    start = i;
                    end = i;
                } else if(start != 0){
                    end = i;
                }
                proportions[i] /= values.size();
                sum += proportions[i];
            }
        }
        
        
        //Compute entropy using Shannon formule:
        double entropy = 0;
        
        for (int i = start; i <= end; i++) {
            System.out.print(i+": "+proportions[i]+", ");
            if(proportions[i] > 0)
                entropy += proportions[i] * (Math.log(proportions[i]) / Math.log(2));
        }
        entropy = -entropy;
        return (float)entropy;
    }
    
//     /**
//     * calculates the value of Quartil 1, i.e., 25% of data.
//     *
//     * @param values
//     * @return median value
//     */
//    public static <T extends Number & Comparable<? super T>> float calculateFirstQuartile(List<T> values) {
//        Collections.sort(values, Comparator.naturalOrder());
//        int size = values.size();
//        double position = (size + 1) * 0.25;
//        if (position == (int) position) {
//            // Position is a integer number?
//            int index = (int) position - 1;
//            return values.get(index).floatValue();
//        } else {
//            int lowerIndex = (int) position - 1;
//            int upperIndex = (int) Math.ceil(position) - 1;
//            float lowerValue = values.get(lowerIndex).floatValue();
//            float upperValue = values.get(upperIndex).floatValue();
//            return (lowerValue + upperValue) / 2;
//        }
//    }
    
    /**
     * calculates the value of Quartil quartile, i.e., 1st, 2nd or 3rd part of data.
     * @param <T> numeric values
     * @param values
     * @param quartile 1st, 2nd or 3rd quartile desired.
     * @return value regarding desired quartile.
     */
    public static <T extends Number & Comparable<? super T>> float calculateQuartile(List<T> values, float quartile) {
        Collections.sort(values, Comparator.naturalOrder());
        int size = values.size();
        float quartileProportion = 0.25f*quartile;
        double position = (size + 1) * quartileProportion;
        if (position == (int) position) {
            // Position is an integer number?
            int index = (int) position - 1;
            return values.get(index).floatValue();
        } else {
            int lowerIndex = (int) position - 1;
            int upperIndex = (int) Math.ceil(position) - 1;
            float lowerValue = values.get(lowerIndex).floatValue();
            float upperValue = values.get(upperIndex).floatValue();
            return (lowerValue + upperValue) / 2.0f;
        }
    }

}
