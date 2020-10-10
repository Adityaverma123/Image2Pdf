package com.PDFKaro.imagetopdf.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.PDFKaro.imagetopdf.Adapters.PdfAdapter;
import com.PDFKaro.imagetopdf.R;
import com.PDFKaro.imagetopdf.Utils.Constants;
import com.PDFKaro.imagetopdf.Utils.PdfItem;
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
    List<PdfItem>items;
    Context context;
    SwipeRefreshLayout refreshLayout;
    public HomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Your PDFs");

        context=getContext();
        add_image=view.findViewById(R.id.add_image_home);
        recyclerView = view.findViewById(R.id.pdf_list);
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
        adapter=new PdfAdapter(context,items);
        LinearLayoutManager manager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }


    private void insertData(String name, String uri,String finalUri) {


            refreshLayout.setRefreshing(false);
            Log.i("name", name);
            items.add(0,new PdfItem(name,getTime(),uri,getDate(),finalUri));
            adapter.notifyItemInserted(items.size());
            saveData();
            preferences.edit().clear().apply();


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
        String json=gson.toJson(items);
        editor.putString("task_list",json);
        editor.apply();
    }



    private  void  loadData()
    {
        refreshLayout.setRefreshing(false);
        SharedPreferences sharedPreferences=context.getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
        Gson gson=new Gson();
        String json=sharedPreferences.getString("task_list",null);
        Type type=new TypeToken<ArrayList<PdfItem>>(){}.getType();
        items=gson.fromJson(json,type);
        if(items==null)
        {
            items=new ArrayList<>();
        }


    }

    public void addReceivedName(String name,String uri,String finalUri)
    {
        insertData(name,uri,finalUri);
    }
}
