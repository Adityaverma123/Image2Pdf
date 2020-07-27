package com.example.imagetopdf.Model;

import android.graphics.pdf.PdfDocument;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class PdfModel implements Parcelable {
   String uri;

    protected PdfModel(Parcel in) {
        uri = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uri);
    }

    @Override
    public int describeContents() {
        return 0;
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
}
