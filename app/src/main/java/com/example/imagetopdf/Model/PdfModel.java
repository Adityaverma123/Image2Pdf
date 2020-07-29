package com.example.imagetopdf.Model;

import android.graphics.pdf.PdfDocument;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class PdfModel implements Serializable {
  PdfDocument pdfDocument;

    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    public PdfModel(PdfDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
    }
}
