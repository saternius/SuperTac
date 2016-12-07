import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by davidsilin on 5/14/16.
 */
public class TypeStoreData implements Serializable{
    public String text;
    public String CUID;
    private static final long serialVersionUID = 6529685098267757691L;

    public TypeStoreData(String t, String C){
        text = t;
        CUID = C;
    }

    public String toJSONObj(){
        return "{\"text\":"+ JSONObject.quote(text)+",\"CUID\":\""+CUID+"\"}";
    }
}