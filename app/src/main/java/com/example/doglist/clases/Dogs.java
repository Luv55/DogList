package com.example.doglist.clases;

import com.example.doglist.bbddDogs;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Dogs extends RealmObject {
    public static final String DOGS_ID = "id";
    public static final String DOGS_RAZA = "name";
    public static final String DOGS_URL = "description";

    @PrimaryKey
    private long id;
    private String raza;
    private String url;

    public Dogs() {
    }

    public Dogs(String raza, String url) {
        this.id = bbddDogs.DogsID.incrementAndGet();
        this.raza = raza;
        this.url = url;
    }

    public Dogs(long id, String raza, String url) {
        this.id = id;
        this.raza = raza;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Dogs{" +
                "id=" + id +
                ", raza='" + raza + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
