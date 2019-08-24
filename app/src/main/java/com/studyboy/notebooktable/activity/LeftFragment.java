package com.studyboy.notebooktable.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.ListFragment;

import com.studyboy.notebooktable.R;
import com.studyboy.notebooktable.databaseAndListview.fileList.FileAdapter;
import com.studyboy.notebooktable.databaseAndListview.fileList.MyDataBaseHelper;
import com.studyboy.notebooktable.databaseAndListview.fileList.MyFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class LeftFragment extends ListFragment {

    private Callbacks mCallBacks;
    // 利用接口实现回调,
    public interface Callbacks{
        public void onItemSelected(String name);
    }

    /** 文件名、最近编辑日期、用于显示listview 的数组 */
    String fileName = null;
    String fileDatetime = null;
    private List<MyFile> fileList = new ArrayList<>();

    private MyDataBaseHelper dbHelper;
    ListView listView;
    FileAdapter adapter = null;
    View view;

    /** 用于通过接口的回调 ，onAttach(Activity) 已被弃用 */
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        // 如果Context 没有实现Callbacks接口，抛出异常
        if(!(context instanceof Callbacks)){
            throw new IllegalStateException("LeftFragment所在的Activity必须实现Callbacks接口");
        }
        // 把该 Context 当成Callbacks对象
        mCallBacks = (Callbacks) context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container,
                             Bundle saveInstanceState){
        view = inflater.inflate(R.layout.left_fragment,container,false);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // getListView 一定要在此方法中调用，不然会报错
//        listView = getListView() ;
        listViewShow();
    }

    public void listViewShow() {
        // 清空列表
        listView = getListView();
        fileList.clear();
        // 获取数据 加载到列表中
        getData();
        // 列表初始化
        initFileList();

        adapter = new FileAdapter(getActivity(), R.layout.list_item_file, fileList);

        setListAdapter(adapter);

        // 实现点击监听跳转
        // 实现长按监听删除

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {

                MyFile file = fileList.get(position);
                fileName = file.getName();
                fileDatetime = file.getDatetime();
//                Toast.makeText(getActivity(), "你在 长按111111" + fileName,
//                        Toast.LENGTH_SHORT).show();
                showDeleteDialog();
                return true;
            }
        });
    }

    // 当用户单击某列表项时激发该回调方法
    @Override
    public void onListItemClick(ListView parent,View view,int position,long id){
          MyFile file = fileList.get(position);
          fileName = file.getName();
          // 激发mCallbacks的onItemSelect方法
          mCallBacks.onItemSelected(fileName);
    }

    // 显示删除提示按钮
    public void showDeleteDialog() {
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());

        deleteDialog.setTitle("郎郎记事本");
        deleteDialog.setMessage("     您确定要删除该文件吗");
        deleteDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 数据库删除
                        deleteFileInDataBase();
                        // 本地文件删除
                        deleteFile();
                        listViewShow();
                    }
                }).show();
    }

    /** 数据库中删除数据 ,根据长按获取的 fileName */
    public void deleteFileInDataBase(){

        dbHelper = new MyDataBaseHelper(getActivity(),"file.db",null,1);
        // 若没有 file.db 数据库，则创建
        SQLiteDatabase db = null;
        try{
            db = dbHelper.getWritableDatabase();
            if(db != null) {
                db.delete("table_file", "name = ? ", new String[]{fileName});
                Toast.makeText(getActivity(), "删除成功 ", Toast.LENGTH_SHORT).show();
            }
            db.close();
            db  = null;
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                if(db != null){
                    db.close();
                    db = null;
                }
            } catch ( Exception e){
                e.printStackTrace();
            }
        }
    }

    /** 删除存储的文件 */
    public void deleteFile( ){
        try{
            File file = new File("/data/data/com.studyboy.notebooktable/files", fileName);
            // 找到文件并删除
            if( file.delete()){
                Toast.makeText(getActivity(),"删除成功",Toast.LENGTH_SHORT).show();
            };
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**  打开数据库并获取文档记录数据 */
     public void getData(){

         MainActivity mainActivity = (MainActivity) getActivity();
         dbHelper = new MyDataBaseHelper(mainActivity, "file.db", null, 1);
         // 若没有 file.db 数据库，则创建
         Cursor cursor = null;
         SQLiteDatabase db = null;
         try {
             db = mainActivity.openOrCreateDatabase("file.db",MODE_PRIVATE,null);
             if (db != null) {
                 cursor = db.query("table_file",null,null,null,
                         null,null,"datetime desc");  //   按datatime 降序排序
                 if (cursor != null){
                     if(cursor.moveToFirst()) {
                         do {
                             // 查询得到的数据加载到 filelist 中，用于listView 显示
                             fileName = cursor.getString(cursor.getColumnIndex("name"));
                             fileDatetime = cursor.getString(cursor.getColumnIndex("datetime"));
                             MyFile file = new MyFile(fileName, fileDatetime);
                             fileList.add(file);
                         } while (cursor.moveToNext());
                     }
                     cursor.close();
                     cursor = null;
                 }
                 db.close();
                 db = null;
             }

         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             try {
                 if (cursor != null){
                     cursor.close();
                     cursor = null;
                 }
             } catch (Exception e) {
                 e.printStackTrace();
             }
             try {
                 if (db != null){
                     db.close();
                     db = null;
                 }
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
     }

    /**
     * 完全清空或异常显示
     */
    public void initFileList(){
        if(fileList == null){
            MyFile file = new MyFile("还没有笔记哦","2019-99-99 99:99:99");
            fileList.add(file);
        }
    }

}
