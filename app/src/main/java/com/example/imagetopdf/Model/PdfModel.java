package com.example.imagetopdf.Model;

import android.graphics.pdf.PdfDocument;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class PdfModel implements Parcelable {
    PdfDocument pdfDocument;

    protected PdfModel(Parcel in) {

    }

    public static final Creator<PdfModel> CREATOR = new Creator<PdfModel>() {
        @Override
        public PdfModel createFromParcel(Parcel in) {
            return new PdfModel(in);
        }

        @Override
        public PdfModel[] newArray(int size) {
            return new PdfModel[size];
        }
    };

    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    public void setPdfDocument(PdfDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    public PdfModel(PdfDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
