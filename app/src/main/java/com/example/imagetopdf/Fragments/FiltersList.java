package com.example.imagetopdf.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imagetopdf.Adapters.ThumbnailAdapter;
import com.example.imagetopdf.Interface.EditImageFragmentListener;
import com.example.imagetopdf.R;
import com.example.imagetopdf.ui.SpaceItemDecoration;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;


public class FiltersList extends Fragment implements EditImageFragmentListener {
    RecyclerView recyclerView;
    ThumbnailAdapter adapter;
    List<ThumbnailItem>thumbnailItems;
    EditImageFragmentListener listener;

    public void setListener(EditImageFragmentListener listener) {
        this.listener = listener;
    }

    public FiltersList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView= inflater.inflate(R.layout.fragment_filters_list, container, false);
        adapter=new ThumbnailAdapter(thumbnailItems,this,getActivity());
        recyclerView=itemView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int space=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpaceItemDecoration(space));
        recyclerView.setAdapter(adapter);
        displayThumbnails(null);
        return  itemView;
    }

    private void displayThumbnails(final Bitmap bitmap) {
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                Bitmap thumbImg;
                if(bitmap==null)
                {
                    //thumbImg=
                     //AddImage
                }
            }
        };
        new Thread(runnable).start();
    }

    @Override
    public void onFilterSelected(Filter filter) {

    }
}
