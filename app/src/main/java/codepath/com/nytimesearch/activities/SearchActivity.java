package codepath.com.nytimesearch.activities;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import com.loopj.android.http.*;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import codepath.com.nytimesearch.Article;
import codepath.com.nytimesearch.ArticleArrayAdapter;
import codepath.com.nytimesearch.R;
import cz.msebera.android.httpclient.Header;



public class SearchActivity extends AppCompatActivity {
     EditText etQuery;
     GridView gvResults;
     Button btnSearch;

     ArrayList<Article> articles;
     ArticleArrayAdapter adapter;


    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            if (exitValue != 0){
                Toast.makeText(this, "Pas de connection", Toast.LENGTH_LONG).show();
            }
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        isOnline();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();

    }
     public  void  setupViews(){

        gvResults = (GridView) findViewById(R.id.gvResults);

        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);

                Article article = articles.get(position);
                i.putExtra("url", article.getWebUrl());
                startActivity(i);

            }
        });

     }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                AsyncHttpClient client = new AsyncHttpClient();
                //String  queri = etQuery.getText().toString();

                Toast.makeText(SearchActivity.this, "Recherche en  cours ", Toast.LENGTH_LONG).show();
                String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
                RequestParams params = new RequestParams();
                params.put("api-key", "16b07ba4166644d59b5ebc833866c166");
                params.put("page", 0);
                params.put("q", query);
                client.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d( "onSuccess: ", response.toString());
                        JSONArray articleJsonResults = null;
                        try {
                            articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                            adapter.addAll(Article.fromJSONArray(articleJsonResults));
                            Log.d("DEBUG", articles.toString());
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
                return false;



                
            }




            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void sharelink(MenuItem item) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "http://codepath.com");
        startActivity(Intent.createChooser(shareIntent, "Share link using"));
        Toast.makeText(SearchActivity.this, "Hello", Toast.LENGTH_SHORT).show();
    }
}
