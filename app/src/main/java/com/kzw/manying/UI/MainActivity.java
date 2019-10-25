package com.kzw.manying.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.kzw.manying.Bean.SearchItem;
import com.kzw.manying.Bean.VideoRealm;
import com.kzw.manying.R;
import com.kzw.manying.UI.Adapter.SearchListAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {


    @InjectView(R.id.subRecyclerView)
    RecyclerView subRecyclerView;

    @InjectView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    private Realm realm;
    public List<SearchItem> subList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        realm = Realm.getDefaultInstance();
        iniView();
    }

    private void iniView() {
        getSupportActionBar().setTitle(R.string.title);
        getSubList();
        subRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        SearchListAdapter subAdapter = new SearchListAdapter();
        subRecyclerView.setAdapter(subAdapter);
        subAdapter.setList(subList);
        subAdapter.setOnItemClickListener(this::goIntent);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(() -> {
            subList.clear();
            getSubList();
            subAdapter.setList(subList);
            refreshLayout.setRefreshing(false);
        });
    }

    private void getSubList() {
        List<VideoRealm> list = realm.where(VideoRealm.class).findAll();
        if (list != null && list.size() > 0) {
            for (VideoRealm video : list) {
                SearchItem item = new SearchItem();
                item.setType(video.getType());
                item.setName(video.getName());
                item.setUrl(video.getUrl());
                item.setSiteName(video.getSiteName());
                subList.add(item);
            }
        }
    }

    private void goIntent(SearchItem item) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, VideoPlayActivity.class);
        intent.putExtra("item", item);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
