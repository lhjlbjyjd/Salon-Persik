package ua.rodionov.salonpersik;

import android.graphics.Bitmap;

public class News {

    String title,text;
    Bitmap image;
    int price;

    public News (String _title, String _text, Bitmap _image, int _price){
        title = _title;
        text = _text;
        image = _image;
        price = _price;
    }
}
