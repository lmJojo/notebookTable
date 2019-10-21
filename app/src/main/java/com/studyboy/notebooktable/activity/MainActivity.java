package com.studyboy.notebooktable.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.studyboy.notebooktable.R;
import com.studyboy.notebooktable.databaseAndListview.fileList.MyDataBaseHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        LeftFragment.Callbacks{

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
//        listView = (ListView)findViewById(R.id.@android:id/list)

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
        // 显示文件名
//        text_Name.setText(fileName.toCharArray(), 0, fileName.length());
    }

    /** 重写接口中的方法，实现回调，获取传递过来的filename */
    @Override
    public void onItemSelected(String name){

        fileName = name;
        // 显示名字
        text_Name.setText(fileName.toCharArray(), 0, fileName.length());
        // 获取文件内容
        fileString = read_innerFile();
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
                Intent intent = new Intent(MainActivity.this, sdFileShowActivity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.btn_reName:

                reNameFile();
//                editFinish();
                break;
        }
    }

    /**
     *  重命名
     */
    public void reNameFile(){
        // 获取新名字
        String newName = text_Name.getText().toString();

        fileReName(fileName,newName);
        // 数据库名字更新
        updateDatabase(fileName,newName);
        // 文件新名字
        fileName = newName;
        leftFragment.listViewShow();
    }

    private String sdFilePath ;
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
                    String sdFileText = openSDFile(sdFilePath);
                    // 显示文件内容
                    editText.setText(sdFileText.toCharArray(), 0, sdFileText.length());
                    // 根据文件路径获取文件名并显示
                    File file = new File(sdFilePath);
                    fileName = file.getName();
                    text_Name.setText(fileName.toCharArray(), 0, fileName.length());
                }
                break;
            default:
                break;
        }
    }

    /**
     *  打开SD 存储文件
     * @param filePath
     * @return
     */
    public String  openSDFile(String filePath) {
        String path =filePath;  //    "/storage/emulated/0/青花瓷.txt";
        File file = new File(path);
        StringBuffer buffer= null;
        try{
            InputStream is = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(is,"UTF-8");//GBK
            BufferedReader in = new BufferedReader(inputStreamReader);
            buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        if(buffer.toString().equals("") ){
            Toast.makeText(this, " 咩有文件111111111", Toast.LENGTH_SHORT ).show();
        }
        return buffer.toString();
    }

    /**
     *  编辑完成，保存文件，刷新视图
     */
    public void editFinish(){

          reNameFile();
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
        fileDatetime = getTime();
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
        values.put("datetime",getTime());
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


       // 读取本地存储 的文本
    public String read_innerFile(){
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try{
            in = openFileInput(fileName);
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
    /**
     *  文件重命名
     */
    public void fileReName(String oldName,String newName){
//        String path = "/data/data/com.studyboy.notebooktable/files";
        String path = getFilesDir().getPath();
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
