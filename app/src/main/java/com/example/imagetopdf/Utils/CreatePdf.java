package com.example.imagetopdf.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class CreatePdf extends AsyncTask<Void,Void,String> {
    List<String>uris;
    Context context;
    public CreatePdf(List<String>uris, Context context) {
        this.uris=uris;
        this.context=context;
    }
    @Override
    protected String doInBackground(Void... voids) {
        File filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File dir = new File(filePath.getAbsolutePath() + "/Image2Pdf");
        if (!dir.exists()) {
            dir.mkdir();
        }
        String filename = System.currentTimeMillis() + ".pdf";
//            PdfDocument document = new PdfDocument();
        Rectangle pageSize = new Rectangle(PageSize.getRectangle("Default"));
        Document document1 = new Document(PageSize.A4,50,38,50,38);
        document1.setMargins(50,38,50,38);
        try {
            PdfWriter.getInstance(document1, new FileOutputStream(dir + "/" + filename));
            document1.open();
            for (int j = 0; j < uris.size(); j++) {
                int quality=30;
                double qualitymode=quality*0.9;
                Image image=Image.getInstance(uris.get(j));
                image.setCompressionLevel((int) qualitymode);
                image.setBorder(Rectangle.BOX);
                image.setBorderWidth(0);
//                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//                Bitmap bitmap = BitmapFactory.decodeFile(uris.get(j), bmOptions);
                float pageWidth = document1.getPageSize().getWidth() - (50 + 38);
                float pageHeight = document1.getPageSize().getHeight() - (38 + 50);
                //Bitmap sample = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uris.get(j)));
                //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                /*
                sample.compress(Bitmap.CompressFormat.PNG, 70, stream);
                Image image = Image.getInstance(stream.toByteArray());
                                float scaler = ((document1.getPageSize().getWidth() - document1.leftMargin()
                                        - document1.rightMargin() - 0) / image.getWidth()) * 100;
                */
                image.scaleAbsolute(pageWidth,pageHeight);
               // image.setAbsolutePosition();
               // image.scalePercent(scaler);
                image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                document1.add(image);
                document1.newPage();
            }
        } catch (Exception e) {
            Log.i("error", e.getMessage());

        }
        return null;
    }

}
