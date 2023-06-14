/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufsc.tests;


import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author vanes
 */
public class RunExperiment_Validation {

    public static String filename;
    public static String extension;
    public static String dir;

    public static void main(String[] args) throws IOException, ParseException, CloneNotSupportedException {

        dir = "datasets\\";
        dir += "RE\\";
//        filename = "Running_Example_v5";
        filename = args[0];
        extension = ".csv";

        
        //informando lista de att a ser forçados como categoricos, mesmo contendo números
        String[] lstCategoricalsPreDefined = {"price"};
        for (int i = 0; i < lstCategoricalsPreDefined.length; i++) {
            lstCategoricalsPreDefined[i] = lstCategoricalsPreDefined[i].toUpperCase();
        }
        
        String SEPARATOR = ",";
        
        String[] valuesNulls = {"-999"};

        
//        String[] lstIgnoreColumns = {"label","poi"};
        String[] lstIgnoreColumns = {"label"};
        for (int i = 0; i < lstIgnoreColumns.length; i++) {
            lstIgnoreColumns[i] = lstIgnoreColumns[i].toUpperCase();
        }

        float rc = Float.parseFloat(args[2]);
        float threshold_rv = Float.parseFloat(args[3]);
//        String patternDate = "yyyy-MM-dd HH:mm:SS.SSS";
        String patternDateIn = "?"; //For minutes time (integer value) inform '?' character
        

        if (args[1].equalsIgnoreCase("Z")) {
            //method R
                MATSG_TimeSeq_version3 method = new MATSG_TimeSeq_version3();
//            method.notConsiderNulls();
            method.setFilenameFullDataset(args[4]);
            method.execute(dir, filename, extension, lstCategoricalsPreDefined, SEPARATOR, valuesNulls, lstIgnoreColumns, patternDateIn, rc, threshold_rv);
        } 
        else {
            System.err.println("Argumento não encontrado: "+args[1]);
        }
        
        
    }

}
