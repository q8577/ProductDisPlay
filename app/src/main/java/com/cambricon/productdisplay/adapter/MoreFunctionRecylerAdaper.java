package com.cambricon.productdisplay.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.task.OnItemClickListener;

import java.util.List;

/**
 * Created by dell on 18-3-15.
 */

public class MoreFunctionRecylerAdaper extends RecyclerView.Adapter<MyViewHolder> implements OnItemClickListener {
    private LayoutInflater inflater;
    private Context mContext;
    private List<String> mDatas;
    private List<Integer> mdraw;
    public MoreFunctionRecylerAdaper(Context context, List<String> datas,List<Integer> mdraw) {
        this.mContext = context;
        this.mDatas = datas;
        this.mdraw=mdraw;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.more_functions_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv.setText(mDatas.get(position));
        holder.test.setText("hello");
        holder.imageView.setImageResource(mdraw.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onItemClick(View view) {

    }

    @Override
    public void onItemLongClick(View view) {

    }

    //新增item
    public void addData(int pos) {
        mDatas.add("新增");
        notifyItemInserted(pos);
    }

    //移除item
    public void deleateData(int pos) {
        mDatas.remove(pos);
        notifyItemRemoved(pos);
    }
}
class MyViewHolder extends RecyclerView.ViewHolder {

    TextView tv;
    TextView test;
    ImageView imageView;

    public MyViewHolder(View itemView) {
        super(itemView);
        tv = itemView.findViewById(R.id.recycle_tv);
        tv.setTextSize(20);
        test = itemView.findViewById(R.id.recycle_test);
        test.setTextSize(18);
        imageView=itemView.findViewById(R.id.header_img);

    }
}