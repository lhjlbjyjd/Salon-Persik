package ua.rodionov.salonpersik;

import android.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class OthersActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others);
        findViewById(R.id.phoneButton).setOnClickListener(this);
        findViewById(R.id.mapButton).setOnClickListener(this);
        findViewById(R.id.vkButton).setOnClickListener(this);
        findViewById(R.id.instButton).setOnClickListener(this);
        findViewById(R.id.aboutButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.phoneButton:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                                        if (ActivityCompat.checkSelfPermission(OthersActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            if (ActivityCompat.shouldShowRequestPermissionRationale(OthersActivity.this,
                                                    android.Manifest.permission.CALL_PHONE)) {
                                                //TODO: Пользователь - дибил
                                            } else {
                                                ActivityCompat.requestPermissions(OthersActivity.this,
                                                        new String[]{android.Manifest.permission.CALL_PHONE},
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
                break;
            case R.id.mapButton:
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;
            case R.id.vkButton:
                String url = "http://vk.com/salon_persik_zp";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.instButton:
                String url1 = "http://www.instagram.com/salon.persik/";
                Intent i1 = new Intent(Intent.ACTION_VIEW);
                i1.setData(Uri.parse(url1));
                startActivity(i1);
                break;
            case R.id.aboutButton:
                Intent intent1 = new Intent(this, AboutActivity.class);
                startActivity(intent1);
        }
    }
}
