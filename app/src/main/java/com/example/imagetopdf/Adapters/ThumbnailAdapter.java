package com.example.imagetopdf.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagetopdf.Interface.EditImageFragmentListener;
import com.example.imagetopdf.R;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.MyViewHolder> {

    private List<ThumbnailItem>thumbnailItems;
    private EditImageFragmentListener listener;
    private Context context;
    private  int selected_index=0;

    public ThumbnailAdapter(List<ThumbnailItem> thumbnailItems, EditImageFragmentListener listener, Context context) {
        this.thumbnailItems = thumbnailItems;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ThumbnailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.thumbnail_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailAdapter.MyViewHolder holder, final int position) {
        holder.thumbnail.setImageBitmap(thumbnailItems.get(position).image);
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFilterSelected(thumbnailItems.get(position).filter);
                selected_index=position;
                notifyDataSetChanged();
            }
        });
        holder.filter_name.setText(thumbnailItems.get(position).filterName);
        if(selected_index==position)
        {
            holder.filter_name.setTextColor(ContextCompat.getColor(context,R.color.selected_filters));
        }
        else
            holder.filter_name.setTextColor(ContextCompat.getColor(context,R.color.normal_color));
    }

    @Override
    public int getItemCount() {
        return thumbnailItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView filter_name;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail=(ImageView)itemView.findViewById(R.id.thumbnail);
            filter_name=itemView.findViewById(R.id.filter_name);
        }
    }
}
