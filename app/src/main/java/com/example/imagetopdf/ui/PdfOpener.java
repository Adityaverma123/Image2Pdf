package com.example.imagetopdf.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;

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
        File file=getImageFile(fileName);

           Uri path = FileProvider.getUriForFile(this, "com.example.imagetopdf.Utils.FileProvider", file);
        pdfView.fromUri(path);

    }
    private File getImageFile(String fileName) {
        File filePath= Environment.getExternalStorageDirectory();
        File dir=new File(filePath.getAbsolutePath()+"/Image2Pdf");

        File file=new File(dir,fileName);
        return file;
    }

}
