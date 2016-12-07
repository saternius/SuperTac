import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jason on 5/22/16.
 * This class serves to convert semType Strings to TUI's
 * Ex: dsyn -> T047
 */
public class SemTUI {



    private static HashMap semTUI = new HashMap<String,String>();
    private static HashMap semDef = new HashMap<String,String>();

    //Generates the TUIMap
    public static void genTUIMap(){

        String textMap = FileReader.readFile("semTypes.txt");
        int l_index = 0;
        int barI2;
        String abb;
        while(true){
            int barI = textMap.indexOf("|",l_index);
            abb = textMap.substring(l_index,barI);
            barI2 = textMap.indexOf("|",barI+1);
            String tui = textMap.substring(barI+1,barI2);
            int commaI = textMap.indexOf("*",barI2+1);
            semTUI.put(abb,tui);
            if(commaI==-1){
                break;
            }
            String def = textMap.substring(barI2+1,commaI);
            semDef.put(abb,def);
            l_index=commaI+1;
        }
        String def = textMap.substring(barI2+1,textMap.length()-1);
        semDef.put(abb,def);
    }

    /**
     * Returns the TUI of an semType
     * @param abb
     * @return
     */
    public static String getTUI(String abb){
        return (String)semTUI.get(abb);
    }

    
    /**
     * Returns the definition of the semtype
     * Ex: dsyn -> Disease or Syndrome*
     * @param abb
     * @return
     */
    public static String getDef(String abb){
        return (String)semDef.get(abb);
    }

    public static ArrayList<String> getChemicalSems(){
        ArrayList<String> isChem = new ArrayList<>();
        isChem.add("gngm");
        isChem.add("aapp");
        isChem.add("enzy");
        return isChem;
    }
}
