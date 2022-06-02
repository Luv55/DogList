package com.example.doglist.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.doglist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ViewPagerAdapter extends PagerAdapter {

    Context context;

    //Array de imagenes
    Bitmap[] images = new Bitmap[6];
    ArrayList <Bitmap> imagenes;

    // Layout Inflater
    LayoutInflater mLayoutInflater;


    public ViewPagerAdapter(Context context, ArrayList<Bitmap> imagenes) {
        this.context = context;
        this.imagenes = imagenes;

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        //Numero de imagenes
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        Bitmap[] img = imagenes.toArray(images);
        //obtenemos el item para la imagen
        View itemView = mLayoutInflater.inflate(R.layout.image_item, container, false);

        //referenciamos el imageView con el item
        ImageView imageView = itemView.findViewById(R.id.imageViewMain);

        //Le damos al imageview una imagen de Bitmaps
        imageView.setImageBitmap(img[position]);

        //Añadimos el imageView al recyclerView
        Objects.requireNonNull(container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //Elimina las imagenes cargadas anteriormente
        container.removeView((LinearLayout) object);
    }
}
