package com.example.imagetopdf.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagetopdf.Fragments.AddFileFragment;
import com.example.imagetopdf.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    Activity activity;
    List<Bitmap>bitmaps;
    List<Uri>cropUri;
    public ImageAdapter(Activity activity,List<Bitmap>bitmaps,List<Uri>cropUri)
    {
        this.activity=activity;
        this.bitmaps=bitmaps;
        this.cropUri=cropUri;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item,null,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.imageView.setImageBitmap(bitmaps.get(position));
        holder.imageNo.setText("Image "+(position+1));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCrop(position);
            }
        });

    }

    private void startCrop(int position) {
        CropImage.activity(cropUri.get(position))
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(activity);
    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView imageNo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.gallery_image);
            imageNo=itemView.findViewById(R.id.imageNo);
        }
    }
}
