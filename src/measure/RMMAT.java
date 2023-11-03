/*

 */
package measure;

import br.ufsc.model.InputDataset;
import br.ufsc.model.MultipleAspectTrajectory;
import br.ufsc.model.SemanticAspect;
import br.ufsc.model.SemanticType;
import br.ufsc.util.CSVWriter;
import br.ufsc.util.Util;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vanessalagomachado
 */
public class RMMAT {

    String extension = ".CSV";
    String SEP_DIR;
    String directory;
    String fileCompleteValidation;
    
    int tauSimSpatial;
    int tauSimTemporal;
    
    InputDataset inputTrajectories;
    MultipleAspectTrajectory representativeTrajectory;

    double avgSimilarity;
    double medianSimilarity;
    double SDSimilarityAVG;
    double SDSimilarityMedian;
    float entropyRT;
    float firstQuartile;
    float thirdQuartile;
    float sizeForthQuartile;
    float sizeThirdQuartile;
    float sizeSecondQuartile;
    float sizeFirstQuartile;

    public RMMAT(String dir, String file, InputDataset inputTrajectories, MultipleAspectTrajectory representativeTrajectory) {
        directory = dir;
        SEP_DIR = "\\";
        
        tauSimSpatial = 4;
        tauSimTemporal = 30;
//        fileCompleteValidation = directory + SEP_DIR + directory.substring(directory.lastIndexOf(SEP_DIR)) + " [Measures]" + extension;
        
        String lastDir = file.substring(0, file.lastIndexOf(SEP_DIR));
        
        String fileMeasure = "";
        if(file.contains(SEP_DIR)){
            if (file.indexOf(SEP_DIR) != file.lastIndexOf(SEP_DIR)){
                fileMeasure = file.substring(file.indexOf(SEP_DIR), file.lastIndexOf(SEP_DIR));
                fileMeasure = fileMeasure.substring(fileMeasure.lastIndexOf(" ")); 
            }
            else {
                fileMeasure = file.substring(file.trim().indexOf(SEP_DIR));
                fileMeasure = fileMeasure.substring(0, fileMeasure.trim().indexOf(" "));
            }
                
        } 

        fileMeasure += "[Measures][times "+tauSimSpatial+"]";
        fileCompleteValidation = directory + SEP_DIR + lastDir + SEP_DIR + fileMeasure.trim() + extension;
       
        this.inputTrajectories = inputTrajectories;
        this.representativeTrajectory = representativeTrajectory;

    }

    public void computeRepresentativenessMeasure() throws ParseException, CloneNotSupportedException {

        if (representativeTrajectory.getPointList().isEmpty()) {
            System.err.println("RT is empty");

        } else {

//        MUITAS_v2 measure = new MUITAS_v2();
            MUITAS measure = new MUITAS();
            //Computing and setting thresholds
            //3Dimensions with equal weight (0.33) ~
            measure.setWeight("SPATIAL", 0.34f);
            measure.setWeight("TIME", 0.33f);

            float auxWeight = 0.33f / (inputTrajectories.getListAttributes().size());

//        System.out.println("Atributes: " + attributes);
            for (SemanticAspect eachAtt : inputTrajectories.getListAttributes()) {
                measure.setWeight(eachAtt, auxWeight);
                if (eachAtt.getType().equals(SemanticType.NUMERICAL)) {
                    measure.setThreshold(eachAtt, 10);
                }
            }

            //add spatial threshold -- Test: spatialThreshold x 2 -- pensando em atingir a distância de até 2 células
            measure.setThreshold("SPATIAL", (representativeTrajectory.getSpatialThreshold() * tauSimSpatial ));
//            measure.setThreshold("SPATIAL", (representativeTrajectory.getSpatialThreshold() ));
            

            if (!new File(fileCompleteValidation).exists()) {
                while (tauSimTemporal <= 60){ // iniciando o tau em 30, para calcular as opções 30, 45 e 60
                measure.setThreshold("TIME", tauSimTemporal);
//                measure.setThreshold("TIME", (int) representativeTrajectory.getTemporalDifAVG());
                inputTrajectories.similarityDataset(measure);
                    writeSimilarityInfoDataset();
                    tauSimTemporal += 15;
                }
            }
//            measure.setThreshold("TIME", (int) representativeTrajectory.getTemporalDifAVG());

            List<Double> listValues = new ArrayList<>();

            for (MultipleAspectTrajectory eachTraj : inputTrajectories.getListTrajectories()) {

                listValues.add(measure.similarityOf(representativeTrajectory, eachTraj));

            }
            //after computed measure with each T and RT, it is computed median value

            avgSimilarity = Util.calculateAverage(listValues);
            medianSimilarity = Util.calculateMedian(listValues);
            SDSimilarityAVG = Util.calculateStandardDesviationAVG(listValues);
            SDSimilarityMedian = Util.calculateStandardDesviationMedian(listValues);
            entropyRT = Util.calculateEntropy(listValues);
            
            firstQuartile = Util.calculateQuartile(listValues, 1);
            thirdQuartile = Util.calculateQuartile(listValues, 3);
            
            for(double value: listValues){
                if(value <= firstQuartile)
                    sizeFirstQuartile++;
                else if (value <= medianSimilarity)
                    sizeSecondQuartile++;
                else if(value <= thirdQuartile)
                    sizeThirdQuartile++;
                else
                    sizeForthQuartile++;
            }
            
            //Normalize step
            sizeFirstQuartile /= listValues.size();
            sizeSecondQuartile /= listValues.size();
            sizeThirdQuartile /= listValues.size();
            sizeForthQuartile /= listValues.size();
            
            writeSimilarityInfoRT();

        }
    }

    public void writeSimilarityInfoDataset() {
        try {

            CSVWriter valWriter;
            if (!new File(fileCompleteValidation).exists()) {

                valWriter = new CSVWriter(fileCompleteValidation);
                valWriter.writeLine("tauRC,tauRV,|coverTraj|,|CoverPoints|,"
                        + "AVG,SD AVG,Median,SD Median,"
                        + "Q1 value,Q3 value,"
                        + "Q1 prop,Q2 prop,Q3 prop,Q4 prop,Entropy,"
                        + "label,tauSimSpatial,tauSimTemporal,|size|");
                valWriter.writeLine("-999,-999,"+
                        inputTrajectories.getListTrajectories().size() + "," // cover traj \ size traj. in D
                        + inputTrajectories.getSizePoints() + "," // Cover Points \ size of all points
                        + inputTrajectories.getAvgSimilarity() + "," // AVG
                        + inputTrajectories.getSDSimilarityAVG() + "," // SD AVG
                        + inputTrajectories.getMedianSimilarity() + "," // Median
                        + inputTrajectories.getSDSimilarityMedian() + "," // SD Median
                        + "," // 1st Q
                        + "," // 3rd Q
                        + "," // size 1st Q
                        + "," // size 2nd Q
                        + "," // size 3rd Q
                        + "," // size 4th Q
                        + "," // Entropy
                        + "D,"+ // label
                        tauSimSpatial + ","+
                        tauSimTemporal+ ","+
                        inputTrajectories.getSizePoints());
                valWriter.writeLine("-999,-999,-999,-999,"
                        + inputTrajectories.getAvgMoreSimilarity() + ","
                        + inputTrajectories.getSDMoreSimilarityAVG() + ","
                        + inputTrajectories.getMedianMoreSimilarity() + ","
                        + inputTrajectories.getSDMoreSimilarityMedian() + ","
                        + "," // 1st Q
                        + "," // 3rd Q
                        + "," // size 1st Q
                        + "," // size 2nd Q
                        + "," // size 3rd Q
                        + "," // size 4th Q
                        + "," // Entropy
                       + inputTrajectories.getMoreSimilar().getId() + "," // label
                       + tauSimSpatial + ","
                       + tauSimTemporal+ ","
                       + inputTrajectories.getMoreSimilar().getPointList().size()); // |size| more similar T
            } else {
                valWriter = new CSVWriter(fileCompleteValidation, true);
               valWriter.writeLine("-999,-999,"+
                        inputTrajectories.getListTrajectories().size() + "," // cover traj \ size traj. in D
                        + inputTrajectories.getSizePoints() + "," // Cover Points \ size of all points
                        + inputTrajectories.getAvgSimilarity() + "," // AVG
                        + inputTrajectories.getSDSimilarityAVG() + "," // SD AVG
                        + inputTrajectories.getMedianSimilarity() + "," // Median
                        + inputTrajectories.getSDSimilarityMedian() + "," // SD Median
                        + "," // 1st Q
                        + "," // 3rd Q
                        + "," // size 1st Q
                        + "," // size 2nd Q
                        + "," // size 3rd Q
                        + "," // size 4th Q
                        + "," // Entropy
                        + "D,"+ // label
                        tauSimSpatial + ","+
                        tauSimTemporal+ ","+
                        inputTrajectories.getSizePoints());
                valWriter.writeLine("-999,-999,-999,-999,"
                        + inputTrajectories.getAvgMoreSimilarity() + ","
                        + inputTrajectories.getSDMoreSimilarityAVG() + ","
                        + inputTrajectories.getMedianMoreSimilarity() + ","
                        + inputTrajectories.getSDMoreSimilarityMedian() + ","
                        + "," // 1st Q
                        + "," // 3rd Q
                        + "," // size 1st Q
                        + "," // size 2nd Q
                        + "," // size 3rd Q
                        + "," // size 4th Q
                        + "," // Entropy
                       + inputTrajectories.getMoreSimilar().getId() + "," // label
                       + tauSimSpatial + ","
                       + tauSimTemporal+ ","
                       + inputTrajectories.getMoreSimilar().getPointList().size()); // |size| more similar T
            }

            valWriter.flush();
            valWriter.close();
        } catch (IOException e) {
            System.err.println("Error on writing: " + e.toString());
        }
    }
    
     public void writeSimilarityInfoRT() {
        try {

            CSVWriter valWriter;
            if (!new File(fileCompleteValidation).exists()) {
                System.err.println("Error to compute and write dataset similarity info. "); 
                return;
            } else {
                
                valWriter = new CSVWriter(fileCompleteValidation, true);
                

                valWriter.writeLine(
                        representativeTrajectory.getRcThreshold() + ","
                        + representativeTrajectory.getRvThreshold() + ","
                        + representativeTrajectory.getCoverTrajectories() + ","
                        + representativeTrajectory.getCoverPoints() + ","
                        + avgSimilarity + ","
                        + SDSimilarityAVG + ","
                        + medianSimilarity + ","
                        + SDSimilarityMedian + ","
                        + firstQuartile+ "," // 1st Q
                        + thirdQuartile+ "," // 3rd Q
                        + sizeFirstQuartile+ "," // size 1st Q
                        + sizeSecondQuartile+ "," // size 2nd Q
                        + sizeThirdQuartile+ "," // size 3rd Q
                        + sizeForthQuartile+ "," // size 4th Q
                        + entropyRT+ "," // Entropy
                        + "RT," // label
                        + tauSimSpatial +"," // tau Sim Spatial
                        + "," // tau Sim Temporal
                        + representativeTrajectory.getPointList().size()); // |size|
            }

            valWriter.flush();
            valWriter.close();
        } catch (IOException e) {
            System.err.println("Error on writing: " + e.toString());
        }
    }
     
     
     
    
}
