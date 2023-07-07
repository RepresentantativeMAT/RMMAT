/*

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
import br.ufsc.model.SemanticType;
import br.ufsc.util.Util;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vanes
 */
public class MUITAS_v2 extends MUITAS{


    public double computeMatch(AttributeValue rep, AttributeValue atv) {
        double match = 0;

        if (atv == null || rep == null) {
            return 0;
        }

        if (rep.getAttibute().getType() == SemanticType.NUMERICAL) {
            match = Math.abs((Double) rep.getValue() - (Double) atv.getValue()) <= getThreshold(atv.getAttibute()) ? 1.0 : 0;

        } else if (rep.getAttibute().getType() == SemanticType.CATEGORICAL) {
            // case of semantic - categorical
            try {
                Map<String, Double> valuesRT = (HashMap) rep.getValue();
                if (valuesRT.containsKey(((String) atv.getValue()).toUpperCase())) {
                    match = valuesRT.get(((String) atv.getValue()).toUpperCase());
//                    System.out.println("Attr: " + ((String) atv.getValue()).toUpperCase() + " - value: " + match);
                }
            } catch (Exception e) {
                match = rep.getValue().equals(atv.getValue()) ? 1.0 : 0.0;
            }

        } else {
            System.err.println("Attribute Type not identified> " + rep.getAttibute().getName() + " = " + rep.getValue());
        }

        return match * getWeight(rep.getAttibute());
    }

}
