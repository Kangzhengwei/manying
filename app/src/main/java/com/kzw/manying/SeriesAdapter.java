package com.kzw.manying;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * author: kang4
 * Date: 2019/9/23
 * Description:
 */
public class SeriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UrlPlayBean.Url> list;
    private OnItemClickListener clickListener;

    public SeriesAdapter(List<UrlPlayBean.Url> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView series;
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            series = itemView.findViewById(R.id.series);
            view=itemView;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.series_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        UrlPlayBean.Url url = list.get(i);
        ((ViewHolder) viewHolder).series.setText(url.getVideoSeries());
        ((ViewHolder)viewHolder).view.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.itemClick(url);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void itemClick(UrlPlayBean.Url item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }
}
