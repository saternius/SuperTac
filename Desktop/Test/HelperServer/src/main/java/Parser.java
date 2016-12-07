import org.apache.commons.codec.net.URLCodec;
import spark.Spark;
import static spark.Spark.init;
import static spark.Spark.port;
import static spark.Spark.webSocket;

/**
 * Created by jason on 11/25/16.
 */
public class Parser {
    public static String host = "localhost";
    public static void main(String[] args) throws Exception {
        HelperServer.loadParser();
        SemTUI.genTUIMap();
        port(9090);
        webSocket("/helper",HelperServer.class);
        init();
        URLCodec uc = new URLCodec();
        Spark.get("/parseS",(request, response)->{
            try{
                String q = request.queryString();
                String input = uc.decode(uc.decode(q.substring(2, q.length()), "UTF-8"), "UTF-8");
                return HelperServer.parser.parse(input);
            }catch(Exception e){
                return "";
            }

        });

    }
}
