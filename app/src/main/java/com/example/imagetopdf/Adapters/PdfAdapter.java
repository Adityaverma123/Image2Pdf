package com.example.imagetopdf.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagetopdf.R;
import com.example.imagetopdf.Utils.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.ViewHolder> {
    Context context;
    List<String >names;
    private static final int TYPE_HEAD=0;
    private static final int TYPE_LIST=1;
    public PdfAdapter(Context context, List<String>names)
    {
        this.context=context;
        this.names=names;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


           View view = LayoutInflater.from(context).inflate(R.layout.pdf_list_item, parent, false);
            return new ViewHolder(view);



    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File file = getImageFile(position);
                    if (file.exists()) {

                        Uri path;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            path = FileProvider.getUriForFile(context, "com.example.imagetopdf.Utils.FileProvider", file);
                        else
                            path = Uri.fromFile(file);
                        MimeTypeMap map = MimeTypeMap.getSingleton();
                        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
                        String type = map.getMimeTypeFromExtension(ext);
                        intent.setDataAndType(path, type);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, path);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try {
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(context, "No Application available to view pdf", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        names.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Item doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                }


            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(holder.itemView, position);
                }
            });
            holder.pdfName.setText(names.get(position));
            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = getImageFile(position);
                    Uri path;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        path = FileProvider.getUriForFile(context, "com.example.imagetopdf.Utils.FileProvider", file);
                    else
                        path = Uri.fromFile(file);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    MimeTypeMap map = MimeTypeMap.getSingleton();
                    String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
                    String type = map.getMimeTypeFromExtension(ext);
                    shareIntent.setDataAndType(path, type);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, path);
                    context.startActivity(Intent.createChooser(shareIntent, "Share Pdf via:"));
                }
            });

    }

    private File getImageFile(int position) {



        File filePath= context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File dir=new File(filePath.getAbsolutePath()+"/Image2Pdf");

        String fileName=names.get(position);
        File file=new File(dir,fileName);
        return file;
    }
    private void deleteItem(View rowView,final int position)
    {
        AlphaAnimation animation=new AlphaAnimation(1.0f,0.0f);
        animation.setDuration(500);
        rowView.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                names.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
                SharedPreferences preferences=context.getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
                preferences.edit().clear().apply();

                SharedPreferences.Editor editor=preferences.edit();
                Gson gson=new Gson();
                String json=gson.toJson(names);
                editor.putString("task_list",json);
                editor.apply();
            }
        },animation.getDuration());


    }


    @Override
    public int getItemViewType(int position) {

        return names.size();

    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView pdfName;
        LinearLayout layout;
        ImageView delete;
        ImageView share;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

                pdfName = itemView.findViewById(R.id.pdf_name);
                layout = itemView.findViewById(R.id.pdf_Layout);
                delete = itemView.findViewById(R.id.delete);
                share = itemView.findViewById(R.id.share);


        }
    }

}
