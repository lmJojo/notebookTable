package com.studyboy.notebooktable.databaseAndListview.fileList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.studyboy.notebooktable.R;
import com.studyboy.notebooktable.databaseAndListview.fileList.MyFile;

import java.util.List;

/**
 *  自定义适配器
 */
public class FileAdapter extends ArrayAdapter<MyFile> {

    private int resourceId;
    public FileAdapter(Context context, int textViewResourceId, List<MyFile> objects) {

        //  上下文，子项布局id， 数据
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    /**
     *  利用 convertView 和建立内部类 viewHolder 优化listView性能
      * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        // 获取当前file 实例
        MyFile file=getItem(position);
        View view;
        ViewHolder viewHolder;
        // convertView ,将之前加载的布局进行缓存，以便重用
        if( convertView == null){
            view= LayoutInflater.from(getContext()).inflate(resourceId ,parent,
                    false );
            viewHolder = new ViewHolder();

            viewHolder.tv_Name=(TextView) view.findViewById(R.id.name);
            viewHolder.tv_DateTime=(TextView)view.findViewById(R.id.datetime);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        // 获取Name 和 Datatime内容
        viewHolder.tv_Name.setText(file.getName());
        viewHolder.tv_DateTime.setText(file.getDatetime());
        return view;
    }
    class ViewHolder{
        // 新增内部类以免每次都获取 控件实例
        TextView tv_Name;
        TextView tv_DateTime;
    }

}
