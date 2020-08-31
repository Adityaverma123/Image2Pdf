package com.PDFKaro.imagetopdf.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.PDFKaro.imagetopdf.Adapters.PdfAdapter;
import com.PDFKaro.imagetopdf.Interface.RefreshList;
import com.PDFKaro.imagetopdf.R;
import com.PDFKaro.imagetopdf.Utils.Constants;
import com.PDFKaro.imagetopdf.ui.AddImagesActivity;
import com.PDFKaro.imagetopdf.ui.HomeActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment  {
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
    Context context;
    SwipeRefreshLayout refreshLayout;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Your PDFs");

        context=getContext();
        add_image=view.findViewById(R.id.add_image_home);
        recyclerView = view.findViewById(R.id.pdf_list);
        preferences=context.getSharedPreferences("Home2List", Context.MODE_PRIVATE);

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddImageFragment fragment=new AddImageFragment();
                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_layout,fragment).addToBackStack(null);
                transaction.commit();
            }
        });
        refreshLayout=view.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        loadData();
        buildRecyclerView(view);
        return  view;
    }
    private void buildRecyclerView(View view) {
        recyclerView =view.findViewById(R.id.pdf_list);
        recyclerView.setHasFixedSize(true);
        adapter=new PdfAdapter(context,names,uris,dates,times);
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
        sharedPreferences=context.getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
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
        SharedPreferences sharedPreferences=context.getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
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

    public void addReceivedName(String name,String uri)
    {
        insertData(name,uri);
    }
}