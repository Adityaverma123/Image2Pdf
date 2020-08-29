package com.PDFKaro.imagetopdf.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.PDFKaro.imagetopdf.Adapters.PdfAdapter;
import com.PDFKaro.imagetopdf.Utils.Constants;
import com.PDFKaro.imagetopdf.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private Button add_image;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;

    PdfAdapter adapter;
    List<String> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout. activity_home);
        add_image=findViewById(R.id.add_image_home);
        recyclerView = findViewById(R.id.pdf_list);
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,AddImagesActivity.class);
                startActivity(intent);
            }
        });
        names=new ArrayList<>();
       // adapter=new PdfAdapter(this,names);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        //createList();
    }
}
