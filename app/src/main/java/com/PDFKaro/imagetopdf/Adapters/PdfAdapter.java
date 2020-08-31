package com.PDFKaro.imagetopdf.Adapters;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.PDFKaro.imagetopdf.Utils.Constants;
import com.bumptech.glide.Glide;
import com.PDFKaro.imagetopdf.R;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.ViewHolder> {
    Context context;
    List<String >names;
    List<String >uris;
    List<String>dates;
    List<String>times;
    public PdfAdapter(Context context, List<String>names,List<String>uris,List<String>dates,List<String>times)
    {
        this.times=times;
        this.dates=dates;
        this.uris=uris;
        this.context=context;
        this.names=names;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


           View view = LayoutInflater.from(context).inflate(R.layout.pdf_item, parent, false);
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
                            path = FileProvider.getUriForFile(context, "com.PDFKaro.imagetopdf.Utils.FileProvider", file);
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
                        dates.remove(position);
                        uris.remove(position);
                        times.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,names.size());
                        notifyDataSetChanged();
                        SharedPreferences preferences=context.getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
                        preferences.edit().clear().apply();

                        SharedPreferences.Editor editor=preferences.edit();
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
                        notifyDataSetChanged();
                        Toast.makeText(context, "Item doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                }

            });
            holder.list_date.setText(dates.get(position));
            holder.list_time.setText(times.get(position));
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog=new AlertDialog.Builder(context);
                    dialog.setTitle("Delete Item");
                    dialog.setMessage("Do you really want to delete this Pdf?")

                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteItem(holder.itemView, position);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();

                }

            });
            holder.list_name.setText(names.get(position));
        Glide.with(context).load(Uri.parse(uris.get(position))).into(holder.list_image);
           // holder.list_image.setImageURI(Uri.parse(uris.get(position)));
            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = getImageFile(position);
                    Uri path;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        path = FileProvider.getUriForFile(context, "com.PDFKaro.imagetopdf.Utils.FileProvider", file);
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
    private void deleteFile(String filename)
    {
        File filePath= Environment.getExternalStorageDirectory();
        File dir=new File(filePath.getAbsolutePath()+"/PDFKaro");
        File file=new File(dir,filename);
        file.delete();
    }

    private File getImageFile(int position) {

        File filePath= Environment.getExternalStorageDirectory();
        File dir=new File(filePath.getAbsolutePath()+"/PDFKaro");

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
                deleteFile(names.get(position));
                names.remove(position);
                dates.remove(position);
                uris.remove(position);
                times.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,names.size());
                notifyDataSetChanged();
                SharedPreferences preferences=context.getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
                preferences.edit().clear().apply();

                SharedPreferences.Editor editor=preferences.edit();
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
                notifyDataSetChanged();
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
        TextView list_name;
        RelativeLayout layout;
        ImageView delete;
        ImageView share;
        ImageView list_image;
        TextView list_date;
        TextView list_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

                list_name = itemView.findViewById(R.id.list_name);
                layout = itemView.findViewById(R.id.pdf_viewer);
                delete = itemView.findViewById(R.id.pdf_delete);
                share = itemView.findViewById(R.id.pdf_share);
                list_image=itemView.findViewById(R.id.list_image);
                list_date=itemView.findViewById(R.id.list_date);
                list_time=itemView.findViewById(R.id.list_time);


        }
    }

}