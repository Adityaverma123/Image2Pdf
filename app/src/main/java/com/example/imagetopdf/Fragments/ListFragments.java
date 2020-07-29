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

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imagetopdf.Adapters.PdfAdapter;
import com.example.imagetopdf.R;
import com.example.imagetopdf.Utils.Constants;
import com.example.imagetopdf.Utils.ObjectSerializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListFragments extends Fragment implements Serializable{
    RecyclerView recyclerView;
    List<PdfDocument> pdfLists;
    SharedPreferences sharedPreferences;
    Context context;
    PdfAdapter adapter;
    List<String>names;
    List<String>pdf;
    List<PdfDocument>documents;
    TextView first;
    public ListFragments(Context context)
    {
        this.context=context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_fragments, container, false);
        recyclerView = view.findViewById(R.id.pdf_list);
        names=new ArrayList<>();
        pdf=new ArrayList<>();
        documents=new ArrayList<>();

        adapter=new PdfAdapter(context,names);
        LinearLayoutManager manager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        return view;
    }
    private void createPdf(List<String> uris)
    {

    }






}
