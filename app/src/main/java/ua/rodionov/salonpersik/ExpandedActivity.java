package ua.rodionov.salonpersik;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ExpandedActivity extends AppCompatActivity {

    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded);
        if(getIntent().getStringExtra("type").equals("news")){
            setTitle("Все новости и акции");
            count = getIntent().getIntExtra("count", 0);

        }
    }
}
