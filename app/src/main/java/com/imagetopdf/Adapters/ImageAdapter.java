package com.imagetopdf.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.imagetopdf.Interface.OnChangePic;
import com.imagetopdf.Interface.Visibility;
import com.imagetopdf.R;
import com.imagetopdf.Utils.Constants;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    Context context;
    List<Uri>uris;
    public int cropNo;
    List<Uri>cropUri;
    OnChangePic onChangePic;
    OnItemClickListener onItemClickListener;
    Visibility visibility;

    public int getCropNo() {
        return cropNo;
    }

    public void setCropNo(int cropNo) {
        this.cropNo = cropNo;
    }

    Activity activity;
    public ImageAdapter(Context context,List<Uri>uris,OnItemClickListener onItemClickListener,Visibility visibility)
    {
        this.visibility=visibility;
        this.onItemClickListener=onItemClickListener;
        this.cropUri=cropUri;
        this.context=context;
        this.uris=uris;
        this.activity=activity;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.image_item,null,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        //holder.imageView.setImageURI(uris.get(position));
        Glide.with(context).load(uris.get(position)).into(holder.imageView);
        holder.imageNo.setText("Image "+(position+1));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              onItemClickListener.onItemClicked(position,view);
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("button pressed","pressed");
                uris.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,uris.size());
            }
        });
    }

    private void startCrop(int position) {
        setCropNo(position);
      Intent intent= CropImage.activity(uris.get(position))
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(activity);
      intent.putExtra("requestcode", Constants.CHANGE_NO);
     activity.startActivityForResult(intent,Constants.CHANGE_NO);


    }
    public interface OnItemClickListener {
        void onItemClicked(int position,View view);
    }



    @Override
    public int getItemCount() {
        if(uris.size()==0){
            visibility.setVisibility(false);
            return 0;
        }
        else {
            visibility.setVisibility(true);
            return uris.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView imageView;
        TextView imageNo;
        ImageView deleteBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.gallery_image);
            imageNo=itemView.findViewById(R.id.imageNo);
            deleteBtn=itemView.findViewById(R.id.deleteIcon);

        }
    }
}
