package com.studyboy.notebooktable;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.studyboy.notebooktable.databaseAndListview.openfile.SdFileBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SdFileData {

    private String TAG = "SdFileData";
    private List<SdFileBean>  sdFileBeanList = new ArrayList<>();
    private String filePath ;
    private Context mContext;

    public SdFileData (Context mContext ){
        this.mContext = mContext;
    }
    public List<SdFileBean> getList(){
        return sdFileBeanList;
    }

    private String recentPath;

    /**
     *  获取该路径下的txt文件或文件夹，加入 listView 中显示
     * @param filePath
     */
    public void getFileDirectory( String filePath){


        recentPath = filePath;

        // listView 顶部显示路径
//        tv_headShow.setText("Path ： "+ recentPath);

        File[] files = new File(filePath).listFiles();


        if(files  ==  null){
            Log.d(TAG, "getFileDirectory:******* 空数组 ");
        } else {
            // 文件列表按名称排列
            List fileList = Arrays.asList(files);
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.isDirectory() && o2.isFile())
                        return -1;
                    if (o1.isFile() && o2.isDirectory())
                        return 1;
                    return o1.getName().compareTo(o2.getName());
                }
            });
            // 列表初始化
            initShowList();
            SdFileBean mSdFileBean;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                // 是文件夹或TXT文件
                if ( checkFileShape(file) ) {

                    //  根据文件夹或TXT 获取对应图标
                    if (file.isDirectory()) {
                        mSdFileBean = new SdFileBean(file.getPath(), R.drawable.folder);
                        sdFileBeanList.add( mSdFileBean );
                    } else {
                        mSdFileBean = new SdFileBean(file.getPath(), R.drawable.txt);
                        sdFileBeanList.add( mSdFileBean );
                    }
                }
            }
           // listViewFileShow();
        }

    }

    /**
     *  初始化显示列表，设置第一行为根目录，第二行为返回上一级目录
     */
    public void initShowList( ){
        sdFileBeanList.clear();
        SdFileBean fileBean = new SdFileBean("返回根目录",R.drawable.folder);
        sdFileBeanList.add(fileBean);
        fileBean = new SdFileBean("返回上一级",R.drawable.folder);
        sdFileBeanList.add(fileBean);
    }

    /**
     *  判断文件类型
     * @param file
     * @return
     */
    public boolean checkFileShape(File file){

        boolean checkShape;
        String nameString  = file.getName();
        int length = nameString.length();
        // 获取文件后缀
        String endString = nameString.substring(nameString.lastIndexOf(".")+1,length) .toLowerCase();
        // 是否为文件夹
        if(file.isDirectory()){
            checkShape = true;
        }
        else {
            if(endString.equals("txt")){
                checkShape = true;
            } else{
                checkShape = false;
            }
        }
        return  checkShape;
    }


    /**
     *  获取根目录文件列表
     */
    public String getSDRoot(){

        String rootPath = "";
        try{
            // 获取根目录
            rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        } catch(Exception e){
            Toast.makeText( mContext," 打不开诶 ",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return rootPath;
    }

}
