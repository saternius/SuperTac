
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.apache.http.HttpHeaders.USER_AGENT;


/**
 * Created by davidsilin on 4/15/16.
 */
public class GetRequest {

    public static String get(String url) throws Exception{
        HttpClient client = new DefaultHttpClient();
        url = url.replaceAll("%", "%25");
        url = url.replaceAll(";", "%3b");
        url = url.replaceAll("\"", "%22");
        url = url.replaceAll(" ", "%20");
        url = url.replaceAll("#", "%23");
        url = url.replaceAll("\'", "%27");
        HttpGet request = new HttpGet(url);
        // add request header
        request.addHeader("User-Agent", USER_AGENT);

        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
