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
 *  �Զ���������
 */
public class FileAdapter extends ArrayAdapter<MyFile> {

    private int resourceId;
    public FileAdapter(Context context, int textViewResourceId, List<MyFile> objects) {

        //  �����ģ������id�� ����
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    /**
     *  ���� convertView �ͽ����ڲ��� viewHolder �Ż�listView����
      * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        // ��ȡ��ǰfile ʵ��
        MyFile file=getItem(position);
        View view;
        ViewHolder viewHolder;
        // convertView ,��֮ǰ���صĲ��ֽ��л��棬�Ա�����
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
        // ��ȡName �� Datatime����
        viewHolder.tv_Name.setText(file.getName());
        viewHolder.tv_DateTime.setText(file.getDatetime());
        return view;
    }
    class ViewHolder{
        // �����ڲ�������ÿ�ζ���ȡ �ؼ�ʵ��
        TextView tv_Name;
        TextView tv_DateTime;
    }

}
