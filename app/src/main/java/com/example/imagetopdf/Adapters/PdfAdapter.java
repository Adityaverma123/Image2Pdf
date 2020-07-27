package com.example.imagetopdf.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagetopdf.R;

import java.util.List;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.ViewHolder> {
    Context context;
    List<String >names;
    List<PdfDocument>pdfDocuments;

    public PdfAdapter(Context context, List<String>names,List<PdfDocument>pdfDocuments)
    {
        this.pdfDocuments=pdfDocuments;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.pdfName.setText(names.get(position));
    }

    @Override
    public int getItemCount() {
        return 0;
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
