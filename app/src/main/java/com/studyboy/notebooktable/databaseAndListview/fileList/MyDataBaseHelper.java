package com.studyboy.notebooktable.databaseAndListview.fileList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDataBaseHelper extends SQLiteOpenHelper {

    private Context mContext;
    // �������
    public static final String CREATE_FILE = "create table table_file(name Text,datetime text)";

    public MyDataBaseHelper (Context context, String name , SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase  db){
        // �������ݿ�ͬʱ����
        db.execSQL(CREATE_FILE);
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldversion,int newVersion){
    // �������ݿ�汾����

    }
}
