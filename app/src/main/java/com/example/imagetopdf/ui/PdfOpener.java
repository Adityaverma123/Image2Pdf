package com.example.imagetopdf.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.example.imagetopdf.Adapters.PdfAdapter;
import com.example.imagetopdf.Model.PdfModel;
import com.example.imagetopdf.R;
import com.example.imagetopdf.Utils.Constants;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.List;

public class PdfOpener extends AppCompatActivity {
    PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_opener);
        File filePath= Environment.getExternalStorageDirectory();
        File dir=new File(filePath.getAbsolutePath()+"/Image2Pdf/");

        String fileName=getIntent().getStringExtra(Constants.SEND_NAME);

        String path=dir+"/"+fileName;
        Log.i("path",path);
        File file=new File(dir,fileName);
//        if(file.exists()){


    }
}
