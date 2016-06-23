package ua.rodionov.salonpersik;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.gcm.Task;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.FileInputStream;
import java.io.InputStream;

public class QRActivity extends AppCompatActivity {

    private String CLIENT_ID;
    ImageView qr_code;
    Bitmap qr_bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        CLIENT_ID = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        for(int i = 0; i < 2; i++) {
            Toast.makeText(getApplicationContext(), "Клиентский номер: " + String.valueOf(CLIENT_ID), Toast.LENGTH_LONG).show();
        }

        qr_code = (ImageView)findViewById(R.id.qr_code);

        try {
            FileInputStream is = openFileInput("qr.png");
            qr_code.setImageBitmap(BitmapFactory.decodeStream(is));
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPressFinish(View v){
        finish();
    }
}
