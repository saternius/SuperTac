import org.json.JSONObject;
import java.io.Serializable;

/**
 * Created by jason on 6/6/16.
 */
public class BadAns implements Serializable{
    private static final long serialVersionUID = 6529685098269757690L;
    public String subject;              //The word that is being replaces
    public String text;                 //The entire bad answer
    public String CUI;
    public String origin;               //Information on how this bans was generated
    public String path = "NULL";        //String representation of the graph nodes
    public double bonusRank = 0;        //Bonus rank to give to question
    public int phraseComplexity = 0;
    public String[] parentDetails;      //Keeps track of details of its parents (Used in parGen)
    public boolean preferGraphName = false;
    public String graphName = "null";
    public String ansName = "null";
    public String neoQuery = "null";
    public String chunkID = "null";

    public BadAns(String subject, String CUI, String origin, String path, double bRank, String neoQuery){
        int phraseComplexity = (!origin.equals("numGen"))? MedGramRanker.calcPhraseComplexity(subject):0;
        init(subject,CUI,origin,path,bRank,neoQuery,phraseComplexity);
    }

    public BadAns(String subject, String CUI, String origin, String path, double bRank, String neoQuery, int phraseC){
        init(subject,CUI,origin,path,bRank,neoQuery,phraseC);
    }

    public void init(String subject, String CUI, String origin, String path, double bRank, String neoQuery, int phraseC){
        this.subject = subject;
        this.CUI = CUI;
        this.origin = origin;
        this.path = path;
        this.bonusRank = bRank;
        this.phraseComplexity = phraseC;
        this.neoQuery = neoQuery;
    }

    public BadAns(){
        //nothing.
    }

    /**
     * @return the Bans to JSON format.
     * @throws Exception
     */
    public String toJSON() throws Exception {
        if(path!= null){
            path = path.replaceAll("\"","");
        }

        if(!graphName.equals("null")){
            preferGraphName = checkPreferGraphName();
        }
        neoQuery = neoQuery.replaceAll("\"","'");
        String pgName = preferGraphName?"true":"false";
        String ret = "{\"chunkID\":\""+chunkID+"\",\"text\":"+ JSONObject.quote(text)+", \"cuiNames\":[], \"subject\":"+ JSONObject.quote(subject)+",\"graphName\": \""+graphName+"\",\"phraseC\":"+phraseComplexity+", \"preferGraphName\": "+pgName+",\"CUI\":\""+CUI+"\", \"origin\": \""+origin+"\", \"path\": \""+path+"\", \"neoQuery\":\""+neoQuery+"\"}";
        return ret;
    }

    public String toLightJSON(){
        if(path!= null){
            path = path.replaceAll("\"","");
        }

        if(!graphName.equals("null")){
            preferGraphName = checkPreferGraphName();
        }
        neoQuery = neoQuery.replaceAll("\"","'");
        String pgName = preferGraphName?"true":"false";
        String ret = "{\"subject\":"+ JSONObject.quote(subject)+",\"graphName\": \""+graphName+"\",\"phraseC\":"+phraseComplexity+", \"preferGraphName\": "+pgName+",\"CUI\":\""+CUI+"\", \"origin\": \""+origin+"\", \"path\": \""+path+"\", \"neoQuery\":\""+neoQuery+"\",\"bRank\":"+bonusRank+"}";
        return ret;
    }

    /**
     * Checks if its better to use docRep name of Graph Name
     * @return
     */
    private boolean checkPreferGraphName() {
        return MedicalWeights.checkPreferGraphName(ansName,subject,graphName);
    }
}
