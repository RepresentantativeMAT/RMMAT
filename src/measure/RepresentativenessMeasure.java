/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
public class RepresentativenessMeasure {

    String extension = ".CSV";
    String SEP_DIR = "/";
    String directory;
    String fileCompleteValidation;
    InputDataset inputTrajectories;
    MultipleAspectTrajectory representativeTrajectory;

    double avgSimilarity;
    double medianSimilarity;
    double SDSimilarity;

    public RepresentativenessMeasure(String dir, InputDataset inputTrajectories, MultipleAspectTrajectory representativeTrajectory) {
        directory = dir;
        fileCompleteValidation = directory + SEP_DIR + directory.substring(directory.lastIndexOf(SEP_DIR)) + " [Measures]" + extension;

        this.inputTrajectories = inputTrajectories;
        this.representativeTrajectory = representativeTrajectory;

    }

    public void computeRepresentativenessMeasure() throws ParseException, CloneNotSupportedException {

        if (representativeTrajectory.getPointList().isEmpty()) {
            System.out.println("RT is empty");

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
            measure.setThreshold("SPATIAL", (representativeTrajectory.getSpatialThreshold() * 2));
            

            if (!new File(fileCompleteValidation).exists()) {
                measure.setThreshold("TIME", 60);
                inputTrajectories.similarityDataset(measure);
            }
//            measure.setThreshold("TIME", (int) representativeTrajectory.getTemporalDifAVG());

            List<Double> listValues = new ArrayList<>();

            for (MultipleAspectTrajectory eachTraj : inputTrajectories.getListTrajectories()) {

                listValues.add(measure.similarityOf(representativeTrajectory, eachTraj));

            }
            //after computed measure with each T and RT, it is computed median value

            avgSimilarity = Util.calculateAverage(listValues);
            medianSimilarity = Util.calculateMedian(listValues);
            SDSimilarity = Util.calculateStandardDesviation(listValues);

            writeSimilarityInfo();

        }
    }

    public void writeSimilarityInfo() {
        try {

            CSVWriter valWriter;
            if (!new File(fileCompleteValidation).exists()) {

                valWriter = new CSVWriter(fileCompleteValidation);
                valWriter.writeLine("|Ground Truth|, |Points|, AVG, Median, SD, "
                        + "SelectedMoreSimilar, AVG Selected, Median Selected, SD Selected");
                valWriter.writeLine(inputTrajectories.getListTrajectories().size() + ", "
                        + inputTrajectories.getSizePoints() + ", "
                        + inputTrajectories.getAvgSimilarity() + ", "
                        + inputTrajectories.getMedianSimilarity() + ", "
                        + inputTrajectories.getSDSimilarity() + ", "
                        + inputTrajectories.getMoreSimilar().getId() + ", "
                        + inputTrajectories.getAvgMoreSimilarity() + ", "
                        + inputTrajectories.getMedianMoreSimilarity() + ", "
                        + inputTrajectories.getSDMoreSimilarity());

                valWriter.writeLine("tauRC, tauRV, "
                        + "|coverPoints|, |coverTraj|, "
                        + "avg, median, SD");

                valWriter.writeLine(
                        representativeTrajectory.getRcThreshold() + ", "
                        + representativeTrajectory.getRvThreshold() + ", "
                        + representativeTrajectory.getCoverPoints() + ", "
                        + representativeTrajectory.getCoverTrajectories() + ", "
                        + avgSimilarity + ", "
                        + medianSimilarity + ", "
                        + SDSimilarity);

            } else {
                valWriter = new CSVWriter(fileCompleteValidation, true);
                valWriter.writeLine(
                        representativeTrajectory.getRcThreshold() + ", "
                        + representativeTrajectory.getRvThreshold() + ", "
                        + representativeTrajectory.getCoverPoints() + ", "
                        + representativeTrajectory.getCoverTrajectories() + ", "
                        + avgSimilarity + ", "
                        + medianSimilarity + ", "
                        + SDSimilarity);
            }

            valWriter.flush();
            valWriter.close();
        } catch (IOException e) {
            System.err.println("Error on writing: " + e.toString());
        }
    }

}
