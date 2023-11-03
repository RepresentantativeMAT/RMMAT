/*

Main Class with the core... and version only modify what is necessary

version 1: 
    Match Semantic for categorical type is considered match (1) when the rank value RT contain the value of the input traj


version 2: 
    Match Semantic for categorical type is used % proportion of the relative value

 */
package measure;

import br.ufsc.model.AttributeValue;
import br.ufsc.model.Centroid;
import br.ufsc.model.MultipleAspectTrajectory;
import br.ufsc.model.Point;
import br.ufsc.model.STI;
import br.ufsc.model.SemanticAspect;
import br.ufsc.util.Util;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vanes
 */
public class MUITAS {

    protected Map<Object, Float> weights;
    protected Map<Object, Float> thresholds;

    protected double parityT1T2 = 0;
    protected double parityT2T1 = 0;

    public MUITAS() {
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
                scores[i][j] = this.score(t1.getPointList().get(i), t2.getPointList().get(j));
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

    private final double score(Point pointT1, Point p2) throws ParseException {
        double score = 0;

        if (pointT1 instanceof Centroid) {
            Centroid p1 = (Centroid) pointT1;
            //Spatial match:
            if (Util.euclideanDistance(p1, p2) <= getThreshold("SPATIAL")) {
                score += (getWeight("SPATIAL"));
            }

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
        } else {
            //Spatial match:
            if (Util.euclideanDistance(pointT1, p2) <= getThreshold("SPATIAL")) {
                score += (getWeight("SPATIAL"));
            }
            //Temporal match:
            if (Util.minutesDiference(pointT1.getTime().getStartTime(), p2.getTime().getStartTime()) <= getThreshold("TIME")) {
                score += (getWeight("TIME"));
            }
            // Semantic match:
            for (AttributeValue atvP1 : pointT1.getListAttrValues()) {
                AttributeValue tempAttP2 = atvP1.getAttibute() != null ? p2.getAttributeValue(atvP1.getAttibute()) : null;

                double tempSemanticMatch = computeMatch(atvP1, tempAttP2);
                score += tempSemanticMatch;

            }
        }
        return score;
    }

    public double computeMatch(AttributeValue rep, AttributeValue atv) {
        double match = 0;

        if (atv == null || rep == null) {
//            System.out.println("Entrou na analise do valor");
            return 0;
        } else if (rep.getValue() == null){
            if (atv.getValue() == null)
                return 1;
            else 
                return 0;
        }

        if (null == rep.getAttibute().getType()) {
            System.err.println("Attribute Type not identified: " + rep.getAttibute().getName() + " = " + rep.getValue());
        } else {
            switch (rep.getAttibute().getType()) {
                case NUMERICAL:
                    double numP1 = (Double) atv.getValue();
                    try {

                        double numRep = (Double) rep.getValue();
                        if (numP1 < 0 && numRep < 0) {
                            match = 1;
                        } else if (numP1 < 0 || numRep < 0) {
                            match = 0;
                        } else {
                            match = Math.abs(numP1 - numRep) <= getThreshold(atv.getAttibute()) ? 1.0 : 0;
                        }
                    } catch (Exception e) {
//                        System.err.println("Error with match Numerical type: att: " + atv.getAttibute()
//                                + " - value RT: " + rep.getValue()
//                                + "\n--" + e);
//                        System.out.println("Class value: " + rep.getValue().getClass());
                        Map<String, Double> valuesNumMap = (HashMap) rep.getValue();

                        // Pattern {nullValue: Proport; repValue: propor}
                        String keyNull = "-999.0";
                        for (String keyNumValue : valuesNumMap.keySet()) {
                            if (keyNumValue.equalsIgnoreCase(keyNull)) {
                                if (numP1 < 0) {
//                                        match = valuesNumMap.get(keyNull);
                                    match = 1;
                                }
                            } else {
                                double numRep = Double.parseDouble(keyNumValue);
                                match = Math.abs(numP1 - numRep) <= getThreshold(atv.getAttibute()) ? 1.0 : 0;
                            }
                        }

                    }
//                } else {
//                }
                    break;
                case CATEGORICAL:
                // case of semantic - categorical
                try {
                    Map<String, Double> valuesRT = (HashMap) rep.getValue();
                    if (valuesRT.containsKey(((String) atv.getValue()).toUpperCase())) {
                        match = 1;
                    }
                } catch (Exception e) {
                    match = rep.getValue().equals(atv.getValue()) ? 1.0 : 0.0;
                }
                break;
                default:
                    System.err.println("Attribute Type not identified> " + rep.getAttibute().getName() + " = " + rep.getValue());
                    break;
            }
        }

        return match * getWeight(rep.getAttibute());
    }

    
    
}
