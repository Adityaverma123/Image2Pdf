package com.PDFKaro.imagetopdf.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.PDFKaro.imagetopdf.Utils.Constants;
import com.PDFKaro.imagetopdf.Utils.PdfItem;
import com.bumptech.glide.Glide;
import com.PDFKaro.imagetopdf.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.ViewHolder> {
    Context context;
    LinkedList<PdfItem> items;
    public PdfAdapter(Context context,LinkedList<PdfItem>items )
    {
        this.items=items;
        this.context=context;

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

                        items.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,items.size());
                        notifyDataSetChanged();
                        SharedPreferences preferences=context.getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
                        preferences.edit().clear().apply();

                        SharedPreferences.Editor editor=preferences.edit();
                        Gson gson=new Gson();
                        String json=gson.toJson(items);

                        editor.putString("task_list",json);

                        editor.apply();
                        notifyDataSetChanged();
                        Toast.makeText(context, "Item doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                }

            });
            holder.list_date.setText(items.get(position).getDate());
            holder.list_time.setText(items.get(position).getTime());
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
            holder.list_name.setText(items.get(position).getNames());
            holder.edit_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   AlertDialog.Builder builder=new AlertDialog.Builder(context);
                   View view=LayoutInflater.from(context).inflate(R.layout.name_dialog,null,false);
                     final EditText editText=view.findViewById(R.id.name);
                    Button cancel=view.findViewById(R.id.cancel);
                    Button update=view.findViewById(R.id.update);
                    builder.setView(view);
                    //dialog.setContentView(R.layout.name_dialog);
                    final AlertDialog dialog=builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(editText.getText().toString().length()>0) {
                                updateText(editText.getText().toString(), position);
                                dialog.dismiss();
                            }
                            else {
                                Toast.makeText(context,"Name can't be empty",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

        File file = new File(items.get(position).getPath());
        if(file.exists()) {
            Uri path;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                path = FileProvider.getUriForFile(context, "com.PDFKaro.imagetopdf.Utils.FileProvider", file);
            else
                path = Uri.fromFile(file);
            Glide.with(context).load(path).into(holder.list_image);
        }
        else Glide.with(context).load(items.get(position).getUri()).into(holder.list_image);
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

    private void updateText(String name,int position) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(name);
        buffer.append(".pdf");
        File filePath= context.getExternalFilesDir(null);
        File dir=new File(filePath.getPath()+"/PDFKaro");
        File dir1=Environment.getExternalStoragePublicDirectory("PDFKaro");
        File from1=new File(dir1,items.get(position).getNames());
        File from=new File(dir,items.get(position).getNames());
        if(from.exists() && from1.exists()) {
            File to=new File(dir,buffer.toString());
            File to1=new File(dir1, buffer.toString());
            if(to.exists() && to1.exists())
            {
                Log.i("value","true");
                Toast.makeText(context,"File already exists with this name",Toast.LENGTH_SHORT).show();
            }
            else {
                from.renameTo(to);
                from1.renameTo(to1);
                items.get(position).setNames(buffer.toString());
                saveFile();
            }
        }

    }

    private void deleteFile(String filename)
    {
        File filePath= context.getExternalFilesDir(null);
        File dir=new File(filePath.getPath()+"/PDFKaro");
        File file=new File(dir,filename);
        File filepath1=Environment.getExternalStoragePublicDirectory("PDFKaro");
        File file1=new File(filepath1,filename);
        file1.delete();
        file.delete();
    }
    public void saveFile()
    {
        SharedPreferences preferences=context.getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
        preferences.edit().clear().apply();

        SharedPreferences.Editor editor=preferences.edit();
        Gson gson=new Gson();
        String json=gson.toJson(items);
        editor.putString("task_list",json);
        editor.apply();
        notifyDataSetChanged();
    }

    private File getImageFile(int position) {


            File filePath = context.getExternalFilesDir(null);
            File dir = new File(filePath.getAbsolutePath() + "/PDFKaro");

            String fileName = items.get(position).getNames();
            File file = new File(dir, fileName);

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
                deleteFile(items.get(position).getNames());

                items.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,items.size());
                notifyDataSetChanged();
                saveFile();
            }
        },animation.getDuration());


    }



    @Override
    public int getItemCount() {

        return items.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView list_name;
        RelativeLayout layout;
        ImageView delete;
        ImageView share;
        ImageView list_image;
        TextView list_date;
        TextView list_time;
        ImageView edit_name;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

                list_name = itemView.findViewById(R.id.list_name);
                layout = itemView.findViewById(R.id.pdf_viewer);
                delete = itemView.findViewById(R.id.pdf_delete);
                share = itemView.findViewById(R.id.pdf_share);
                list_image=itemView.findViewById(R.id.list_image);
                list_date=itemView.findViewById(R.id.list_date);
                list_time=itemView.findViewById(R.id.list_time);
                edit_name=itemView.findViewById(R.id.edit_name);



        }
    }

}
