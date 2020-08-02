package com.example.imagetopdf.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagetopdf.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.List;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.ViewHolder> {
    Context context;
    List<String >names;
    public PdfAdapter(Context context, List<String>names)
    {
        this.context=context;
        this.names=names;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.pdf_list_item,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
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
                }
                else {
                    names.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context,"Item doesn't exist",Toast.LENGTH_SHORT).show();
                }
            }




        });

        holder.pdfName.setText(names.get(position));
    }

    private File getImageFile(int position) {



        File filePath= context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File dir=new File(filePath.getAbsolutePath()+"/Image2Pdf");

        String fileName=names.get(position);
        File file=new File(dir,fileName);
        return file;
    }



    @Override
    public int getItemCount() {
        return (names.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView pdfName;
        RelativeLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pdfName=itemView.findViewById(R.id.pdf_name);
            layout=itemView.findViewById(R.id.pdf_Layout);
        }
    }
}
