package com.example.doglist.activityes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.doglist.R;
import com.example.doglist.fragments.ViewPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class ViewDogs extends AppCompatActivity {

    ViewPager mViewPager;

    ViewPagerAdapter mViewPagerAdapter;

    //Array de imagenes
    ArrayList<Bitmap> imagenes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_dogs);

        //llamadas al intent
        Intent intent = getIntent();
        String raza = intent.getStringExtra("raza");

        mViewPager = findViewById(R.id.viewPagerMain);

        mViewPagerAdapter = new ViewPagerAdapter(this,imagenes);
        //Buscamos todas las imagenes de la raza
        String url = "https://dog.ceo/api/breed/"+raza+"/images";
        buscarImagenes(url);

        //Añadimos el adaptador al viewpager con un retraso de 1seg para dar tiempo a que carguen las primeras imagenes
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mViewPager.setAdapter(mViewPagerAdapter);
            }
        },1000);

    }

    private void buscarImagenes(String url){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    if ("success".equals(response.getString("status"))) {
                        JSONArray message = response.getJSONArray("message");
                        for (int i=0;i<6; i++){

                            //Obtenemos el array de url y las mandamos al LoadImage para obtener sus Bitmaps
                            LoadImage loadImage = new LoadImage();
                            loadImage.execute(message.getString(i));
                        }


                    }
                } catch (JSONException e) {
                    Log.d("error", e.toString(), null);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString(), null);
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Realizamos la llamada de Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    private class LoadImage extends AsyncTask<String, Void, Bitmap> {


        public LoadImage() {}

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap bitmap = null;
            try {
                //Con el inputStream obtenemos la imagen en Bitmaps
                InputStream inputStream = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            //Añadimos la imagen a un Arraylist para despues enviarla al Adaptador
            imagenes.add(bitmap);
        }
    }

}