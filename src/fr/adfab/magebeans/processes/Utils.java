package fr.adfab.magebeans.processes;

/**
 *
 * @author vanthiepnguyen
 */
public class Utils {
    public static String capitalize(String inputString) {
        
        String cap = inputString.toLowerCase().substring(0, 1).toUpperCase();
        cap += inputString.toLowerCase().substring(1);
        return cap;
    }
}
