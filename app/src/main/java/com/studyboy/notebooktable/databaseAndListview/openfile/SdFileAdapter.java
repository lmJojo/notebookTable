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

public class SdFileAdapter extends ArrayAdapter<SdFileBean> {

    private int resourceId;
    private  List<SdFileBean> mSdFileBeanList;

    public SdFileAdapter(Context context, int textViewResourceId, List<SdFileBean> objects) {

        //  上下文，子项布局id， 数据
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        mSdFileBeanList = objects;
    }


    public void dataChange( List<SdFileBean> newDatas ){
        mSdFileBeanList = newDatas;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        ViewHolder viewHolder;
        View view;
        // 获取当前file 实例
        SdFileBean mSdFileBean = getItem(position);
        if(convertView == null ) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent,
                    false);
            viewHolder.fileIcon = (ImageView) view.findViewById(R.id.file_image);
            viewHolder.fileName = (TextView) view.findViewById(R.id.tv_fileName);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        // 获取SDfileName 和 ImageID 内容
        File file = new File(mSdFileBean.getSDfilePath());
        viewHolder.fileName.setText( file.getName() );
        viewHolder.fileIcon.setImageResource( mSdFileBean.getImageID());
        return view;
    }

    public class ViewHolder{
        TextView  fileName;
        ImageView fileIcon;
    }

}
