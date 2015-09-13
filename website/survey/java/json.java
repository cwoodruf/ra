import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class json {
  
  public static void main(String[] args) {
    
    for (String a: args) {
      String sectionstr = read(a);
      try {
        JSONObject qtree = new JSONObject(sectionstr);
        JSONObject questions = qtree.getJSONObject("questions");
        int qcount = qtree.getInt("qcount");
        int i = 1;
        for (; i < qcount; i++) {
          System.out.println(questions.getJSONObject(new Integer(i).toString()));
          System.out.println();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static String read(String sectionid) {
    StringBuilder builder = new StringBuilder();
    HttpClient client = new DefaultHttpClient();
    try {
      String pw = DigestUtils.shaHex("hi");
System.out.println(pw);
      HttpGet httpGet = new HttpGet("http://localhost:8077/ra/data/section/"+sectionid+"?userid=cal&nonce=0&sig=test");
      HttpResponse response = client.execute(httpGet);
      StatusLine statusLine = response.getStatusLine();
      int statusCode = statusLine.getStatusCode();
      if (statusCode == 200) {
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        String line;
        while ((line = reader.readLine()) != null) {
          builder.append(line);
        }
      } else {
        System.err.println("Failed to download section");
      }
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return builder.toString();
  }
} 

