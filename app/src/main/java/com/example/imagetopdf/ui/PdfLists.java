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
import android.util.Log;
import android.widget.Toast;

import com.example.imagetopdf.Adapters.PdfAdapter;
import com.example.imagetopdf.R;
import com.example.imagetopdf.Utils.Constants;
import com.example.imagetopdf.Utils.ObjectSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PdfLists extends AppCompatActivity {
    RecyclerView recyclerView;
    List<PdfDocument> pdfLists;
    SharedPreferences sharedPreferences;

    PdfAdapter adapter;
    List<String>names;
    List<String>pdf;
    List<PdfDocument>documents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_lists);
        recyclerView = findViewById(R.id.pdf_list);
        names=new ArrayList<>();
        pdf=new ArrayList<>();
        documents=new ArrayList<>();

        adapter=new PdfAdapter(this,names,pdfLists);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        sharedPreferences=getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);

        try {
            createPdf((List<String>) ObjectSerializer.deserialize(sharedPreferences.getString(Constants.LIST_KEY,ObjectSerializer.serialize(new ArrayList<String>()))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void createPdf(List<String> uris)
    {
        try {


            PdfDocument document = new PdfDocument();
            for (int i = 0; i < uris.size(); i++) {
                Bitmap bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(Uri.parse(uris.get(i))));
                PdfDocument.PageInfo pageInfo=new PdfDocument.PageInfo.Builder(bitmap.getWidth(),bitmap.getHeight(),1).create();
                PdfDocument.Page page=document.startPage(pageInfo);
                document.finishPage(page);
            }
            document.close();
            Log.i("pdf",document.toString());
            documents.add(document);
            if(documents!=null) {
                for (int i = 0; i < documents.size(); i++) {
                    String name= UUID.randomUUID().toString();
                    names.add(name);
                    adapter.notifyDataSetChanged();
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            Log.i("error",e.getMessage());
        }
    }
}
