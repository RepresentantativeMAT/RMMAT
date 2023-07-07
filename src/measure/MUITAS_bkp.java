/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package measure;

import br.ufsc.model.AttributeValue;
import br.ufsc.model.Centroid;
import br.ufsc.model.MultipleAspectTrajectory;
import br.ufsc.model.Point;
import br.ufsc.model.STI;
import br.ufsc.model.SemanticAspect;
import br.ufsc.model.SemanticType;
import br.ufsc.util.Util;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vanes
 */
public class MUITAS_bkp {

    private Map<Object, Float> weights;
    private Map<Object, Float> thresholds;

    private double parityT1T2 = 0;
    private double parityT2T1 = 0;

    public MUITAS_bkp() {
        this.weights = new HashMap<Object, Float>();
        this.thresholds = new HashMap<Object, Float>();
    }

    public void clear() {
        this.weights.clear();
        this.thresholds.clear();
    }

    public void setThreshold(Object att, float threshold) {
        this.thresholds.put(att, threshold);
    }

    public double getThreshold2(Object att) {
        if (thresholds.isEmpty()) {
            System.err.println("threshold list is Empty");
        }

        try {
            return this.thresholds.get(att);
        } catch (Exception e) {
            if (att instanceof SemanticAspect) {
                System.err.println("Error in getThreshold for attribute: '" + ((SemanticAspect) att).getName() + "' (thresholds: " + this.thresholds + ")");
            } else {
                System.err.println("ERROR in getThreshold for spatial data: " + e.getMessage());
            }
            throw new NullPointerException();
        }
    }

    public double getThreshold(Object att) {
        if (thresholds.isEmpty()) {
            System.err.println("Threshold list is empty");
            throw new IllegalStateException("Threshold list is empty");
        }

        try {
            if (thresholds.containsKey(att)) {
                return thresholds.get(att);
            } else {
                System.err.println("Threshold not found for attribute: '" + att + "'");
                throw new IllegalArgumentException("Threshold not found for attribute: '" + att + "'");
            }

        } catch (Exception e) {
            System.err.println("Invalid attribute type");
            throw new IllegalArgumentException("Invalid attribute type");

        }
    }

    public void getAllThreshold() {

        System.out.println("Thresholds: ");
        for (Map.Entry<Object, Float> eachTau : thresholds.entrySet()) {
            System.out.println("key: " + eachTau.getKey() + " | Value: " + eachTau.getValue());

        }
    }

    public void setWeight(Object attribute, float weight) {
        this.weights.put(attribute, weight);
    }

    public double getWeight(Object attribute) {
        try {
            if (attribute instanceof STI) {
                return this.weights.get("TIME");
            } else {
                return this.weights.get(attribute);
            }
        } catch (Exception e) {
            System.err.println("Error in getWeight for feature: '" + attribute + "' (weights: " + this.weights + ")");
            throw new NullPointerException();
        }
    }

    public float getAllWeight() {
        float sumWeight = 0.0f;

        for (Map.Entry<Object, Float> eachWeight : weights.entrySet()) {
            sumWeight += eachWeight.getValue();

        }
        return sumWeight;
    }

    public double getParityT1T2() {
        return parityT1T2;
    }

    public double getParityT2T1() {
        return parityT2T1;
    }

    public double similarityOf(MultipleAspectTrajectory t1, MultipleAspectTrajectory t2) throws ParseException {
        parityT1T2 = 0;
        parityT2T1 = 0;
        double[][] scores = new double[t1.getPointList().size()][t2.getPointList().size()];

        for (int i = 0; i < t1.getPointList().size(); i++) {
            double maxScoreRow = 0;

            for (int j = 0; j < t2.getPointList().size(); j++) {
                scores[i][j] = this.score((Centroid) t1.getPointList().get(i), t2.getPointList().get(j));
                maxScoreRow = scores[i][j] > maxScoreRow ? scores[i][j] : maxScoreRow;
            }

            parityT1T2 += maxScoreRow;

        }
        for (int j = 0; j < t2.getPointList().size(); j++) {
            double maxCol = 0;

            for (int i = 0; i < t1.getPointList().size(); i++) {

                maxCol = scores[i][j] > maxCol ? scores[i][j] : maxCol;
            }

            parityT2T1 += maxCol;
        }
        return (parityT1T2 + parityT2T1) / (t1.getPointList().size() + t2.getPointList().size());

    }

    private final double score(Centroid p1, Point p2) throws ParseException {
        double score = 0;

//        System.out.println("RP: \n"+p1);
        //Spatial match:
        if (Util.euclideanDistance(p1, p2) <= getThreshold("SPATIAL")) {
            score += (getWeight("SPATIAL"));
        }
//        System.out.println("Spatial score: "+score);
        matchTemporal:
        {
            // Trajectory with only one STI
            double match = 0;
            if (!p1.getListSTI().isEmpty() && p1.getListSTI().size() == 1) {
                STI sti = p1.getListSTI().get(0);
                match = sti.getInterval().isInInterval(p2.getTime().getStartTime()) ? 1 : 0;
            }
            score += match * getWeight("TIME");

        }

        for (AttributeValue atvP1 : p1.getListAttrValues()) {
            AttributeValue tempAttP2 = atvP1.getAttibute() != null ? p2.getAttributeValue(atvP1.getAttibute()) : null;

            double tempSemanticMatch = computeMatch(atvP1, tempAttP2);
            score += tempSemanticMatch;

        }
        return score;
    }

    public double computeMatch(AttributeValue rep, AttributeValue atv) {
        double match = 0;

        if (atv == null || rep == null) {
            return 0;
        }
        
        if(rep.getAttibute().getType() == SemanticType.NUMERICAL){
            match = Math.abs((Double) rep.getValue() - (Double) atv.getValue()) <= getThreshold(atv.getAttibute()) ? 1.0 : 0;
        
        } else if(rep.getAttibute().getType() == SemanticType.CATEGORICAL){
            // case of semantic - categorical
            Map<String, Double> valuesRT = (HashMap) rep.getValue();
            if (valuesRT.containsKey(((String) atv.getValue()).toUpperCase())) {
                match = 1;
            }
        } else {
            System.err.println("Attribute Type not identified> "+rep.getAttibute().getName()+" = "+rep.getValue());
        }
        
//        System.out.println("Value to be compared - RT:  "+rep.getAttibute().getName()+" = "+rep.getValue()+" >> type: "+rep.getValue().getClass());
//        System.out.println("Value to be compared - T:  "+atv.getAttibute().getName()+" = "+atv.getValue()+" >> type: "+atv.getValue().getClass());
//        System.out.println("Match: "+match+" - Score: "+match * getWeight(rep.getAttibute()));
        return match * getWeight(rep.getAttibute());
    }

}
