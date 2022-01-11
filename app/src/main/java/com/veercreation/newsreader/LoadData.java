package com.veercreation.newsreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class LoadData {
    public static String loadDataFromUrl(URL url){
        try {
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            int data = inputStreamReader.read();
            StringBuilder finalDataBuilder = new StringBuilder(" ");
            while(data!=-1){
                char current = (char) data;
                finalDataBuilder.append(current);
                data = inputStreamReader.read();
            }
            return finalDataBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return Consts.error;
        }

    }
}
