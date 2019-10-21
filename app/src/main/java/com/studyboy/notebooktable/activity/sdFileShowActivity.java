package com.studyboy.notebooktable.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.studyboy.notebooktable.R;
import com.studyboy.notebooktable.databaseAndListview.openfile.FileShow;
import com.studyboy.notebooktable.databaseAndListview.openfile.MyFileAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *   显示打开内存目录的结果
 */
public class sdFileShowActivity extends AppCompatActivity {

    private String TAG = "sdFileShowActivity";
    String file_text = null;

    private List<FileShow>  fileShowList = new ArrayList<>();
    TextView tv_headShow ;
    ListView listView;


    private String rootPath ; //= getSDRoot()
    /** 目前路径*/
    private String recentPath;
    /** 确定有无返回值 */
    private boolean hasReturn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdfile_show_layout);
        setWindows();

        tv_headShow = (TextView ) findViewById(R.id.tv_headShow);
        listView = (ListView) findViewById(R.id.listView_file);

        getPermission();

        Log.d(TAG, "onCreate: ******* rootPath="+rootPath);

    }

    /**
     *  获取该路径下的txt文件或文件夹，加入 listView 中显示
     * @param filePath
     */
    public void getFileDirectory( String filePath){


        recentPath = filePath;

        // listView 顶部显示路径
        tv_headShow.setText("Path ： "+ recentPath);

        File file = new File(filePath);
//        file.getPath();
        File[] files;
        files = file.listFiles();
        FileShow fileShow;
        // 文件列表按时间降序排列
//        Arrays.sort(files, new Comparator<File>() {
//            public int compare(File f1, File f2) {
//                long diff = f1.lastModified() - f2.lastModified();
//                if (diff > 0)
//                    return -1;
//                else if (diff == 0)
//                    return 0;
//                else
//                    return 1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
//            }
//            public boolean equals(Object obj) {
//                return true;
//            }
//        });
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
            // fileShowList 列表的初始化
            initShowList();
            for (int i = 0; i < files.length; i++) {
                file = files[i];
                // 是文件夹或TXT文件
                if (checkFileShape(file)) {

                    //  根据文件夹或TXT 获取对应图标
                    if (file.isDirectory()) {
                        fileShow = new FileShow(file.getPath(), R.drawable.folder);
                        fileShowList.add(fileShow);
                    } else {
                        fileShow = new FileShow(file.getPath(), R.drawable.txt);
                        fileShowList.add(fileShow);
                    }
                }
            }
            listViewFileShow();
        }
    }

    /**
     *  文件夹列表显示及点击监听,文件夹则打开，txt 文件则返回其路径
     */
    public void listViewFileShow( ){
        MyFileAdapter adapter = new MyFileAdapter(sdFileShowActivity.this,
                R.layout.list_sdfile_item , fileShowList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                FileShow fileShow = fileShowList.get(position);
                File newFile = new File( fileShow.SDfilePath() ); // 考虑换成getSDFilePath

                // 返回根目录
                if(position == 0){
                    getFileDirectory(rootPath);
                }
                // 返回上一级目录,SDfileName 为“ 返回上一级 ”，故加入 recentPath 作当前路径
                else if(position == 1){
//                    String parentFile = newFile.getName();
                    File parentFile = new File(recentPath);
                    if(!parentFile.exists() || parentFile.length() == 0) {
                        System.out.println("上级目录不存在");
                    }
                    else{
                        getFileDirectory( parentFile.getParent());
                    }
                }
                // 点击文件夹或TXT文件
                else{

                    if(newFile.isDirectory()){
                        getFileDirectory(fileShow.SDfilePath());
                    }
                    else{
                        // TXT文件，返回其完整路径
                        Intent intent = new Intent();
                        long fileLength = newFile.length();
                        if(fileLength > 716800 ){
                            // 文件过大，不支持
                            intent.putExtra("data_return","1");
                        }
                        else{
                            intent.putExtra("data_return",fileShow.SDfilePath());
                        }
                        setResult(RESULT_OK,intent);
                        // 用于判断是否有返回值
                        hasReturn = true;
                        finish();
                    }
                }

            }
        });
    }

    /**
     *  初始化显示列表，设置第一行为根目录，第二行为返回上一级目录
     */
    public void initShowList( ){
        fileShowList.clear();
        FileShow fileShow = new FileShow("返回根目录",R.drawable.folder);
        fileShowList.add(fileShow);
        fileShow = new FileShow("返回上一级",R.drawable.folder);
        fileShowList.add(fileShow);
        fileShow = null;
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

    public void  showFinish(){
        Intent intent = new Intent();
        intent.putExtra("data_return", "open MySdFile");
        setResult(RESULT_OK, intent);
        finish();
    }


    /**
     *  设置dialog形式 Activity 的属性
     */
    public void setWindows(){
        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.LEFT | Gravity.TOP;//设置对话框置顶显示
        win.setAttributes(lp);
    }


    /**
     *  检查获取权限
     * @return
     */
    public void getPermission(){

        if (Build.VERSION.SDK_INT >= 23) {
            // 检查权限
            int readCheck = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE );
            int writeCheck = checkSelfPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE );
            if( readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED ){
                // 已有权限
                getSDRoot();
            } else {
                // api > 23 还需要手动申请权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else {

            String sdString = Environment.getExternalStorageState();
            if ( sdString.equals( Environment.MEDIA_MOUNTED ) ) {
                getSDRoot();

            } else {
                Toast.makeText(this,"no sdcard",Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     *  获取根目录
     */
    public void getSDRoot(){

        try{
            // 获取根目录
            rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            //
            getFileDirectory(rootPath);

        } catch(Exception e){
            Toast.makeText(this," 打不开诶 ",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "onRequestPermissionsResult: ******* 成功获取权限");
            getSDRoot();

        }
    }

    @Override
    public void finish() {

        // 没有点击就退出
        if(!hasReturn){
            Intent intent = new Intent();
            intent.putExtra("data_return", "");
            setResult(RESULT_OK, intent);
        }
        super.finish();
    }
}
