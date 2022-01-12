package com.veercreation.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.media.MediaParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class NewsActivity extends AppCompatActivity {
    static ArrayList<String> titles = new ArrayList<>();
    static ArrayList<String> contents = new ArrayList<>();

    ArrayAdapter adapter;
    ListView newsListView;

    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);


        progressBar = findViewById(R.id.progressBar);



        DownloadTask task = new DownloadTask();
        try {
            task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
        } catch (Exception e) {
            e.printStackTrace();
        }

        newsListView = findViewById(R.id.newsListView);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, titles);
        newsListView.setAdapter(adapter);

        updateListView();

        newsListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getApplicationContext() , ArticleActivity.class);
            intent.putExtra("content" , contents.get(i));
            startActivity(intent);
        });
    }

    public void updateListView(){
        adapter.notifyDataSetChanged();
        for(String urls : contents){
            Log.i("listItems" , urls);

        }
        }


    public  class DownloadTask extends AsyncTask<String, Void, String> {



        @Override
        protected String doInBackground(String... urls) {
            try {

                String result = LoadData.loadDataFromUrl(new URL(urls[0]));

                JSONArray jsonArray = new JSONArray(result);
                int numberOfResult = Consts.resultLimit;

                if (jsonArray.length() < numberOfResult) {
                    numberOfResult = jsonArray.length();
                }


                for (int i = 0; i < numberOfResult; i++) {
                    String articleID = jsonArray.getString(i);
                    URL url = new URL("https://hacker-news.firebaseio.com/v0/item/" + articleID + ".json?print=pretty");
                    String articleInfo = LoadData.loadDataFromUrl(url);
                    JSONObject jsonObject = new JSONObject(articleInfo);

                    if (!jsonObject.isNull("title") && !jsonObject.isNull("url")) {
                        String articleTitle = jsonObject.getString("title");
                        String articleUrl = jsonObject.getString("url");
                        Log.i("articleData" , articleTitle);
                        titles.add(articleTitle);
                        contents.add(articleUrl);
                    }
                }

                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Consts.error;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.INVISIBLE);
            updateListView();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
    }
}