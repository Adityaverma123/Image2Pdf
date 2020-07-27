package com.example.imagetopdf.Model;

import android.graphics.pdf.PdfDocument;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class PdfModel implements Serializable {
    PdfDocument document;

    public PdfDocument getDocument() {
        return document;
    }

    public void setDocument(PdfDocument document) {
        this.document = document;
    }

    public PdfModel(PdfDocument document) {
        this.document = document;
    }
}
