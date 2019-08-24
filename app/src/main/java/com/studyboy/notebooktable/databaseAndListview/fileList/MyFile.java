package com.studyboy.notebooktable.databaseAndListview.fileList;

public class MyFile {
    public MyFile(String name ,String datetime){
        this.name=name;
        this.datetime=datetime;
    }
    private String name;
    private String datetime;

    public String getName() {
        return name;
    }
    public String getDatetime() {
        return datetime;
    }
}
