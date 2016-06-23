package ua.rodionov.salonpersik;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ExpandedActivity extends AppCompatActivity {

    private int count;
    private ArrayList<News> news = new ArrayList<>();
    private ArrayAdapter<News> adapter;
    private GridView layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded);
        layout = (GridView)findViewById(R.id.grid_view);
        if(getIntent().getStringExtra("type").equals("news")){
            setTitle("Все новости и акции");
            count = getIntent().getIntExtra("count", 0);
            new GetNewsTask().execute();
        }
    }

    private void inflateNewsAdapter() {
        layout.setAdapter(new ExtendedViewAdapter(this, news));
    }

    private class GetNewsTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("http://rodionov-api.zzz.com.ua/get_news.php");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                urlConnection.disconnect();

                resultJson = buffer.toString();

                JSONObject jsonObject = new JSONObject(resultJson);
                int error = jsonObject.getInt("error");
                if(error == 0){
                    JSONArray array = jsonObject.getJSONArray("news");
                    for(int i = 0; i < array.length(); i++){
                        news.add(new News(array.getJSONObject(i).getString("title"),
                                array.getJSONObject(i).getString("text"),
                                BitmapFactory.decodeStream(new URL(array.getJSONObject(i).getString("image")).openConnection().getInputStream()),
                                array.getJSONObject(i).getInt("price")));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            inflateNewsAdapter();
        }

    }
}
