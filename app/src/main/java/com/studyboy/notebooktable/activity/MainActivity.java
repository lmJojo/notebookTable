package com.studyboy.notebooktable.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.studyboy.notebooktable.R;
import com.studyboy.notebooktable.SdFileWindow;
import com.studyboy.notebooktable.databaseAndListview.fileList.MyDataBaseHelper;
import com.studyboy.notebooktable.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        LeftFragment.Callbacks{

    private String TAG = "MainActivity";
    /** 文件名、文件内容、最近编辑日期*/
    String fileName = null;
    String fileString = null;
    String fileDatetime = null;

    private MyDataBaseHelper dbHelper;

    /** 界面button 、文件内容、文件名字等显示控件 */
    Button btn_ReName = null;
    Button  btn_Edit = null;
    Button  btn_EditFinish = null;
    Button  btn_Build = null;
    Button  btn_Open = null;
    EditText editText = null;
    EditText text_Name = null;

    LeftFragment leftFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView( );
    }

    /**
     *  初始化 Button、editText 等
     */
    public void initView( ){

        leftFragment = (LeftFragment) getSupportFragmentManager()
                .findFragmentById(R.id.left_fragment) ;

        btn_ReName = (Button) findViewById(R.id.btn_reName);
        btn_ReName.setOnClickListener(this);
        btn_Edit = (Button) findViewById(R.id.btn_edit);
        btn_Edit.setOnClickListener(this);
        btn_EditFinish = (Button) findViewById(R.id.btn_editFinish);
        btn_EditFinish.setOnClickListener(this);
        btn_Build = (Button) findViewById(R.id.btn_build) ;
        btn_Build.setOnClickListener(this);

        btn_Open = (Button) findViewById(R.id.btn_open);
        btn_Open.setOnClickListener(this);

        editText = (EditText)findViewById(R.id.edit_Text);
        text_Name = (EditText) findViewById(R.id.text_name);

    }

    /** 重写接口中的方法，实现回调，获取传递过来的filename */
    @Override
    public void onItemSelected(String name){

        fileName = name;
        // 显示名字
        text_Name.setText(fileName.toCharArray(), 0, fileName.length());
        // 获取文件内容
        fileString = fileUtil.read_innerFile( fileName );
        // 显示文件内容
        editText.setText(fileString.toCharArray(), 0, fileString.length());

        // 文件可编辑
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
        editText.requestFocus();
    }



    @Override
    public void onClick(View view){
        switch(view.getId() ){
            case R.id.btn_edit:
                editText.setFocusableInTouchMode(true);
                editText.setFocusable(true);
                editText.requestFocus();
                break;
            case R.id.btn_editFinish:
                editFinish();
//                editText.setFocusable(false);
                break;
            case R.id.btn_build:
                // 新建，相当于传递名字空的字符串 " "
                onItemSelected("");
                break;
            case R.id.btn_open:
                initPopWindow();
                break;
            case R.id.btn_reName:

                RenameFile();
                break;
        }
    }

    /**
     *  重命名
     */
    public void RenameFile(){
        // 获取新名字
        String newName = text_Name.getText().toString();

        fileUtil.fileReName(fileName,newName);
        // 数据库名字更新
        updateDatabase(fileName,newName);
        // 文件新名字
        fileName = newName;
        leftFragment.listViewShow();
    }

    private String sdFilePath ;
    private FileUtil fileUtil = new FileUtil(this);
    /**
     *  获取打开SD卡存储文件 返回的路径
     */
    @Override
    protected  void onActivityResult(int requestCode ,int resultCode,Intent data){
        switch(requestCode){
            case 1:
                 sdFilePath = data.getStringExtra("data_return");
                if( sdFilePath.equals("")){
                     return ;
                }
                else if(sdFilePath.equals("1")){
                    Toast.makeText(this,"文件过大，暂不支持",Toast.LENGTH_SHORT).show();
                }
                else{
                    openSdFile( sdFilePath );
                }
                break;
            default:
                break;
        }
    }

    public void openSdFile(String filePath){
        String sdFileText = fileUtil.openSDFile( filePath );
        // 显示文件内容
        editText.setText(sdFileText.toCharArray(), 0, sdFileText.length());
        // 根据文件路径获取文件名并显示
        File file = new File( filePath );
        fileName = file.getName();
        text_Name.setText(fileName.toCharArray(), 0, fileName.length());

    }


    /**
     *  编辑完成，保存文件，刷新视图
     */
    public void editFinish(){

          RenameFile();
        // 文件内容
        fileString = editText.getText().toString();

        if(fileName.equals("")){
            int length = fileString.length();
            // 文件名,内容过长则加省略号，否则内容作为标题
            if(length >5){
                fileName = fileString.substring(0,7)+"…";
            } else {
                fileName = fileString.substring(0,length);
            }
        }
//        Toast.makeText(this," 开始编辑了666"+fileName ,Toast.LENGTH_SHORT).show();
        // 文件名显示设置
         text_Name.setText(fileName.toCharArray(), 0, fileName.length());

        // 保存文件到本地
        saveFile();
        // 刷新listView
        leftFragment.listViewShow();
    }

    /** 保存输入文本 */
    public void saveFile(){
        // 保存名字到数据库
        deleteFileInDataBase();
        saveFileToDB();
        // 保存文本到本地
        write_file();
    }

    /** 保存到数据库 */
    public void saveFileToDB( ) {
//        fileName = editText.getText().toString();
        fileDatetime = fileUtil.getTime();
        dbHelper=new MyDataBaseHelper(this ,"file.db",null,1);
        // 若没有 file.db 数据库，则创建
        SQLiteDatabase db = null;

        ContentValues values = new ContentValues();
        values.put("name", fileName);
        values.put("datetime", fileDatetime);
        try {
            db = dbHelper.getWritableDatabase();
            if(db != null){
                db.insert("table_file", null, values);
                Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
                values.clear();
            }
            values = null;
            db.close();
            db = null;
        }catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            try{
                if( db != null){
                    db.close();
                    db = null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /** 数据库中删除数据 , */
    public void deleteFileInDataBase(){

        dbHelper = new MyDataBaseHelper(this,"file.db",null,1);
        // 若没有 file.db 数据库，则创建
        dbHelper.getWritableDatabase();
        SQLiteDatabase db = null;
        try{
            db = dbHelper.getWritableDatabase();
            if(db != null){
                db.delete("table_file","name = ? ",new String[] { fileName });
                db.close();
                db = null;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                if( db != null){
                    db.close();
                    db = null;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    /** 数据库更新名字和时间 */
    public void updateDatabase(String oldName,String newName){
        dbHelper = new MyDataBaseHelper(this ,"file.db",null,1);
        // 若没有 file.db 数据库，则创建
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("datetime",fileUtil.getTime());
        values.put("name",newName);
        try{
            if(db != null){
                db.update("table_file",values,"name = ?",new String[]{ oldName });
            }
            values = null;
            db.close();
            db = null;
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if( db != null){
                    db.close();
                    db = null;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }




    //  写入本地文本，保存
    public void  write_file(){
//        fileString = editText.getText().toString();  // 获取编辑的文本
        try {
             FileOutputStream fileOutput = openFileOutput(fileName, Context.MODE_PRIVATE);
             OutputStreamWriter outputWriter = new OutputStreamWriter(fileOutput,"UTF-8");
             outputWriter.write(fileString);

             outputWriter.flush();
             fileOutput.flush();  //flush是为了输出缓冲区中所有的内容

             outputWriter.close();
             fileOutput.close();  //写入完成后，将两个输出关闭

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /** 删除存储的文件 */
    public void deleteFile( ){
        try{
            File file = new File("/data/data/com.studyboy.notebooktable/files", fileName);
            // 找到文件并删除
            if( file.delete()){
                Toast.makeText(this,"删除成功",Toast.LENGTH_SHORT).show();
            };
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private SdFileWindow sdFileWindow;
    /**
     *  初始化弹窗
     */
    public void initPopWindow(){
        if(sdFileWindow == null ){
            Log.d(TAG, "initPopWindow: *********  初始化弹窗");
            sdFileWindow = new SdFileWindow(this,btn_Open );
            sdFileWindow.setOnListener(new SdFileWindow.OnWindowListen() {
                @Override
                public void onFilePath(String filePath, boolean canOpen) {
                    if( canOpen ){
                        openSdFile( filePath );
                    } else {
                      Toast.makeText(MainActivity.this,"文件过大，暂不支持打开"
                              ,Toast.LENGTH_SHORT ).show();
                    }
                }
            });
        }

        else{
            sdFileWindow.init();
        }

        //
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( grantResults[0] == PackageManager.PERMISSION_GRANTED ){
            Log.d(TAG, "onRequestPermissionsResult: ******* 成功获取权限");
            sdFileWindow.getRootData();
        } else {
            Toast.makeText(MainActivity.this,"获取权限失败",Toast.LENGTH_SHORT ).show();
        }
    }
}
