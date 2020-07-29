package com.example.imagetopdf.ui;

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
import android.util.Log;
import android.widget.Toast;

import com.example.imagetopdf.Adapters.PdfAdapter;
import com.example.imagetopdf.Model.PdfModel;
import com.example.imagetopdf.R;
import com.example.imagetopdf.Utils.Constants;
import com.example.imagetopdf.Utils.ObjectSerializer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PdfLists extends AppCompatActivity implements Serializable {
    RecyclerView recyclerView;
    List<PdfDocument> pdfLists;
    SharedPreferences sharedPreferences;

    PdfAdapter adapter;
    List<String>names;
    PdfModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_lists);
        recyclerView = findViewById(R.id.pdf_list);
        names=new ArrayList<>();
        adapter=new PdfAdapter(this,names);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        createList();

    }

    private void createList() {
        sharedPreferences=getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
        String name=sharedPreferences.getString(Constants.LIST_KEY,"default.pdf");
        names.add(name);
        Log.i("name",name);
        adapter.notifyDataSetChanged();
    }


}
