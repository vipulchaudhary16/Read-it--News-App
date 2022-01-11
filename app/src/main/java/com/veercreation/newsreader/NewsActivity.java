package com.veercreation.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class NewsActivity extends AppCompatActivity {

    public int numberOfItem;

    ArrayList<String> titles = new ArrayList<>();
    ArrayAdapter adapter;

    ListView newsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        DownloadTask task = new DownloadTask();

        try {
            task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
        } catch (Exception e) {
            e.printStackTrace();
        }

        newsListView = findViewById(R.id.newsListView);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, titles);
        newsListView.setAdapter(adapter);
    }

    public static class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = " ";
            URL url;
            HttpsURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                StringBuilder resultBuilder = new StringBuilder(" ");

                while (data != -1) {
                    char current = (char) data;
                    resultBuilder.append(current);
                    data = inputStreamReader.read();
                }
                result = resultBuilder.toString();

                JSONArray jsonArray = new JSONArray(result);
                int numberOfResult = 20;

                if (jsonArray.length() < 20) {
                    numberOfResult = jsonArray.length();
                }

                for (int i = 0; i < numberOfResult; i++) {
                    String articleID = jsonArray.getString(i);
                    url = new URL("https://hacker-news.firebaseio.com/v0/item/" + articleID + ".json?print=pretty");

                    urlConnection = (HttpsURLConnection) url.openConnection();
                    inputStream = urlConnection.getInputStream();
                    inputStreamReader = new InputStreamReader(inputStream);


                    String articleInfo = " ";
                    data = inputStreamReader.read();

                    resultBuilder = new StringBuilder(" ");

                    while (data != -1) {
                        char current = (char) data;
                        resultBuilder.append(current);
                        data = inputStreamReader.read();
                    }
                    articleInfo = resultBuilder.toString();

                    JSONObject jsonObject = new JSONObject(articleInfo);

                    if (!jsonObject.isNull("title") && !jsonObject.isNull("url")) {
                        String articleTitle = jsonObject.getString("title");
                        String articleUrl = jsonObject.getString("url");

                        url = new URL(articleUrl);
                        urlConnection = (HttpsURLConnection) url.openConnection();
                        inputStream = urlConnection.getInputStream();
                        inputStreamReader = new InputStreamReader(inputStream);
                        data = inputStreamReader.read();
                        StringBuilder articleContentBuilder = new StringBuilder();

                        while (data!=-1){
                            char current = (char) data;
                            articleContentBuilder.append(current);
                            data = inputStreamReader.read();
                        }

                        String articleContent = articleContentBuilder.toString();

                        Log.i("article content " , articleContent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}