package com.example.doglist.activityes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.doglist.fragments.ItemFragment;
import com.example.doglist.R;
import com.example.doglist.clases.Dogs;
import com.example.doglist.interfaces.DogsInteractionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements DogsInteractionListener {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Asociamos el FragmentContainer a el listado
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragmentContainer, ItemFragment.class, null)
                .commit();

        realm = Realm.getDefaultInstance();

        //Utilizamos shared preferences para comprobar si es la primera vez que se accede a la aplicación y así evitar cargar datos de nuevo.
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        if (!sharedPreferences.contains("primeraVez")){
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("primeraVez", "true");
            myEdit.commit();
        }

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String primeraVez = sh.getString("primeraVez", "");
        if (primeraVez.equals("true")){
            cargarPerro();
            SharedPreferences sharedPreferences2 = getSharedPreferences("MySharedPref",MODE_PRIVATE);
            SharedPreferences.Editor myEdit2 = sharedPreferences2.edit();
            myEdit2.putString("primeraVez", "false");
            myEdit2.commit();
        }




    }
    //Si es la primera vez que se accede a la aplicación se carga el listado de perros a traves de la URL
    private void cargarPerro() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://dog.ceo/api/breeds/list/all", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if ("success".equals(response.getString("status"))) {
                        JSONObject message = response.getJSONObject("message");
                        Iterator keys = message.keys();
                        List<String> allBreedsList = new ArrayList();
                        //Recorremos cada elemento del JSON devuelto por la URl
                        while (keys.hasNext()) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    //Aquí guardamos en realm la raza y la url de cara raza de perro.
                                    String raza = (String) keys.next();
                                    Dogs newDog = new Dogs(raza, "https://dog.ceo/api/breed/"+raza+"/images/random" );
                                    realm.copyToRealmOrUpdate(newDog);
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    Log.d("error", e.toString(), null);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error",  error.toString(), null);
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    @Override
    public void onClick(Dogs dogs) {
        //Al pulsar sobre un objeto del RecyclerView abrimos una nueva actividad con las imagenes del perro
        Intent intent = new Intent(getApplicationContext(), ViewDogs.class);
        intent.putExtra("raza", dogs.getRaza());
        startActivity(intent);

    }
}