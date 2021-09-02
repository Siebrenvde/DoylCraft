package dev.siebrenvde.doylcraft.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Requests {

    public HashMap<String, Object> get(String url) {

        try {

            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);

            CloseableHttpResponse response = client.execute(httpGet);

            if(response.getStatusLine().getStatusCode() != 200) {
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String inputLine;
            StringBuffer buffer = new StringBuffer();

            while ((inputLine = reader.readLine()) != null) {
                buffer.append(inputLine);
            }

            reader.close();
            client.close();

            return new Gson().fromJson(buffer.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());

        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}
