package com.example.doglist.interfaces;

import com.example.doglist.clases.Dogs;

//Comunicación entre Fragment y Activity
public interface DogsInteractionListener {
    void onClick(Dogs dogs);
}
