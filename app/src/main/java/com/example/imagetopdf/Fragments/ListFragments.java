package com.example.imagetopdf.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imagetopdf.Adapters.PdfAdapter;
import com.example.imagetopdf.R;
import com.example.imagetopdf.Utils.Constants;
import com.example.imagetopdf.Utils.ObjectSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListFragments extends Fragment {
    RecyclerView recyclerView;
    List<PdfDocument> pdfLists;
    SharedPreferences sharedPreferences;
    Context context;
    PdfAdapter adapter;
    List<String>names;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_fragments, container, false);
        context = getContext();
        recyclerView = view.findViewById(R.id.pdf_list);
        names=new ArrayList<>();
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        try {
            pdfLists = (List<PdfDocument>) ObjectSerializer.deserialize(sharedPreferences.getString(Constants.LIST_KEY, ObjectSerializer.serialize(new ArrayList<PdfDocument>())));

        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter=new PdfAdapter(context,names,pdfLists);
        if(pdfLists!=null) {
            for (int i = 0; i < pdfLists.size(); i++) {
                String name=UUID.randomUUID().toString();
                names.add(name);
                adapter.notifyDataSetChanged();
            }
        }

        return view;
    }
}
