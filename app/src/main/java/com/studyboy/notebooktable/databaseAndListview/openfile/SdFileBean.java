package com.studyboy.notebooktable.databaseAndListview.openfile;



public class SdFileBean {

    String SDfilePath;
    int imageID;

    public SdFileBean(String SDfileName , int imageID){
        this.SDfilePath = SDfileName;
        this.imageID =  imageID;
    }

    public String getSDfilePath() {
        return SDfilePath;
    }

    public int getImageID() {
        return imageID;
    }



}
