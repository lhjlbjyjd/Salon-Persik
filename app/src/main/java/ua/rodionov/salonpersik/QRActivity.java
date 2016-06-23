package ua.rodionov.salonpersik;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.gcm.Task;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

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

        new QRCreationTask(qr_code).execute();
    }

    public void onPressFinish(View v){
        finish();
    }

    private class QRCreationTask extends AsyncTask<Void, Void, Bitmap> {
        ImageView bmImage;
        Bitmap bmp;

        public QRCreationTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

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
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
