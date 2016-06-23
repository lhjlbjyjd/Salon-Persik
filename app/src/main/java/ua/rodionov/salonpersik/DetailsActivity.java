package ua.rodionov.salonpersik;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        switch(getIntent().getStringExtra("objectType")){
            case "news":
                String title = getIntent().getStringExtra("title");
                setTitle(title);
                String text = getIntent().getStringExtra("text");
                ((TextView)findViewById(R.id.txtDetails)).setText(text);
                byte[] byteArray = getIntent().getByteArrayExtra("image");
                Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                ((ImageView)findViewById(R.id.imgHead)).setImageBitmap(image);
                int price = getIntent().getIntExtra("price",0);
                break;
        }
    }
}
