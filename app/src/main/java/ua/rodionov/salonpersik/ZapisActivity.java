package ua.rodionov.salonpersik;

import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class ZapisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zapis);

        final EditText txtDate=(EditText)findViewById(R.id.txtDate);
        txtDate.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    DateDialog dialog=new DateDialog(view);
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");

                }
            }

        });
        EditText txtTime=(EditText)findViewById(R.id.txtTime);
        txtTime.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    TimeDialog dialog=new TimeDialog(view);
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dialog.show(ft, "TimePicker");

                }
            }

        });

        Button submit_button = (Button)findViewById(R.id.submit_button);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText)findViewById(R.id.txtName)).getText().toString();
                String phone = ((EditText)findViewById(R.id.txtPhone)).getText().toString();
                String date = ((EditText)findViewById(R.id.txtDate)).getText().toString();
                String time = ((EditText)findViewById(R.id.txtTime)).getText().toString();
                String master = ((EditText)findViewById(R.id.txtMaster)).getText().toString();
                String service = ((EditText)findViewById(R.id.txtService)).getText().toString();
                new AddEntry().execute(name,phone,date,time,master,service);
            }
        });
    }

    private class AddEntry extends AsyncTask<String, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://rodionov-api.zzz.com.ua/add_entry.php?name="+params[0]+"&phone="+params[1]+"&date="+params[2]+"&time="+params[3]+"&master="+params[4]+"&service="+params[5]);

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
        protected void onPostExecute(String result){
            if(result.equals("{\"error\":0}")){
                Toast.makeText(getApplicationContext(),"Вы успешно записались! Мы скоро вам перезвоним!", Toast.LENGTH_LONG);
            }else{
                Toast.makeText(getApplicationContext(),"Во время выполнения запроса произошла ошибка! Пожалуйста, повторите попытку позже", Toast.LENGTH_LONG);
            }
        }
    }
}
