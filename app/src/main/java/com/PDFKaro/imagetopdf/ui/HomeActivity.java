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
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    PdfAdapter adapter;
    List<String> names;
    List<String>uris;
    List<String>dates;
    List<String>times;
    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout. activity_home);
        add_image=findViewById(R.id.add_image_home);
        recyclerView = findViewById(R.id.pdf_list);
        preferences=getSharedPreferences("Home2List",Context.MODE_PRIVATE);

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,AddImagesActivity.class);
                startActivity(intent);
            }
        });
        refreshLayout=findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        loadData();
        buildRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("status","pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        String name = preferences.getString("name", null);
        String uri = preferences.getString("uri", null);


        if(name!=null && uri!=null)
        {
            insertData(name,uri);
        }
        else {

            Log.i("value","null");
        }

    }

    private void buildRecyclerView() {
        recyclerView = findViewById(R.id.pdf_list);
        recyclerView.setHasFixedSize(true);
        adapter=new PdfAdapter(this,names,uris,dates,times);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private void insertData(String name,String uri) {

        if(name!=null) {
            refreshLayout.setRefreshing(false);
            Log.i("name", name);
            names.add(0,name);
            uris.add(0,uri);
            dates.add(0,getDate());
            times.add(0,getTime());
            adapter.notifyItemInserted(names.size());
            saveData();
            preferences.edit().clear().apply();
        }

    }
    private String getDate()
    {
        Calendar calendar=Calendar.getInstance();
        String month=calendar.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault());
        String year=String.valueOf(calendar.get(Calendar.YEAR));
        String date=String.valueOf(calendar.get(Calendar.DATE));
        return date+" "+month+" "+year;
    }
    private String getTime()
    {
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        Calendar currentDateTime = Calendar.getInstance();
        String  currentTime = df.format(currentDateTime.getTime());
        return currentTime;
    }

    private void saveData()
    {
        sharedPreferences=getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        Gson gson=new Gson();
        String json=gson.toJson(names);
        String image=gson.toJson(uris);
        String date=gson.toJson(dates);
        String time=gson.toJson(times);
        editor.putString("task_list",json);
        editor.putString("task_image",image);
        editor.putString("task_date",date);
        editor.putString("task_time",time);
        editor.apply();
    }



    private  void  loadData()
    {
        refreshLayout.setRefreshing(false);
        SharedPreferences sharedPreferences=getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
        Gson gson=new Gson();
        String json=sharedPreferences.getString("task_list",null);
        String image=sharedPreferences.getString("task_image",null);
        String date=sharedPreferences.getString("task_date",null);
        String time=sharedPreferences.getString("task_time",null);

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
        Type typeDate=new TypeToken<ArrayList<String>>(){}.getType();
        dates=gson.fromJson(date,typeDate);
        if(dates==null)
        {
            dates=new ArrayList<>();
        }
        Type typeTime=new TypeToken<ArrayList<String>>(){}.getType();
        times=gson.fromJson(time,typeTime);
        if(times==null)
        {
            times=new ArrayList<>();
        }
    }
}
