/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufsc.tests;

import java.io.IOException;
import java.text.ParseException;
import measure.LoadData;

/**
 *
 * @author vanes
 */
public class RunExperimentPisaMUITAS {

    public static String filenameRT;
    public static String filenameDataset;
    public static String extension;
    public static String dir;
    public static String sepDir;

    public static void main(String[] args) throws IOException, ParseException, CloneNotSupportedException {

        sepDir = "/";
        dir = "datasets"+sepDir;
        dir += "Pisa";
        filenameRT = "pisa_muitas_26";
        filenameDataset = "pisa_muitas_26";
//        filename = args[0];
        extension = ".csv";


        String SEPARATOR = ",";

        String[] valuesNulls = {"Unknown"};

//        String[] lstIgnoreColumns = null;
        String[] lstIgnoreColumns = {"label"};
        for (int i = 0; i < lstIgnoreColumns.length; i++) {
            lstIgnoreColumns[i] = lstIgnoreColumns[i].toUpperCase();
        }

        String patternDateIn = "yyyy-MM-dd HH:mm:SS";
        
        String[] lstCategoricalsPreDefined = null;

        LoadData measure = new LoadData();
        measure.execute(dir, filenameRT, filenameDataset, extension, sepDir, lstCategoricalsPreDefined, SEPARATOR, valuesNulls, lstIgnoreColumns, patternDateIn);

    }
    
    

}
