package com.example.doglist;

import android.app.Application;

import com.example.doglist.clases.Dogs;

import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class bbddDogs extends Application {

    public static AtomicLong DogsID = new AtomicLong();
    //Base de datos local en realm
    @Override
    public void onCreate() {
        super.onCreate();
        //Llamamos a realm y obtenemos todos los datos guardados
        initRealm();

        Realm realm = Realm.getDefaultInstance();
        //Utilizado para asignarle un id a los perros en la lista principal
        DogsID = getIdByTable(realm, Dogs.class);

        realm.close();
    }

    private void initRealm() {
        //Configuración de la BBDD local con realmn
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration =  new RealmConfiguration.Builder()
                .name("doglist.db")
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(1)
                .allowWritesOnUiThread(true)
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

    }

    private <T extends RealmObject> AtomicLong getIdByTable(Realm realm, Class<T> anyClass){
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() >0) ? new AtomicLong(results.max("id").intValue()) : new AtomicLong();
    }
}
