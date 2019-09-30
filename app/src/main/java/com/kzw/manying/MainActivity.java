package com.kzw.manying;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.edit_url)
    EditText editUrl;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    SearchListAdapter adapter;
    public List<SearchItem> items = new ArrayList<>();
    ProgressDialog dialog;
    @InjectView(R.id.slide_menu)
    ImageView slideMenu;
    @InjectView(R.id.collectList)
    RecyclerView collectList;
    @InjectView(R.id.drawer)
    DrawerLayout drawer;
    @InjectView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    private Realm realm;
    public List<SearchItem> collectlist = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        inivew();
    }

    private void inivew() {
        dialog = new ProgressDialog(MainActivity.this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new SearchListAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this::goIntent);

        editUrl.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                dialog.show();
                items.clear();
                search();
                return true;
            }
            return false;
        });

        realm = Realm.getDefaultInstance();
        getCollectData();
        collectList.setLayoutManager(new LinearLayoutManager(this));
        SearchListAdapter collectAdapter = new SearchListAdapter();
        collectList.setAdapter(collectAdapter);
        collectAdapter.setList(collectlist);
        collectAdapter.setOnItemClickListener(this::goIntent);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(() -> {
            collectlist.clear();
            getCollectData();
            collectAdapter.setList(collectlist);
            refreshLayout.setRefreshing(false);
        });
    }

    private void getCollectData() {
        List<VideoRealm> list = realm.where(VideoRealm.class).findAll();
        if (list != null && list.size() > 0) {
            for (VideoRealm video : list) {
                SearchItem item = new SearchItem();
                item.setType(video.getType());
                item.setName(video.getName());
                item.setUrl(video.getUrl());
                collectlist.add(item);
            }
        }
    }

    private void goIntent(SearchItem item) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, VideoDetailActivity.class);
        intent.putExtra("item", item);
        startActivity(intent);
    }

    private void search() {
        getData();
        getZuidaData();
        getYongjiuData();
        getKuhaData();
    }

    private void getData() {
        Map<String, Object> params = new HashMap<>();
        params.put("wd", editUrl.getText().toString());
        params.put("submit", "search");
        OkhClientUtil.getInstance().postHtml(Constant.SEARCH, params, result -> {
            List<SearchItem> searchlist = new ArrayList<>();
            Document document = Jsoup.parse(result);
            Elements elements = document.getElementsByClass("xing_vb");
            for (Element postItem : elements) {
                //像jquery选择器一样，获取文章标题元素
                Elements list = postItem.getElementsByClass("xing_vb4");
                for (Element item : list) {
                    Element link = item.select("a").first();
                    String relHref = link.attr("href");
                    SearchItem search = new SearchItem(item.text(), Constant.BASEURL + relHref);
                    search.setType(0);
                    searchlist.add(search);
                }
            }
            items.addAll(searchlist);
            runOnUiThread(() -> {
                dialog.dismiss();
                adapter.setList(items);
            });
        });
    }

    private void getZuidaData() {
        Map<String, Object> params = new HashMap<>();
        params.put("wd", editUrl.getText().toString());
        params.put("submit", "search");
        OkhClientUtil.getInstance().postHtml(Constant.ZUIDA_SEARCH, params, result -> {
            List<SearchItem> searchlist = new ArrayList<>();
            Document document = Jsoup.parse(result);
            Elements elements = document.getElementsByClass("xing_vb");
            for (Element postItem : elements) {
                //像jquery选择器一样，获取文章标题元素
                Elements list = postItem.getElementsByClass("xing_vb4");
                for (Element item : list) {
                    Element link = item.select("a").first();
                    String relHref = link.attr("href");
                    SearchItem search = new SearchItem(item.text(), Constant.ZUIDA_BASEURL + relHref);
                    search.setType(1);
                    searchlist.add(search);
                }
            }
            items.addAll(searchlist);
            runOnUiThread(() -> {
                dialog.dismiss();
                adapter.setList(items);
            });
        });
    }

    private void getYongjiuData() {
        Map<String, Object> params = new HashMap<>();
        params.put("wd", editUrl.getText().toString());
        OkhClientUtil.getInstance().postHtml(Constant.YONGJIU_SEARCH, params, result -> {
            List<SearchItem> searchlist = new ArrayList<>();
            Document document = Jsoup.parse(result);
            Elements elements = document.getElementsByClass("tbody");

            for (Element postItem : elements) {
                //像jquery选择器一样，获取文章标题元素
                Elements list = postItem.getElementsByClass("DianDian");
                for (Element item : list) {
                    Element link = item.select("a").first();
                    String relHref = link.attr("href");
                    SearchItem search = new SearchItem(link.text(), Constant.YONGJIU_BASEURL + relHref);
                    search.setType(2);
                    searchlist.add(search);
                    System.out.println(link.text());
                }
            }
            items.addAll(searchlist);
            runOnUiThread(() -> {
                dialog.dismiss();
                adapter.setList(items);
            });
        });
    }

    public void getKuhaData() {
        Map<String, Object> params = new HashMap<>();
        params.put("wd", editUrl.getText().toString());
        params.put("submit", "search");
        OkhClientUtil.getInstance().postHtml(Constant.KUHA_SEARCH, params, result -> {
            List<SearchItem> searchlist = new ArrayList<>();
            Document document = Jsoup.parse(result);
            Elements elements = document.getElementsByClass("xing_vb");
            for (Element postItem : elements) {
                //像jquery选择器一样，获取文章标题元素
                Elements list = postItem.getElementsByClass("xing_vb4");
                for (Element item : list) {
                    Element link = item.select("a").first();
                    String relHref = link.attr("href");
                    SearchItem search = new SearchItem(item.text(), Constant.KUHA_BASEURL + relHref);
                    search.setType(3);
                    searchlist.add(search);
                }
            }
            items.addAll(searchlist);
            runOnUiThread(() -> {
                dialog.dismiss();
                adapter.setList(items);
            });
        });
    }


    @OnClick(R.id.slide_menu)
    public void onViewClicked() {
        drawer.openDrawer(refreshLayout);
    }
}
