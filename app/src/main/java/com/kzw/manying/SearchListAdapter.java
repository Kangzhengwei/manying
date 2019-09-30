package com.kzw.manying;

import androidx.annotation.NonNull;
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
public class SearchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SearchItem> list = new ArrayList<>();
    private OnItemClickListener clickListener;

    public SearchListAdapter() {

    }

    public void setList(List<SearchItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        View item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.video_name);
            item = itemView;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final SearchItem item = list.get(i);
        ((ViewHolder) viewHolder).textView.setText(item.getName());
        ((ViewHolder) viewHolder).item.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.itemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void itemClick(SearchItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }
}
