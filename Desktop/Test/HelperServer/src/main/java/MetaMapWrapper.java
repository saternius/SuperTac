import gov.nih.nlm.nls.metamap.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by davidsilin on 5/17/16.
 */
public class MetaMapWrapper {
    public MetaMapApi api;

    String ip = "localhost";  //GCloud Server
    String options = "-y";
    String ignoreSems = "";


    public MetaMapWrapper(String ip){
        ignoreSems = MedicalWeights.getIgnores();
        System.out.println(ignoreSems);
        this.ip = ip;
        newMetaMapAPI();
    }

    private void newMetaMapAPI(){
        api = new MetaMapApiImpl(ip);
        api.setOptions(options+ignoreSems);
    }

    public void makePhraseFocused(){
        options = "-y -z -i";
        api.setOptions(options+ignoreSems);
    }
    public void makeSentenceFocused(){
        options = "-y";
        api.setOptions(options+ignoreSems);
    }

    /**
     * Sends string to String to metamap server to be parsed.
     * @param in
     * @return
     */
    public List<Result> parseString(String in){

        String patternStr = "\\w";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(in);

        if(!matcher.find() || in.equals(".")){
            //This will result in a prolog error
            return null;
        }

        List<Result> ret = null;
        try {
            if (api == null) {
                System.out.println("Metamap not initialized");
                newMetaMapAPI();
            }
           // System.out.println(in);
            ret = api.processCitationsFromString(in);
        }catch(Exception e){
            System.out.println("[ "+ip+" ] Crashed on: "+in);
            try {
                System.out.println("SLEEPING...");
                Thread.sleep(5000);
                api.disconnect();
                newMetaMapAPI();
                System.out.println("AWAKE.");
            } catch (InterruptedException t) {
                //t.printStackTrace();
            }
        }

        //check if crashed
        if(ret==null || ret.size()==0){
            System.out.println("Possible MM Failure on: \""+in+"\"");
        }

        return ret;
    }


    public void disconnect(){
        System.out.println("disconnecting..");
        api.disconnect();
    }

}
