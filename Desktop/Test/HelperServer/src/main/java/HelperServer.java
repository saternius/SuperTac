
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by jason on 10/28/16.
 */
@org.eclipse.jetty.websocket.api.annotations.WebSocket
public class HelperServer {
    static public LexicalizedParser parser;
    HashMap<Session, ClientData> clientHash = new HashMap<>();
    @OnWebSocketConnect
    public void onConnect(Session s) throws Exception {
        System.out.println("helping a user..");
        clientHash.put(s,new ClientData());
    }

    @OnWebSocketClose
    public void onClose(Session s, int statusCode, String reason) {
        System.out.println("leaving a user.");
        clientHash.remove(s);
    }

    @OnWebSocketMessage
    public void onMessage(Session s, String message) {
        System.out.println(message);
        try {
            JSONObject params = new JSONObject(message);
            String task = params.getString("task");
            if (task.equals("preParse")) {
                String sentence = params.getString("str");
                int pos = params.getInt("pos");
                String typeGen = "{\"success\":\"false\",\"reason\":\"main exception\",\"pos\":"+pos+"}";
                try {
                    ClientData client = clientHash.get(s);
                    typeGen = typeGenParse(client.mw, mmTransform(sentence),client.metamap);
                } catch (Exception e) {
                    //e.printStackTrace();
                    System.out.println(e.toString());
                }
                String parseTree = parseTree(sentence);
                s.getRemote().sendString("{\"parseTree\":" + JSONObject.quote(parseTree) + ",\"typeGen\":" + typeGen + ",\"pos\":"+pos+"}");
            } else if (task.equals("bans")) {
                JSONObject qData = params.getJSONObject("qData");
                s.getRemote().sendString(MedQuestionTransducer.selectBans(qData, clientHash.get(s).neo_q));
            }else if(task.equals("pureParse")){
                String sentence = params.getString("str");
                String parseTree = parseTree(sentence);
                int pos = params.getInt("pos");
                System.out.println("["+pos+"]: "+sentence);
                s.getRemote().sendString("{\"parseTree\":" + JSONObject.quote(parseTree) + ",\"pos\":"+pos+"}");
            }
        }catch(Exception e){
            //e.printStackTrace();
            System.out.println(e.toString());
            try {
                s.getRemote().sendString("{\"success\":\"false\"}");
            } catch (IOException e1) {
                //e1.printStackTrace();
                System.out.println(e.toString());
            }
        }
    }

//    //transform the string so that it wont crash mm;
    public String mmTransform(String str){
        return cleanUpSentenceString(str);
    }

    public String parseTree(String str){
        if(parser == null){
            loadParser();
        }
        try {
            return parser.parse(str).toString();
        }catch (Exception e){
            return "<Error>";
        }
//        return AnalysisUtilities.getInstance().parseSentence(str).parse.toString();
    }

    public static void loadParser(){
        parser = LexicalizedParser.loadModel("lib/englishFactored.ser.gz");
    }

    public String typeGenParse(MedicalWeights mw, String str, MetaMapWrapper metamap) throws IOException {
        //s.getRemote().sendString("FUCK OFF");
        return TypeStoreGen.getUtterances(str,metamap,mw);
    }

    public static String cleanUpSentenceString(String s) {
        String res = s;
        //if(res.length() > 1){
        //	res = res.substring(0,1).toUpperCase() + res.substring(1);
        //}

        res = res.replaceAll("\\s([\\.,!\\?\\-;:])", "$1");
        res = res.replaceAll("(\\$)\\s", "$1");
        res = res.replaceAll("can not", "cannot");
        res = res.replaceAll("\\s*-LRB-\\s*", " (");
        res = res.replaceAll("\\s*-RRB-\\s*", ") ");
        res = res.replaceAll("\\s*([\\.,?!])\\s*", "$1 ");
        res = res.replaceAll("\\s+''", "''");
        //res = res.replaceAll("\"", "");
        res = res.replaceAll("``\\s+", "``");
        res = res.replaceAll("\\-[LR]CB\\-", ""); //brackets, e.g., [sic]
        res = res.replaceAll("\\. \\?", ".?");
        res = res.replaceAll(" 's(\\W)", "'s$1");
        res = res.replaceAll("(\\d,) (\\d)", "$1$2"); //e.g., "5, 000, 000" -> "5,000,000"
        res = res.replaceAll("``''", "");

        //remove extra spaces
        res = res.replaceAll("\\s\\s+", " ");
        res = res.trim();

        return res;
    }

}
