package com.kzw.manying.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.kzw.manying.Bean.SearchItem;
import com.kzw.manying.Interface.VideoSearchInterface;
import com.kzw.manying.R;
import com.kzw.manying.UI.Adapter.SearchListAdapter;
import com.kzw.manying.UI.Presenter.ApiPresenter;
import com.kzw.manying.widget.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchActivity extends AppCompatActivity  implements VideoSearchInterface {

    @InjectView(R.id.editKey)
    EditText editKey;
    @InjectView(R.id.searchRecyclerView)
    RecyclerView searchRecyclerView;
    ProgressDialog dialog;
    List<SearchItem> totalItem = new ArrayList<>();
    private SearchListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);
        initView();
    }

    private void initView() {
        ApiPresenter.getInstance().setSearchListener(this);
        dialog = new ProgressDialog(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        searchRecyclerView.setLayoutManager(manager);
        adapter = new SearchListAdapter();
        searchRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this::goIntent);
        editKey.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                dialog.show();
                totalItem.clear();
                ApiPresenter.getInstance().search(v.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void goIntent(SearchItem item) {
        Intent intent = new Intent();
        intent.setClass(this, VideoPlayActivity.class);
        intent.putExtra("item", item);
        startActivity(intent);
    }

    @Override
    public void returnSearchList(List<SearchItem> items) {
        totalItem.addAll(items);
        runOnUiThread(() -> {
            dialog.dismiss();
            adapter.setList(totalItem);
        });
    }
}
