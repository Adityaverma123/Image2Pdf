package com.PDFKaro.imagetopdf.Utils;

public class PdfItem {
    private String names;
    private String time;
    private String uri;
    private String date;
    private String path;

    public PdfItem(String names, String time, String uri, String date, String path) {
        this.names = names;
        this.time = time;
        this.uri = uri;
        this.date = date;
        this.path = path;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}