package com.kzw.manying;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * author: kang4
 * Date: 2019/9/23
 * Description:
 */
public class VideoSeriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Context mContext;

    private List<UrlPlayBean> playBeanList = new ArrayList<>();
    private OnItemClickListener clickListener;


    public VideoSeriesAdapter(Context context) {
        this.mContext = context;
    }

    public void setPlayBeanList(List<UrlPlayBean> list) {
        playBeanList = list;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView type;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_series_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        UrlPlayBean bean = playBeanList.get(i);
        ((ViewHolder) viewHolder).type.setText(bean.getUrlType());
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        ((ViewHolder) viewHolder).recyclerView.setLayoutManager(manager);
        SeriesAdapter adapter = new SeriesAdapter(bean.getList());
        ((ViewHolder) viewHolder).recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(item -> {
            if (clickListener != null) {
                clickListener.itemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playBeanList.size();
    }

    public interface OnItemClickListener {
        void itemClick(UrlPlayBean.Url item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }
}
