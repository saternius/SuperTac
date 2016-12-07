import java.io.BufferedReader;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jason on 7/1/16.
 * Used to get the complexity of phrases/sentences/articles
 * Uses googles NGram libraries
 */
public class MedGramRanker implements Serializable{
    private static final long serialVersionUID = 6929685098267757690L;
    public static HashMap<String,FreqStruct> nGram = new HashMap<String,FreqStruct>();
    public static String directory = "dict/nGrams/MedGram.nGram";
    static int alerter = 0;

    /**
     * Call this function to generate a new NGram Model.
     * @param save true if you wish to save the NGram Model
     * @throws Exception
     */
    public static void reconstructNGram(Boolean save) throws Exception{
        List<String> results = new ArrayList<String>();
        File[] files = new File("dict/nGrams").listFiles();
        for (File file : files) {
            if (file.isFile()) {
                String name = file.getName();
                if(name.indexOf(".nGram")==-1) {
                    appendLetter(name);
                    System.out.println("Processed " + name);
                }
            }
        }

        if(save) {
            System.out.println("Saving nGram..");
            saveNGram();
        }
    }

    public static void saveNGram() throws Exception {
        FileWriter.saveObject(nGram,directory);
    }

    public static void loadNGram() throws Exception{
        nGram = (HashMap<String, FreqStruct>) FileReader.readObject(directory);
    }

    /**
     * Used in the NGram construction process
     * @param name The letter to be added to the NGram Model
     */
    public static void appendLetter(String name){
        try {
            BufferedReader br = new BufferedReader(new java.io.FileReader("dict/nGrams/"+name));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            line = br.readLine();
            while (line != null) {
                String[] res = line.split(",");
                int freq = 0;
                int occ = 0;
                try {
                    freq = Integer.parseInt(res[1]);
                    occ = Integer.parseInt(res[2]);
                }catch(Exception e){
                    freq = -1;
                    occ = -1;
                }
                if(res.length==3 && freq>0 && occ>0) {
                    String word = res[0];
                    nGram.put(word, new FreqStruct(freq, occ));
                    alerter++;
                    if(alerter%500000==0){
                        System.out.println(alerter+" words processed.");
                    }
                }
                line = br.readLine();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Calculates the complexity of a phrase.
     * Details: Gives complexity of the most complex word in the phrase
     * @param str
     * @return
     */
    public static int calcPhraseComplexity(String str){
        String[] words = str.split(" ");
        int minOcc = 999999999;
        for(int i=0; i<words.length;i++){
            words[i] = words[i].replace(",","").replace(".","").replace("?","").replace("!","").replace("(","").replace(")","");
            FreqStruct fs = nGram.get(words[i]);
            if(fs!=null){
                if(fs.occ<minOcc){
                    minOcc = fs.occ;
                }
            }else{
                //System.out.println(words[i]+":null:null");
            }
        }
        if(minOcc == 999999999){
            minOcc = 0;
        }
        return minOcc;
    }

    /**
     * Gives the complexity of an Article
     * Returns the average complexity of all the sentence complexities.
     * @param str
     * @return
     */
    public static int calcArticleComplexity(String str){
        String[] sentences = str.split("\\. ");
        int totC = 0;
        ArrayList<Integer> comps = new ArrayList<>();
        for(int i=0; i<sentences.length;i++){
            totC+=calcSentenceComplexity(sentences[i]);
        }
        return totC/sentences.length;
    }

    /**
     * Gives the complexity of a sentence
     * Returns the average of the 3 most complex words in the sentence.
     * @param str
     * @return
     */
    public static int calcSentenceComplexity(String str){
        String[] words = str.split(" ");
        ArrayList<Integer> weights = new ArrayList<Integer>();


        for(int i=0; i<words.length;i++){
            FreqStruct nG = nGram.get(words[i]);
            if(nG!=null) {
                weights.add(nG.occ);
            }
        }

        if(weights.size() == 0 ){
            return 1999999;
        }
        if(weights.size()<3){
            int tots = 0;
            for(int i=0; i<weights.size();i++){
                tots+=weights.get(i);
            }
            return tots/weights.size();
        }else{
            Collections.sort(weights);
            return (weights.get(0)+weights.get(1)+weights.get(2))/3;
        }
    }

    public static int wordComplexity(String word){
       try {
           if (nGram.containsKey(word)) {
               return nGram.get(word).occ;
           }
       }catch(Exception e){
           //System.out.println("failed to fetch : "+word);
       }

        return 0;
    }
}