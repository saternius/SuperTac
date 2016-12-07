
import gov.nih.nlm.nls.metamap.*;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by davidsilin on 5/13/16.
 * The TypeStore creates data structures for all the semtype metamap recognizes in the document
 */
public class TypeStoreGen {
    public HashMap<String,Integer> semFreq = new HashMap<>();   //tallies the occurances of semTypes in the document
    public int totalSems = 0;


    /**
     * Parses a sentence for all semtypes in it
     * @param text
     * @param mmw
     */
    public static String getUtterances(String text, MetaMapWrapper mmw, MedicalWeights mw){
        //sm.incPreProc();
        String ret = "{\"success\":\"true\",\"commands\":[";

        boolean hasResults = false;
        List<Result>  mmReturn = mmw.parseString(text);

        if(mmReturn==null || mmReturn.size()==0){
            return "{\"success\":\"false\",\"reason\":\"no mm results\"}";
        }
        Result res = mmReturn.get(0);
        ArrayList<String> preCleanPhrases = new ArrayList<>();
        try {
            List<Utterance>utter = res.getUtteranceList();
            for (Utterance utterance : utter) {
                String phrases = "";
                String matched = "";

                for (PCM pcm: utterance.getPCMList()) {
                    String phrase = pcm.getPhrase().getPhraseText();
                    phrases +=  phrase+ "##";
                    preCleanPhrases.add(phrase);
                    List<String> matched_a = new ArrayList<>();
                    for (Mapping map: pcm.getMappingList()) {
                        for (Ev ev: map.getEvList()) {
                            for (String type : ev.getSemanticTypes()){
                                //ensure we want to match the word
                                if(mw.getSemValue().containsKey(type) && (mw.getSemValue().get(type) >= .5f || mw.getSemValue().get(type) == -1)) {
                                    matched_a.addAll(ev.getMatchedWords());
                                }
                            }
                        }
                    }
                    matched+= matched_a.toString() + "##";
                }

                String[] cleanPhrases = fetchCleanPhrases(utterance,phrases,matched,preCleanPhrases);



                int i = 0;
                for (PCM pcm: utterance.getPCMList()) {
                    //should consider cleaning the phrase here
                    if (i > cleanPhrases.length - 1 || cleanPhrases[i].length() == 0) {
                        i++;
                        continue;
                    }
                    ArrayList<String> addedSemtypes = new ArrayList<>();
                    for (Mapping map : pcm.getMappingList()) {
                        for (Ev ev : map.getEvList()) {
                            for (String type : ev.getSemanticTypes()) {
                                if (!addedSemtypes.contains(type) && mw.getSemValue().containsKey(type) && mw.getSemValue().get(type) >= .5f) {
                                    String phrase = cleanPhrases[i].contains("<!DOC")?preCleanPhrases.get(i):cleanPhrases[i];
                                    ret += dataToString(type, phrase, ev.getConceptId(), ev.getMatchedWords(), ev.getSemanticTypes());
                                    hasResults = true;
                                    addedSemtypes.add(type);
                                }
                            }
                        }
                    }
                    i++;
                }
            }
        } catch (Exception e){
            System.out.println("TypeGen Error: " + e.getMessage());
        }

        if(!hasResults) {
            return "{\"success\":\"false\",\"reason\":\"dunno\"}";
        }
        ret = ret.substring(0, ret.length() - 1);
        return ret+"]}";
    }

    private static String[] fetchCleanPhrases(Utterance utterance, String phrases, String matched, ArrayList<String> preCleanPhrases) {
        try {
            String url = "http://127.0.0.1:5000/clean?";
            url += "utterance=" + utterance.getString();
            url += "&phrases=" + phrases.substring(0, phrases.length() - 2);
            url += "&matched=" + matched.substring(0, matched.length() - 2);
            System.out.println(url);
            phrases = GetRequest.get(url);
            String[] results = (phrases + "##end").split("##", -1);

            System.out.println("Utterance: "+utterance.toString());
            System.out.println("Phrases: "+phrases);
            System.out.println("Matched: "+matched);
            String resString = "";
            for(int i=0;i<results.length;i++){
                resString+=results[i]+", ";
            }
            System.out.println("Results: "+resString);
            return results;
        }catch (Exception e){
            System.out.println(e.toString());
            return (String[])preCleanPhrases.toArray();
        }

    }

    public static String dataToString(String type, String cleanPhrase, String conceptID, List<String> matchedWords, List<String> semanticTypes){
        String phrase = "";
        for(int i=0;i<matchedWords.size();i++){
            phrase+=matchedWords.get(i)+" ";
        }

        String semString = "";
        for(int i=0;i<semanticTypes.size();i++){
            semString+=semanticTypes.get(i)+",";
        }

        phrase = phrase.substring(0,phrase.length()-1);
        semString = semString.substring(0,semString.length()-1);

        return "{\"type\":\""+type+"\",\"cleanPhrase\":"+ JSONObject.quote(cleanPhrase)+",\"conceptID\":\""+conceptID+"\",\"matchWords\":"+ JSONObject.quote(phrase)+",\"semTypes\":\""+semString+"\"},";
    }
}
