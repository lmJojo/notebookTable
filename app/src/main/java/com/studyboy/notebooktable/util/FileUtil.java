package com.studyboy.notebooktable.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {
    private Context mContext;
    public FileUtil(Context mContext){
        this.mContext = mContext;
    }
   /**
     *  打开SD 存储文件
     * @param filePath
     * @return
     */
   public   String  openSDFile(String filePath) {
        String path =filePath;  //    "/storage/emulated/0/青花瓷.txt";
        File file = new File(path);
        if( file.exists() ){
            StringBuffer stringBuffer = new StringBuffer();
            try{
                InputStream inputStream = new FileInputStream(file);
                int leng = 0;
                byte[] buffer = new byte[1020];
                while ( ( leng = inputStream.read( buffer )) != -1){
                    stringBuffer.append( new String( buffer,0,leng ));
                }
                inputStream.close();
//                InputStreamReader inputStreamReader = new InputStreamReader(is,"UTF-8");//GBK
//                BufferedReader in = new BufferedReader(inputStreamReader);
//                buffer = new StringBuffer();
//                String line = "";
//                while ((line = in.readLine()) != null) {
//                    buffer.append(line);
//                }
            }catch(Exception e){
                e.printStackTrace();
            }
//            if(stringBuffer.toString().equals("") ){
//
//                Log.d("FileUtil", "openSDFile: *******  咩有文件111111111");
//            }
            return stringBuffer.toString();
        } else {
            return "";
        }

    }

    // 读取本地存储 的文本
    public  String read_innerFile(String fileName){
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try{
            in = mContext.openFileInput(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = " ";
            while((line = reader.readLine()) != null){
                content.append(line);
                content.append("\n");
            }
            in.close();
            reader.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally {
            if(reader!=null){
                try{
                    in.close();
                    reader.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }


    /**
     *  文件重命名
     */
    public void fileReName(String oldName,String newName){
//        String path = "/data/data/com.studyboy.notebooktable/files";
        String path = mContext.getFilesDir().getPath();
        File oldFile = new File(path,oldName);
        File newFile = new File(path,newName);
        oldFile.renameTo(newFile);
    }

    /** 获取时间 */
    public  String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String str = sdf.format(date);
        return str;
    }
}
