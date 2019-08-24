package com.studyboy.notebooktable.databaseAndListview.openfile;



public class FileShow {

    String SDfilePath;
    int imageID;

    public FileShow(String SDfileName , int imageID){
        this.SDfilePath = SDfileName;
        this.imageID =  imageID;
    }

    public String SDfilePath() {
        return SDfilePath;
    }

    public int getImageID() {
        return imageID;
    }



}
