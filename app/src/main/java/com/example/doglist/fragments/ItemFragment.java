package com.example.doglist.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.doglist.R;
import com.example.doglist.clases.Dogs;
import com.example.doglist.interfaces.DogsInteractionListener;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A fragment representing a list of Items.
 */
public class ItemFragment extends Fragment {

    DogsInteractionListener mListener;
    RealmResults<Dogs> dogsmList;
    Realm realm;

    public ItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();
    }

    public static ItemFragment newInstance(String param1) {
        ItemFragment fragment = new ItemFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dog_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            //Cargamos la lista de perros
            dogsmList = realm.where(Dogs.class).findAll();

            //Relacionamos la actividad, la lista de perros y la interfaz para comunicarse entre ellos.
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(getActivity(), dogsmList, mListener));
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DogsInteractionListener) {
            mListener = (DogsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNotasInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}