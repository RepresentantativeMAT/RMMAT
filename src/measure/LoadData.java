/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package measure;

import br.ufsc.model.AttributeValue;
import br.ufsc.model.Centroid;
import br.ufsc.model.InputDataset;
import br.ufsc.model.MultipleAspectTrajectory;
import br.ufsc.model.Point;
import br.ufsc.model.SemanticAspect;
import br.ufsc.model.SemanticType;
import br.ufsc.util.CSVWriter;
import br.ufsc.util.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vanessalagomachado
 */
public class LoadData {
    // Attributes 

    // ------------- to Spatial division -- Dataset file information
    private String filenameRT; //Filename of the RT
    private String filenameDataset; //Filename of the dataset
    private String directory;//Directory of the dataset
    private String extension; //Extension of the filename
    private String sepDir; //Separator of Operational System regarding path file

    private String SEPARATOR;

    // --- Define initial index value to semantic attributes
    private int INDEX_SEMANTIC = 3;
//    private boolean considerNulls = true;

    // For loading information from the dataset
//    private static List<SemanticAspect> attributes; //List of all diferent attributes found in the dataset
    // --------------------- AUX ----------------------
    private static int rId;
    private static String auxTid;
    private static MultipleAspectTrajectory trajectory; //Contain all points of a MAT -- used to add points into MAT 

    // --------------- to determine categoricals pre-defined values
    List<String> lstCategoricalsPD;
    List<String> lstIgnoreCols = null;
    String[] valuesNulls; //witch values are considered null in input dataset?

    // To model trajectory data
    private static MultipleAspectTrajectory representativeTrajectory;
//    private static List<MultipleAspectTrajectory> listTrajectories; //List of all MATs in the dataset
    private InputDataset inputTrajectories;

    // format of input data
    private static SimpleDateFormat formatDate;
    private boolean dailyInfo = false;

    // threshold match temporal for use in compute similarity between all t in T
    int tauTime = 0;

    // mapping info
    Map<Integer, BitSet> mappingData;

    public void execute(String dir, String fileRT, String fileDataset, String ext, String sepDir, String[] lstCategoricalPD, String SEPARATOR, String[] valuesNULL, String[] ignoreColumns, String patternDateInput) throws IOException, ParseException, CloneNotSupportedException {

        initialize(dir, fileRT, fileDataset, ext, sepDir, lstCategoricalPD, SEPARATOR, valuesNULL, ignoreColumns, patternDateInput);

        // Load dataset follow data model representation
        loadDataset();

        rId = 0;

        loadRepresentativeMAT();

        //Only compute measure when have RT
        if (!representativeTrajectory.getPointList().isEmpty()) {

            representativeTrajectory.setCoverTrajectories(mappingData.size());

//        System.out.println("RT: "+representativeTrajectory);
            computeRepresentativenessMeasure();
        } else {
            System.err.println("Representative Trajectory not founded");
        }

    }

    private void initialize(String dir, String fileRT, String fileDataset, String ext, String sepDir, String[] lstCategoricalPD, String SEPARATOR, String[] valuesNULL, String[] ignoreColumns, String patternDateInput) {
        directory = dir;
        filenameRT = fileRT;
        filenameDataset = fileDataset;
        extension = ext;
        this.sepDir = sepDir;
        this.SEPARATOR = SEPARATOR;
        this.valuesNulls = valuesNULL;
        if (lstCategoricalPD != null) {
            lstCategoricalsPD = Arrays.asList(lstCategoricalPD);
        }
        if (ignoreColumns != null) {
            lstIgnoreCols = Arrays.asList(ignoreColumns);
        }
        if (!patternDateInput.equals("?")) {
            this.formatDate = new SimpleDateFormat(patternDateInput);
        } else {
//            Para mim: Lembrar de incluir essa informação em cada trajetória. Se necessário, ou na criação da RT
//            tsis.representativeTrajectory.setDailyInfo(true);
            this.formatDate = new SimpleDateFormat("HH:mm");
            dailyInfo = true;
        }

        //initialization of aux attributes
        rId = 0;
        auxTid = "-1";

        //initialization of lists
//        attributes = new ArrayList<SemanticAspect>();
        inputTrajectories = new InputDataset();
        representativeTrajectory = new MultipleAspectTrajectory("RT");
        mappingData = new HashMap<Integer, BitSet>();
    }

    private void loadDataset() throws IOException, ParseException {

        java.io.Reader input = new FileReader(directory + sepDir + filenameDataset + extension);
        BufferedReader reader = new BufferedReader(input);

        String datasetRow = reader.readLine();
        //To Get the header of dataset
        String[] datasetColumns = datasetRow.split(SEPARATOR);

        //To add all types of semantic attributes in the dataset, specified in the first line
        int order = 0;
        for (String s : Arrays.copyOfRange(datasetColumns, INDEX_SEMANTIC, datasetColumns.length)) {
            //when attr do not need to be ignored
            if (lstIgnoreCols == null || !lstIgnoreCols.contains(s.toUpperCase().trim())) {
                if (lstCategoricalsPD != null && lstCategoricalsPD.contains(s.toUpperCase())) //If attribute was predefined as categorical
                {
                    inputTrajectories.addAttribute(new SemanticAspect(s.toUpperCase().trim(), order++, SemanticType.CATEGORICAL));
                } else {
                    inputTrajectories.addAttribute(new SemanticAspect(s.toUpperCase().trim(), order++));

                }
            } else {
                order++; //to skip column when it need to be ignored
            }

        }

        datasetRow = reader.readLine();

        //EoF - To get the trajectory data of dataset of each line
        while (datasetRow != null) {
            datasetColumns = datasetRow.toUpperCase().split(SEPARATOR);
            addAttributeValues(datasetColumns);
            datasetRow = reader.readLine();
        }

        reader.close();

    }

    private void addAttributeValues(String[] attrValues) throws ParseException {

        ++rId; //Id given to each data point 

        //Defines the semantic dimension as all attributes in predefined index to the end of line
        String[] semantics = Arrays.copyOfRange(attrValues, INDEX_SEMANTIC, attrValues.length);

        // All trajectory point follow the pattern:
        // id trajectory, coordinates (lat long), time, all semantic dimensions...
        // Follow the pattern add each MAT point in relative MAT
        Date time = !dailyInfo ? formatDate.parse(attrValues[2]) : Util.convertMinutesToDate(Integer.parseInt(attrValues[2]));
        addTrajectoryData(attrValues[0], attrValues[1].split(" "), time, semantics);

    } // end of addAttributeValue method

    /**
     * Add each MAT point in relative MAT object -- mapping input data to the
     * model predefined following O.O.
     *
     * @param tId - Id of MAT
     * @param coordinates - coordinates of point
     * @param time - time date of point
     * @param semantics - semantics attributes of point
     */
    private void addTrajectoryData(String tId, String[] coordinates, Date time, String[] semantics) {
        if (!tId.equals(auxTid)) { // If the MAT is not created
            auxTid = tId;
            inputTrajectories.addTrajectory(new MultipleAspectTrajectory(Integer.parseInt(tId))); // Adds (Create) the new trajectory
            trajectory = inputTrajectories.getListTrajectories().get(inputTrajectories.getListTrajectories().size() - 1);
        }
        // aux values
        ArrayList<AttributeValue> attrs = new ArrayList<>();
        int ord = 0;

        // Organize the point semantic attributes
        for (String val : semantics) {
            SemanticAspect attribute = findAttributeForOrder(ord++);
//            System.out.println("Attribute: " + attribute + " --> val: " + val);
            if (attribute == null) {
                continue; // Skip ignored columns
            }

            if (attribute.getType() == null) {
                try {
                    double numVal = Double.parseDouble(val.toUpperCase());
                    attribute.setType(SemanticType.NUMERICAL);
                } catch (NumberFormatException e) {
                    attribute.setType(SemanticType.CATEGORICAL);
                }

            }

            switch (attribute.getType()) {
                case NUMERICAL:
                try {
                    double numVal = Double.parseDouble(val.toUpperCase());
                    attrs.add(new AttributeValue(numVal, attribute));
                } catch (NumberFormatException e) {
                    // Handle invalid numerical values
                }
                break;

                case CATEGORICAL:
                try {
                    int numVal = Integer.parseInt(val);
                    attrs.add(new AttributeValue("*" + numVal, attribute));
                } catch (NumberFormatException e) {
                    attrs.add(new AttributeValue(val.toUpperCase(), attribute));
                }
                break;

                default:
                    // Handle unsupported attribute types
                    break;
            }
        }

        // Adds the MAT point to the current MAT
        trajectory.addPoint(new Point(rId, Double.parseDouble(coordinates[0]),
                Double.parseDouble(coordinates[1]), time, attrs));
    }

    private void loadRepresentativeMAT() throws ParseException, IOException {
        try {
            // Need update pattern data to format of RT
            formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            java.io.Reader input = new FileReader(directory + sepDir + filenameRT + extension);
            BufferedReader reader = new BufferedReader(input);

            String[] columns;
            String eachRow;
            do {
                eachRow = reader.readLine();
                if (eachRow.contains("input.T")) {
                    eachRow = reader.readLine();
                    System.out.println("Linha RT: "+eachRow);
                    
                    
                    inputTrajectories.setSizePoints(Integer.parseInt(eachRow.split(",")[1].trim()));

                } else if (eachRow.contains("setting infos")) {
                    break;
                }

            } while (eachRow != null);
            // !eachRow.contains("##")); // Logic used on RT file to segment each section of the data (dataset info, RT settings, and RT)

            // READ RT setting info
            //To Get the header of settings
            eachRow = reader.readLine();
            columns = eachRow.split(SEPARATOR);
            int orderCellSize = -1;
            int orderCoverPoints = -1;

            for (int i = 0; i < columns.length; i++) {

                if (columns[i].trim().equalsIgnoreCase("CellSize")) {
                    orderCellSize = i;
                } else if (columns[i].toUpperCase().trim().contains("COVER")) {
                    orderCoverPoints = i;
                    break;
                }
            }

            eachRow = reader.readLine();
            String[] valuesLine = eachRow.split(SEPARATOR);

            // computing point dispersion avg according to Z value and cellSize
            float spatialTau = (float) (Float.parseFloat(valuesLine[orderCellSize++].trim()) / 0.7071) / Float.parseFloat(valuesLine[0].trim());

            representativeTrajectory.setSpatialThreshold(spatialTau);
            representativeTrajectory.setRcThreshold(Float.parseFloat(valuesLine[orderCellSize++].trim()));
            representativeTrajectory.setRvThreshold(Float.parseFloat(valuesLine[orderCellSize++].trim()));

            representativeTrajectory.setCoverPoints(Integer.parseInt(valuesLine[orderCoverPoints].trim()));

            do {
                eachRow = reader.readLine();
                if (eachRow.contains("RT description")) {
                    break;
                }

            } while (eachRow != null);
            // Begin read RT

            //To Get the header of dataset
            eachRow = reader.readLine();
            String[] datasetColumns = eachRow.split(SEPARATOR);
            // To convert each name in SemanticAspect object and store the new order to find in each representative point
            // INDEX_SEMANTIC-1 -- because in original dataset 1st colum is the "tid" 
            // datasetColumns.length-1 > Last one refers to mapping info

            for (int i = INDEX_SEMANTIC - 1; i < datasetColumns.length - 1; i++) {

                SemanticAspect semAspec = findAttribute(datasetColumns[i]);
                if (semAspec != null) {
                    semAspec.setOrder(i);
                } else {
                    System.err.println("Semantic Aspect not found -- " + datasetColumns[i]);
                }
            }

            eachRow = reader.readLine();
            while (eachRow != null) {

                datasetColumns = eachRow.toUpperCase().split(SEPARATOR);
                addAttributeValuesRT(datasetColumns);
                eachRow = reader.readLine();

            }

            reader.close();

        } catch (FileNotFoundException e) {
            CSVWriter valWriter;

            String lastDir = filenameRT.substring(0, filenameRT.lastIndexOf(sepDir));
            String fileLog = filenameRT.substring(filenameRT.indexOf(sepDir),
                    filenameRT.lastIndexOf(sepDir));
            fileLog = fileLog.substring(fileLog.lastIndexOf(" ")) + "[logs][times 4]";
            String pathFileLog = directory + sepDir + lastDir + sepDir + fileLog.trim() + extension;
            if (!new File(pathFileLog).exists()) {
                valWriter = new CSVWriter(pathFileLog);
            } else {
                valWriter = new CSVWriter(pathFileLog, true);
            }

            valWriter.writeLine("Arquivo não encontrado: " + e.getMessage());
            valWriter.flush();
            valWriter.close();

        } catch (IOException e) {
            System.out.println("Erro de leitura do arquivo: " + e.getMessage());
        }

    }

    private void addAttributeValuesRT(String[] attrValues) throws ParseException {

        if (representativeTrajectory == null) {
            representativeTrajectory = new MultipleAspectTrajectory("rt");

        }
        // Spatial dimension
        String[] coordinates = attrValues[0].split(" ");
        Centroid rp = new Centroid(Double.parseDouble(coordinates[0]),
                Double.parseDouble(coordinates[1]));
        rp.setrId(++rId);

        // Temporal dimension
        // implement to MAT-SGT: only one interval foreach point, and all Point contain time information
        if (attrValues[1].contains("-")) {
            String[] interval = attrValues[1].split("-");
            rp.addSTI(formatDate.parse(interval[0].trim()), formatDate.parse(interval[1].trim()), -1);
            tauTime += rp.getListSTI().get(rp.getListSTI().size() - 1).getInterval().minutesInInterval();
        } else {
            rp.addSTI(formatDate.parse(attrValues[1].trim()), -1);
        }

        //  Semantic Dimension
        // aux values
        ArrayList<AttributeValue> attrs = new ArrayList<>();
        SemanticAspect a;

        for (int i = INDEX_SEMANTIC - 1; i < attrValues.length - 1; i++) {

            a = findAttributeForOrder(i);
            if (a != null) { // This one will be NULL whether the columns is setted as ignored
                if (Arrays.asList(valuesNulls).contains(attrValues[i].trim())) {
                    attrs.add(new AttributeValue(a.getType() == SemanticType.NUMERICAL
                            ? ((Double) Double.parseDouble(attrValues[i].trim().toUpperCase()))
                            : attrValues[i].trim().toUpperCase(),
                            a));
                } else {
                    try {
//                        System.out.println("Attr:"+a.getName());
                        attrs.add(new AttributeValue(a.getType() == SemanticType.NUMERICAL
                                ? ((Double) Double.parseDouble(attrValues[i].trim().toUpperCase()))
                                : convertStringToMap(attrValues[i].trim()),
                                a));
                    } catch (NumberFormatException e) {
                        if (attrValues[i].trim().contains("{")) {
                            attrs.add(new AttributeValue(convertStringToMap(attrValues[i].trim()),
                                    a));
                        } else if (attrValues[i] == null) {
                            attrs.add(new AttributeValue(Double.valueOf("0.0"),
                                    a));
                        } else {
                            System.err.println("Error to read value " + attrValues[i].trim() + " of: " + a.getName());
                        }
                    } catch (ArrayIndexOutOfBoundsException e2){
                        System.err.println("Attr: "+a+" = "+attrValues[i].trim());
                        System.err.println("Error: "+e2);
                    }
                }
            }

        }
        a = null;

        rp.setListAttrValues(attrs);
//        

        //  Mapping information
        String[] mappings = attrValues[attrValues.length - 1].replace("{", "").replace("}", "").split(";");
        for (String m : mappings) {
        }
        for (String mapTrajPoint : mappings) {
            String[] trajPoint = mapTrajPoint.split(":");
            int idTraj = Integer.parseInt(trajPoint[0].trim());
            int ridPoint = Integer.parseInt(trajPoint[1].trim());

            //Get points of Trajectory mapped
            BitSet rIds = mappingData.get(idTraj);

            //If the Trajectory mapping doesn't exist
            if (rIds == null) {
                rIds = new BitSet();
                rIds.set(ridPoint);
                mappingData.put(idTraj, rIds);
            } else {
                //update on mapped points of trajectory adding this point as mapped
                rIds.set(ridPoint);
                mappingData.replace(idTraj, rIds);
            }
        }

        //Adds the MAT point to current MAT
        representativeTrajectory.addPoint(rp);

    } // end of addAttributeValue method

    private void computeRepresentativenessMeasure() throws ParseException, CloneNotSupportedException {
        try {
            representativeTrajectory.setTemporalDifAVG(tauTime / representativeTrajectory.getPointList().size());
        } catch (Exception e) {
            System.err.println("Error to compute temporal average as thresholds: " + e);
        }
        RMMAT RM = new RMMAT(directory, filenameRT, inputTrajectories, representativeTrajectory);
        RM.computeRepresentativenessMeasure();

    }

    // Other auxiliary methods...
    /**
     * find the SemanticAspect object by the order
     *
     * @param order
     * @return SemanticAspect
     */
    public SemanticAspect findAttributeForOrder(int order) {
        for (SemanticAspect attr : inputTrajectories.getListAttributes()) {
            if (attr.getOrder() == order) {
                return attr;
            }
        }
        return null;
    } //end findAttributeForOrder method

    /**
     * find the SemanticAspect object by the order
     *
     * @param order
     * @return SemanticAspect
     */
    public SemanticAspect findAttribute(String name) {
        for (SemanticAspect attr : inputTrajectories.getListAttributes()) {
            if (attr.getName().equalsIgnoreCase(name.trim())) {
                return attr;
            }
        }
        return null;
    } //end findAttributeForOrder method

    public static Map<String, Double> convertStringToMap(String input) {
        input = input.replace("{", "").replace("}", "").trim();
        if(input.isEmpty() || input.equalsIgnoreCase(""))
            return null;
        Map<String, Double> map = new HashMap<>();
//        System.out.println("String: " + input);
        
        String[] pairs = input.split(";");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");

            String key = keyValue[0].trim();
            Double value = Double.parseDouble(keyValue[1].trim());
            map.put(key, value);
        }

        return map;
    }

}
