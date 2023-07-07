/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufsc.tests;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import measure.LoadData;

/**
 *
 * @author vanes
 */
public class RunExperiment {

    public static String filenameRT;
    public static String filenameDataset;
    public static String extension;
    public static String dir;
    public static String sepDir;

    public static void main(String[] args) throws IOException, ParseException, CloneNotSupportedException {

        sepDir = "/";
        dir = "datasets"+sepDir;
        dir += "RE";
        filenameRT = "Running_Example_v5";
        filenameDataset = "Running_Example_v5";
//        filename = args[0];
        extension = ".csv";

        //informando lista de att a ser forçados como categoricos, mesmo contendo números
        String[] lstCategoricalsPreDefined = {"price"};
        for (int i = 0; i < lstCategoricalsPreDefined.length; i++) {
            lstCategoricalsPreDefined[i] = lstCategoricalsPreDefined[i].toUpperCase();
        }

        String SEPARATOR = ",";

        String[] valuesNulls = {"-1"};

        String[] lstIgnoreColumns = null;

//        String patternDate = "yyyy-MM-dd HH:mm:SS.SSS";
        String patternDateIn = "?"; //For minutes time (integer value) inform '?' character

        LoadData measure = new LoadData();
        measure.execute(dir, filenameRT, filenameDataset, extension, sepDir, lstCategoricalsPreDefined, SEPARATOR, valuesNulls, lstIgnoreColumns, patternDateIn);

    }
    
    

}
