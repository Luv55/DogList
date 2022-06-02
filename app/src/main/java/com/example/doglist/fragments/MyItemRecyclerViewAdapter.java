package com.example.doglist.fragments;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.doglist.R;
import com.example.doglist.clases.Dogs;
import com.example.doglist.interfaces.DogsInteractionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;


public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Dogs> mValues;
    private final DogsInteractionListener mListener;
    private Context ctx;
    private RealmChangeListener listenerRefresh;
    private Realm realm;

    public MyItemRecyclerViewAdapter(FragmentActivity context, RealmResults<Dogs> items, DogsInteractionListener listener) {
        ctx = context;
        mValues = items;
        mListener = listener;
        //Cargamos el listado de perros
        this.listenerRefresh = new RealmChangeListener<OrderedRealmCollection<Dogs>>() {
            public void onChange(OrderedRealmCollection<Dogs> dogs) {
                notifyDataSetChanged();
            }
        };

        if (items != null) {
            addListener(items);
        }
    }

    //Añadimos los perros a la lista
    private void addListener(OrderedRealmCollection<Dogs> items) {
        if (items instanceof RealmResults) {
            RealmResults realmResults = (RealmResults) items;
            realmResults.addChangeListener(listenerRefresh);
        } else if (items instanceof RealmList) {
            RealmList<Dogs> list = (RealmList<Dogs>) items;
            //noinspection unchecke
            list.addChangeListener((RealmChangeListener) listenerRefresh);
        } else {
            throw new IllegalArgumentException("RealmCollection not supported: " + items.getClass());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dog, parent, false);
        realm = Realm.getDefaultInstance();
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final MyItemRecyclerViewAdapter.ViewHolder holder, int position) {

        //Le damos valores a cada item y eventos como el onClick
        holder.mItem = mValues.get(position);
        holder.tv_raza.setText(holder.mItem.getRaza());
        buscarImagenes(holder.mItem.getUrl(),holder.iv_url);
        holder.dogsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(holder.mItem);
            }
        });

    }
    //Buscamos la imagen random del perro
    private void buscarImagenes(String url, ImageView imageView){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if ("success".equals(response.getString("status"))) {
                        //Lo pasamos como string ya que es solo 1 imagen
                        String message = response.getString("message");
                        LoadImage loadImage = new LoadImage(imageView);
                        loadImage.execute(message);

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
        RequestQueue requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        requestQueue.add(request);
    }

    private class LoadImage extends AsyncTask<String, Void, Bitmap>{

        ImageView imageView;

        public LoadImage(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap bitmap = null;
            try {
                //pasamos la imagen a bitmap con el inputstream
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
            //Relacionamos la imagen con el imageView del item
            imageView.setImageBitmap(bitmap);
        }
    }



    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tv_raza;
        public final ImageView iv_url;
        public final LinearLayout dogsContainer;
        public Dogs mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tv_raza = view.findViewById(R.id.tv_raza);
            iv_url = view.findViewById(R.id.iv_perro);
            dogsContainer = view.findViewById(R.id.linearLayoutDog);
        }

    }
}