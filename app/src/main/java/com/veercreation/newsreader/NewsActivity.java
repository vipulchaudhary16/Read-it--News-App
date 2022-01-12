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
    static ArrayList<String> titles = new ArrayList<>();
    static ArrayList<String> contents = new ArrayList<>();

    ArrayAdapter adapter;
    ListView newsListView;

    private SQLiteDatabase articleDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        articleDB = this.openOrCreateDatabase("Articles" , MODE_PRIVATE , null);
        articleDB.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY , articleID INTEGER , title VARCHAR , content VARCHAR)");


        DownloadTask task = new DownloadTask();
        try {
//            task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
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
            Cursor c = articleDB.rawQuery("SELECT * FROM articles", null);
            int titleInd = c.getColumnIndex("title");
            int contentInd = c.getColumnIndex("content");


            if(c.moveToFirst()){
                titles.clear();
                contents.clear();

                do{
           Log.i("database" , Integer.toString(titleInd));
           Log.i("database" , Integer.toString(contentInd));
                    titles.add(c.getString(titleInd));
                    contents.add(c.getString(contentInd));

                } while(c.moveToNext());

                adapter.notifyDataSetChanged();

            }
        }


    public  class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                String result = LoadData.loadDataFromUrl(new URL(urls[0]));

                JSONArray jsonArray = new JSONArray(result);
                int numberOfResult = Consts.resultLimit;

                if (jsonArray.length() < 20) {
                    numberOfResult = jsonArray.length();
                }

                articleDB.execSQL("DELETE FROM articles");

                for (int i = 0; i < numberOfResult; i++) {
                    String articleID = jsonArray.getString(i);
                    URL url = new URL("https://hacker-news.firebaseio.com/v0/item/" + articleID + ".json?print=pretty");
                    String articleInfo = LoadData.loadDataFromUrl(url);
                    JSONObject jsonObject = new JSONObject(articleInfo);

                    if (!jsonObject.isNull("title") && !jsonObject.isNull("url")) {
                        String articleTitle = jsonObject.getString("title");
                        String articleUrl = jsonObject.getString("url");
                        String articleContent = LoadData.loadDataFromUrl(new URL(articleUrl));


                        Log.i("article content " , articleTitle);

                        titles.add(articleTitle);

                        String sql =  "INSERT INTO articles (articleID , title , content) VALUES (? , ?, ?)";

                        SQLiteStatement statement = articleDB.compileStatement(sql);
                        statement.bindString(1 , articleID );
                        statement.bindString(2 , articleTitle );
                        statement.bindString(3 , articleContent );
                        statement.execute();

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Consts.error;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            updateListView();
        }
    }
}