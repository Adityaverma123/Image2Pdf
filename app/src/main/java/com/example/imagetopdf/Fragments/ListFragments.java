package com.example.imagetopdf.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imagetopdf.Adapters.PdfAdapter;
import com.example.imagetopdf.Model.PdfModel;
import com.example.imagetopdf.R;
import com.example.imagetopdf.Utils.Constants;
import com.example.imagetopdf.Utils.ObjectSerializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListFragments extends Fragment {
    RecyclerView recyclerView;
    List<PdfDocument> pdfLists;
    SharedPreferences sharedPreferences;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    PdfAdapter adapter;
    List<String>names;
    List<String>uris;
    String type="showfiles";
    Context context;
    SwipeRefreshLayout refreshLayout;
    public ListFragments(Context context)
    {
        this.context=context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View  view = inflater.inflate(R.layout.fragment_list_fragments, container, false);preferences = context.getSharedPreferences("home2List", Context.MODE_PRIVATE);
        final String name = preferences.getString("name", null);

        refreshLayout=view.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               loadData();
            }
        });
        loadData();
        buildRecyclerView(view);
//        insertData(name);

        return view;
    }

    public void addReceivedName(String name,String uri)
    {
        insertData(name,uri);
    }


    private void buildRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.pdf_list);
        recyclerView.setHasFixedSize(true);
        adapter=new PdfAdapter(context,names,uris);
        LinearLayoutManager manager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }


    private void insertData(String name,String uri) {

        if(name!=null) {
            refreshLayout.setRefreshing(false);
            Log.i("name", name);
            names.add(0,name);
            uris.add(0,uri);
            adapter.notifyItemInserted(names.size());
            saveData();
            preferences.edit().clear().apply();
        }

    }
    private void saveData()
    {
        sharedPreferences=context.getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        Gson gson=new Gson();
        String json=gson.toJson(names);
        String image=gson.toJson(uris);
        editor.putString("task_list",json);
        editor.putString("task_image",image);
        editor.apply();
    }


    private  void  loadData()
    {
        refreshLayout.setRefreshing(false);
        SharedPreferences sharedPreferences=context.getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
        Gson gson=new Gson();
        String json=sharedPreferences.getString("task_list",null);
        String image=sharedPreferences.getString("task_image",null);

        Type type=new TypeToken<ArrayList<String>>(){}.getType();
        names=gson.fromJson(json,type);
        if(names==null)
        {
            names=new ArrayList<>();
        }
        Type typeimage=new TypeToken<ArrayList<String>>(){}.getType();
        uris=gson.fromJson(image,typeimage);
        if(uris==null)
        {
            uris=new ArrayList<>();
        }
    }


}