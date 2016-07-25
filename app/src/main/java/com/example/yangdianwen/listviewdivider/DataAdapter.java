package com.example.yangdianwen.listviewdivider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangdianwen on 16-7-25.
 */
public class DataAdapter extends BaseAdapter{
    private List<String> datas;


    public DataAdapter() {
       datas=new ArrayList<>();
        for (int i = 0; i <20 ; i++) {
            datas.add(""+i);
        }
    }
    public void addDatas(String datas){
        this.datas.add(""+datas);
        notifyDataSetChanged();
    }
    public void clearDatas(){
        datas.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem, null);
            viewHolder=new ViewHolder();
        viewHolder.textView= (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
        }else {
        viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(""+datas.get(position));
        return convertView;
    }
    private class ViewHolder{
        private TextView textView;
    }
}
