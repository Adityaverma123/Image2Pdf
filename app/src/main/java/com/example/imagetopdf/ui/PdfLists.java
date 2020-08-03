package com.example.imagetopdf.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.example.imagetopdf.Adapters.PdfAdapter;
import com.example.imagetopdf.Model.PdfModel;
import com.example.imagetopdf.R;
import com.example.imagetopdf.Utils.Constants;
import com.example.imagetopdf.Utils.ObjectSerializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PdfLists extends AppCompatActivity implements Serializable {
    RecyclerView recyclerView;
    List<PdfDocument> pdfLists;
    SharedPreferences sharedPreferences;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    PdfAdapter adapter;
    List<String>names;
    PdfModel model;
    String type="showfiles";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_lists);
         preferences = getSharedPreferences("home2List", Context.MODE_PRIVATE);
        String name = preferences.getString("name", null);
       loadData();
       buildRecyclerView();
       insertData(name);


    }

    private void buildRecyclerView() {
        recyclerView = findViewById(R.id.pdf_list);
        recyclerView.setHasFixedSize(true);
        adapter=new PdfAdapter(this,names);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private void insertData(String name) {

        if(name!=null) {
            Log.i("name", name);
            names.add(name);
            adapter.notifyItemInserted(names.size());
            saveData();
            preferences.edit().clear().apply();
        }
        
    }

    private void createList() {

        //sharedPreferences=getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
       // String name=sharedPreferences.getString(Constants.LIST_KEY,"default.pdf");
    }
    private void saveData()
    {
        sharedPreferences=getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        Gson gson=new Gson();
        String json=gson.toJson(names);
        editor.putString("task_list",json);
        editor.apply();
    }



    private  void  loadData()
    {
       SharedPreferences sharedPreferences=getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
       Gson gson=new Gson();
       String json=sharedPreferences.getString("task_list",null);
        Type type=new TypeToken<ArrayList<String>>(){}.getType();
        names=gson.fromJson(json,type);
        if(names==null)
        {
           names=new ArrayList<>();
        }
    }
}
