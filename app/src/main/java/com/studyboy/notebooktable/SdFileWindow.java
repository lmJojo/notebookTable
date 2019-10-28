package com.studyboy.notebooktable;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.studyboy.notebooktable.databaseAndListview.openfile.SdFileAdapter;
import com.studyboy.notebooktable.databaseAndListview.openfile.SdFileBean;
import com.studyboy.notebooktable.util.DimenUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 用弹窗替换 activity 显示 ,暂时放下
 */
public class SdFileWindow {

    private String TAG = "SdFileWindow";
    private Context mContext;
    private View btnView;
    public SdFileWindow(Context mContext,View btnOpen){
        this.mContext = mContext;
        btnView = btnOpen;
        init();
    }

    private View contentView ;
    private TextView tv_path;
    private ListView sdfileList;
    public PopupWindow popupWindow;
    private int width,height;

    /**
     * 初始化界面及相关参数
     */
    public void init(){

        Log.d(TAG, "init: ************ 初始化");
        if( popupWindow == null ) {
            contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_sdfile_list, null);
            tv_path = (TextView) contentView.findViewById(R.id.tv_path);
            sdfileList = (ListView) contentView.findViewById(R.id.sdfile_list);
            // 检查权限并获取数据
            getPermission();
            showSdFileList();

            width = DimenUtil.dip2px(mContext,428 );
            height = DimenUtil.dip2px(mContext,600);

            popupWindow = new PopupWindow( width, height);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLUE));

            popupWindow.setContentView(contentView);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setTouchable(true);
            popupWindow.setFocusable(true);
        } else {
            // 检查权限并获取数据
            getPermission();
            showSdFileList();
        }
        // 显示位置
        popupWindow.showAsDropDown(btnView,0,0);
        popupWindow.setHeight( height );
        popupWindow.setWidth( width );
//        popupWindow.showAtLocation( , Gravity.TOP | Gravity.LEFT ,0,0);
        Log.d(TAG, "init: ********************** 显示窗口");
        // weith 和 height 设置
    }

    private SdFileAdapter adapter;
    public void showSdFileList(){
        Log.d(TAG, "showSdFileList: **************** 列表加载数据");
        // listView 显示监听
        adapter = new SdFileAdapter(mContext, R.layout.list_sdfile_item , mSdFileBeans);
        sdfileList.setAdapter(adapter);
        sdfileList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pointItemFile( position );
            }
        });

    }

    private List<SdFileBean> mSdFileBeans = new ArrayList<>();
    private SdFileData mSdfileData = new SdFileData( mContext );
    private String rootPath,currentPath;

    public void getRootData(){
        // 获取根目录
        rootPath = mSdfileData.getSDRoot();
        tv_path.setText( "当前路径："+rootPath );
        getSdFileData( rootPath );
    }


    public void getSdFileData(String path){
        Log.d(TAG, "getSdFileData: ********** 获取数据");
         currentPath = path;
         mSdfileData.getFileDirectory( path );
        mSdFileBeans = mSdfileData.getList();
         //刷新数据
         if( adapter != null ){
             adapter.dataChange( mSdFileBeans );
         }
    }

    /**
     * 点击item
     * @param position
     */
    public void  pointItemFile(int position){

        SdFileBean fileBean = mSdFileBeans.get(position);
        File newFile = new File( fileBean.getSDfilePath() );

        // 返回根目录
        if(position == 0){
            getSdFileData(rootPath);
        }
        // 返回上一级目录,SDfileName 为“ 返回上一级 ”，故加入 recentPath 作当前路径
        else if(position == 1){
//                    String parentFile = newFile.getName();
            File parentFile = new File( currentPath );
            if(!parentFile.exists() || parentFile.length() == 0 || currentPath.equals(rootPath)) {
                System.out.println("上级目录不存在");
            }
            else{
                getSdFileData( parentFile.getParent());
            }
        }
        // 点击文件夹或TXT文件
        else{

            if(newFile.isDirectory()){
                getSdFileData( fileBean.getSDfilePath() );
            }
            else{
                // TXT文件，返回其完整路径
                Intent intent = new Intent();
                long fileLength = newFile.length();
                if(fileLength > 716800 ){
                    // 文件过大，不支持
                    mListener.onFilePath( fileBean.getSDfilePath(),false);
                }
                else{
                    // 接口回调
                    mListener.onFilePath( fileBean.getSDfilePath(),true );
                }

            }
        }

    }




    /**
     * 回调接口
     */
    public interface OnWindowListen{
        void onFilePath(String path,boolean canOpen);
    }
    private OnWindowListen mListener;
    public void setOnListener(OnWindowListen mListener){
        this.mListener = mListener;
    }

    /**
     *  检查获取权限
     * @return
     */
    public void getPermission(){

        String sdString = Environment.getExternalStorageState();
        if ( sdString.equals( Environment.MEDIA_MOUNTED ) ) {
            Log.d("SdFileWindow", "getPermission: ************** sdcard存储正常");
        } else {
            Toast.makeText(mContext,"no sdcard",Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= 23) {
            // 检查权限
            int readCheck = mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE );
            int writeCheck = mContext.checkSelfPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE );
            if( readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED ){
                // 已有权限
                getRootData();
            } else {
                // 没有权限 ，api > 23 还需要手动申请权限
                ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

    }
}
