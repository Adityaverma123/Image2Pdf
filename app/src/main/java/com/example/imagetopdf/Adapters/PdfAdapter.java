package com.example.imagetopdf.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
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

import com.example.imagetopdf.BuildConfig;
import com.example.imagetopdf.R;
import com.example.imagetopdf.Utils.Constants;
import com.example.imagetopdf.ui.HomeScreen;
import com.example.imagetopdf.ui.PdfOpener;

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
                Intent intent=new Intent(context,PdfOpener.class);

                try
                    {
                        intent.putExtra(Constants.SEND_NAME,names.get(position));
                        context.startActivity(intent);
                    }
                    catch(ActivityNotFoundException e)
                    {
                        Toast.makeText(context, "No Application available to view pdf", Toast.LENGTH_LONG).show();
                    }
                }


        });

        holder.pdfName.setText(names.get(position));
    }




    @Override
    public int getItemCount() {
        return names.size();
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
