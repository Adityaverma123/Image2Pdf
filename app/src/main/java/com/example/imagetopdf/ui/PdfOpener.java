package com.example.imagetopdf.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.imagetopdf.Adapters.PdfAdapter;
import com.example.imagetopdf.Model.PdfModel;
import com.example.imagetopdf.R;

import java.util.List;

public class PdfOpener extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_opener);


    }
}
