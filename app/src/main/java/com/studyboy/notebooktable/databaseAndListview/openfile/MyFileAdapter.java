package com.studyboy.notebooktable.databaseAndListview.openfile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.studyboy.notebooktable.R;

import java.io.File;
import java.util.List;

public class MyFileAdapter extends ArrayAdapter<FileShow> {

    private int resourceId;

    public MyFileAdapter(Context context, int textViewResourceId, List<FileShow> objects) {

        //  上下文，子项布局id， 数据
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        // 获取当前file 实例
        FileShow fileShow = getItem(position);
        //
        View view= LayoutInflater.from(getContext()).inflate(resourceId ,parent,
                false );
        ImageView imageView = (ImageView) view.findViewById(R.id.file_image);
        TextView tv_fileName = (TextView)view.findViewById(R.id.tv_fileName);

        // 获取SDfileName 和 ImageID 内容
        File file = new File(fileShow .SDfilePath());
        tv_fileName.setText(file.getName());
        imageView.setImageResource( fileShow.getImageID());
        return view;
    }

}
