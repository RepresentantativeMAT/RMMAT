/*


 */
package br.ufsc.model;

import br.ufsc.util.Util;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import measure.MUITAS;

/**
 *
 * @author vanessalagomachado
 */
public class InputDataset {
    private List<MultipleAspectTrajectory> listTrajectories;
    private double avgSimilarity;
    private double medianSimilarity;
    private double SDSimilarityAVG;
    private double SDSimilarityMedian;
    private List<SemanticAspect> attributes;
    private int sizePoints; 
    
    private MultipleAspectTrajectory moreSimilar;
    private double avgMoreSimilarity;
    private double medianMoreSimilarity;
    private double SDMoreSimilarityAVG;
    private double SDMoreSimilarityMedian;

    public InputDataset(List<MultipleAspectTrajectory> trajectories) {
        listTrajectories = trajectories;
    }

    public InputDataset() {
        listTrajectories = new ArrayList<>();
        attributes = new ArrayList<>();
    }
    

    public List<MultipleAspectTrajectory> getListTrajectories() {
        return listTrajectories;
    }

    public void setListTrajectories(List<MultipleAspectTrajectory> listTrajectories) {
        this.listTrajectories = listTrajectories;
    }
    
    
    public List<SemanticAspect> getListAttributes() {
        return attributes;
    }

    public void setListAttributes(List<SemanticAspect> listAttributes) {
        this.attributes = listAttributes;
    }

    public double getAvgSimilarity() {
        return avgSimilarity;
    }

    public void setAvgSimilarity(double avgSimilarity) {
        this.avgSimilarity = avgSimilarity;
    }

    public double getMedianSimilarity() {
        return medianSimilarity;
    }

    public void setMedianSimilarity(double medianSimilarity) {
        this.medianSimilarity = medianSimilarity;
    }


    public MultipleAspectTrajectory getMoreSimilar() {
        return moreSimilar;
    }

    public void setMoreSimilar(MultipleAspectTrajectory moreSimilar) {
        this.moreSimilar = moreSimilar;
    }

    public double getAvgMoreSimilarity() {
        return avgMoreSimilarity;
    }

    public void setAvgMoreSimilarity(double avgMoreSimilarity) {
        this.avgMoreSimilarity = avgMoreSimilarity;
    }

    public double getMedianMoreSimilarity() {
        return medianMoreSimilarity;
    }

    public void setMedianMoreSimilarity(double medianMoreSimilarity) {
        this.medianMoreSimilarity = medianMoreSimilarity;
    }


    
    /**
     * Compute all data regarding similarity measures of input Dataset using Measure parameter 
     * 
     * Compute avg, median and Standard Desviation of all input trajectories in dataset
     * Compute the most similar trajectory between all other ones in dataset
     * ** The most similar is defined as the one that have the better avg metric
     * 
     * Compute avg, median and standard desviation of the similarity with each other trajectory in input dataset
     * 
     * @param measure SimilarityMeasure -- patter measure used: MUITAS
     * @throws ParseException
     * @throws CloneNotSupportedException 
     */
    public void clearObjects(){
        avgSimilarity = 0;
        medianSimilarity = 0;
        SDSimilarityAVG = 0;
        SDSimilarityMedian = 0;
        
        medianMoreSimilarity = 0;
        SDMoreSimilarityAVG = 0;
        SDMoreSimilarityMedian = 0;
        avgMoreSimilarity = 0;
        moreSimilar = null;
    }
    public void similarityDataset(MUITAS measure) throws ParseException, CloneNotSupportedException {
        clearObjects();
        double avgAuxSim = 0;

        List<Double> measuresT = new ArrayList<>();
        
        List<Double> measuresMoreSimilar = new ArrayList<>();
        List<Double> auxMasuresMoreSimilar = new ArrayList<>();
        
        // for each pair in T 
        for (MultipleAspectTrajectory t1 : listTrajectories) {
            for (MultipleAspectTrajectory t2 : listTrajectories) {
                // Compute similarity between each t in T
                measuresT.add(measure.similarityOf(t1, t2));
                auxMasuresMoreSimilar.add(measuresT.get(measuresT.size() - 1));
//                System.out.println(measuresT.get(measuresT.size() - 1)+", ");
            }
//            System.out.println("");
            avgAuxSim = Util.calculateAverage(auxMasuresMoreSimilar);
            if (avgAuxSim > avgMoreSimilarity) {
                avgMoreSimilarity = avgAuxSim;
                moreSimilar = (MultipleAspectTrajectory) t1.clone();
                
                measuresMoreSimilar = new ArrayList<>(auxMasuresMoreSimilar);
            }

            auxMasuresMoreSimilar.clear();
        }
        
        
        avgSimilarity = Util.calculateAverage(measuresT);
        medianSimilarity = Util.calculateMedian(measuresT);
        SDSimilarityAVG = Util.calculateStandardDesviationAVG(measuresT);
        SDSimilarityMedian = Util.calculateStandardDesviationMedian(measuresT);
        
        medianMoreSimilarity = Util.calculateMedian(measuresMoreSimilar);
        SDMoreSimilarityAVG = Util.calculateStandardDesviationAVG(measuresMoreSimilar);
        SDMoreSimilarityMedian = Util.calculateStandardDesviationMedian(measuresMoreSimilar);
        
        measuresT.clear();
        measuresMoreSimilar.clear();
        
        
    }

    
    
    public void addTrajectory(MultipleAspectTrajectory traj){
        listTrajectories.add(traj);
    }
    
    
    public void removeTrajectory(MultipleAspectTrajectory traj){
        listTrajectories.remove(traj);
    }
    
        public void addAttribute(SemanticAspect attr){
        attributes.add(attr);
    }
    
    
    public void removeAttribute(SemanticAspect attr){
        attributes.remove(attr);
    }

    public int getSizePoints() {
        return sizePoints;
    }

    public void setSizePoints(int sizePoints) {
        this.sizePoints = sizePoints;
    }
    
    public MultipleAspectTrajectory getTrajectorybyId(int id){
        MultipleAspectTrajectory auxT = new MultipleAspectTrajectory(id);
        if(listTrajectories.contains(auxT)){
            for(MultipleAspectTrajectory eachT: listTrajectories){
                if(eachT.equals(auxT))
                    return eachT;
            }
        }
        return null;
    }

    public double getSDSimilarityAVG() {
        return SDSimilarityAVG;
    }

    public void setSDSimilarityAVG(double SDSimilarityAVG) {
        this.SDSimilarityAVG = SDSimilarityAVG;
    }

    public double getSDSimilarityMedian() {
        return SDSimilarityMedian;
    }

    public void setSDSimilarityMedian(double SDSimilarityMedian) {
        this.SDSimilarityMedian = SDSimilarityMedian;
    }

    public double getSDMoreSimilarityAVG() {
        return SDMoreSimilarityAVG;
    }

    public void setSDMoreSimilarityAVG(double SDMoreSimilarityAVG) {
        this.SDMoreSimilarityAVG = SDMoreSimilarityAVG;
    }

    public double getSDMoreSimilarityMedian() {
        return SDMoreSimilarityMedian;
    }

    public void setSDMoreSimilarityMedian(double SDMoreSimilarityMedian) {
        this.SDMoreSimilarityMedian = SDMoreSimilarityMedian;
    }
    
}
