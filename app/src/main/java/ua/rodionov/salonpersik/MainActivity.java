package ua.rodionov.salonpersik;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String CLIENT_ID;
    private boolean new_user;
    private ArrayList<News> news = new ArrayList<>();
    private int coins;
    private int newsCount;
    private SharedPreferences sPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CLIENT_ID = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        sPref = getPreferences(MODE_PRIVATE);
        editor = sPref.edit();

        if(sPref.getBoolean("firstLaunch", true)){
            new QRCreationTask().execute();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Добро пожаловать!")
                    .setMessage("Данное приложение создано исключительно для вашего удобства." + '\n' + '\n' +
                    "Клиентский номер не сбрасывается при переустановке приложения, это значит, что удалив и " +
                            "установив приложение заново вы сохраните свой клиентский номер." + '\n' + '\n' + " ВНИМАНИЕ!  Перед обновлением/сбросом " +
                            "системы во избежании проблем настоятельно рекомендуем вам записать ваш клиентский номер:" +'\n' +
                    String.valueOf(CLIENT_ID))
                    .setIcon(R.drawable.logo)
                    .setCancelable(false)
                    .setPositiveButton("Принять",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    editor.putBoolean("firstLaunch", false);
                                    editor.apply();
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        new GetUserData().execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CardView ZapisCard = (CardView) findViewById(R.id.card_view_zapis);
        assert ZapisCard != null;
        ZapisCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ZapisActivity.class);
                startActivity(intent);
            }
        });

        CardView ZapisCardPhone = (CardView) findViewById(R.id.card_view_zapis_phone);
        CardView OtherAbout = (CardView)findViewById(R.id.card_view_other_1);
        CardView OtherMaps = (CardView)findViewById(R.id.card_view_other_2);
        assert ZapisCardPhone != null;
        assert OtherAbout != null;
        assert OtherMaps != null;
        ZapisCardPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Вы действительно хотите позвонить нам?")
                        .setMessage("Мы работаем с 9:00 до 19:00" + '\n' + '\n' +
                                "*Вызов производится на стационарный телефон")
                        .setIcon(R.drawable.logo)
                        .setCancelable(true)
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton("Вызов",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                                    Manifest.permission.CALL_PHONE)) {
                                                //TODO: Пользователь - дибил
                                            } else {
                                                ActivityCompat.requestPermissions(MainActivity.this,
                                                        new String[]{Manifest.permission.CALL_PHONE},
                                                        0);
                                            }
                                        }else {
                                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "0612209872"));
                                            startActivity(intent);
                                        }
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        OtherAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        OtherMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        CardView qrCard = (CardView) findViewById (R.id.card_view_qr);
        assert qrCard != null;
        qrCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QRActivity.class);
                startActivity(intent);
            }
        });

        new GetNewsTask().execute();
    }

    public void setUserData(){
        ((TextView)findViewById(R.id.toolbar_coins)).setText(String.valueOf(coins));
    }

    public void setPreviewNews(){
        CardView[] cardViews = new CardView[3];
        ImageView[] images = new ImageView[3];
        TextView[] titles = new TextView[3];
        TextView[] prices = new TextView[3];
        cardViews[0] = (CardView)findViewById(R.id.card_view_news_1);
        cardViews[1] = (CardView)findViewById(R.id.card_view_news_2);
        cardViews[2] = (CardView)findViewById(R.id.card_view_news_3);
        images[0] = (ImageView)findViewById(R.id.image_news_1);
        images[1] = (ImageView)findViewById(R.id.image_news_2);
        images[2] = (ImageView)findViewById(R.id.image_news_3);
        titles[0] = (TextView)findViewById(R.id.news_title_1);
        titles[1] = (TextView)findViewById(R.id.news_title_2);
        titles[2] = (TextView)findViewById(R.id.news_title_3);
        prices[0] = (TextView)findViewById(R.id.coins_news_1);
        prices[1] = (TextView)findViewById(R.id.coins_news_2);
        prices[2] = (TextView)findViewById(R.id.coins_news_3);
        for(int x = 0; x < 3; x++){
            final int finalX = x;
            cardViews[x].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    news.get(finalX).image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra("objectType", "news");
                    intent.putExtra("title", news.get(finalX).title);
                    intent.putExtra("text", news.get(finalX).text);
                    intent.putExtra("image", byteArray);
                    intent.putExtra("price", news.get(finalX).price);
                    startActivity(intent);
                }
            });
            titles[x].setText(news.get(x).title);
            images[x].setImageBitmap(news.get(x).image);
            prices[x].setText(String.valueOf(news.get(x).price));
        }
    }

    public void openOthers(View v){
        Intent intent = new Intent(this, OthersActivity.class);
        startActivity(intent);
    }

    public void showExpandedActivity(View v){
        if(v.getId() == R.id.button_news){
            Intent intent = new Intent(MainActivity.this, ExpandedActivity.class);
            intent.putExtra("type", "news");
            intent.putExtra("count", newsCount);
            startActivity(intent);
        }
    }

    class GetUserData extends AsyncTask<Void, Void, String>{

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("http://rodionov-api.zzz.com.ua/get_user_data.php?client_id=" + CLIENT_ID);

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

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            // Вывод JSON
            Log.d("JSON", strJson);
            try {
                Log.d("Start", "Start");
                JSONObject jsonObject = new JSONObject(strJson);
                if(jsonObject.getInt("error") == 0){
                    if(jsonObject.getInt("new") == 1){
                        new_user = true;
                        coins = 10;
                    }else{
                        coins = jsonObject.getInt("coins");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            setUserData();
        }

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
                    newsCount = jsonObject.getInt("count");
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
            setPreviewNews();
        }

    }

    private class QRCreationTask extends AsyncTask<Void, Void, Bitmap> {
        Bitmap bmp;

        protected Bitmap doInBackground(Void... urls) {
            QRCodeWriter writer = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = writer.encode(CLIENT_ID, BarcodeFormat.QR_CODE, 512, 512);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }


            } catch (WriterException e) {
                e.printStackTrace();
            }

            try {
                String filename = "qr.png";
                FileOutputStream stream = openFileOutput(filename, Context.MODE_PRIVATE);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();
                bmp.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bmp;

        }
    }
}
