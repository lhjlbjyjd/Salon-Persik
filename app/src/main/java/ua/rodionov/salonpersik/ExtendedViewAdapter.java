package ua.rodionov.salonpersik;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Дмитрий on 23.06.2016.
 */
public class ExtendedViewAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<News> news = new ArrayList<>();

    public ExtendedViewAdapter(Context c, ArrayList<News> _news) {
        mContext = c;
        news = _news;
    }

    public int getCount() {
        return news.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        final int finalX = position;

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {
            gridView = new View(mContext);

            // get layout from grid_item.xml ( Defined Below )

            gridView = inflater.inflate( R.layout.extended_item , null);

            // set value into textview

            TextView textView = (TextView) gridView
                    .findViewById(R.id.txtTitle);

            textView.setText(news.get(position).title);

            // set image based on selected text

            ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.image);

            imageView.setImageBitmap(news.get(position).image);

            TextView price = (TextView) gridView.findViewById(R.id.coins);
            price.setText(String.valueOf(news.get(position).price));

            CardView card = (CardView) gridView.findViewById(R.id.card);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailsActivity.class);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    news.get(finalX).image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra("objectType", "news");
                    intent.putExtra("title", news.get(finalX).title);
                    intent.putExtra("text", news.get(finalX).text);
                    intent.putExtra("image", byteArray);
                    intent.putExtra("price", news.get(finalX).price);
                    mContext.startActivity(intent);
                }
            });
        } else {
            gridView = convertView;
        }
        return gridView;
    }
}