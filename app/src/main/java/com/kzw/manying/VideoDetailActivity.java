package com.kzw.manying;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class VideoDetailActivity extends AppCompatActivity {

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.player)
    VideoPlayer player;
    @InjectView(R.id.subscribe)
    ImageView subscribe;
    private List<UrlPlayBean> urlPlaylist = new ArrayList<>();
    public VideoSeriesAdapter adapter;
    private Realm realm;
    private SearchItem item;
    private boolean isSubsrribe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        ButterKnife.inject(this);
        item = (SearchItem) getIntent().getSerializableExtra("item");
        initview();
        intdata();
    }

    private void intdata() {
        List<SearchItem.VideoUrl> videoUrlList = item.getList();
        if (videoUrlList != null && videoUrlList.size() > 0) {
            for (SearchItem.VideoUrl videoUrl : videoUrlList) {
                if (videoUrl.getType() == 0 || videoUrl.getType() == 1) {
                    getdata(videoUrl.getUrl());
                } else if (videoUrl.getType() == 2) {
                    getYongjiuData(videoUrl.getUrl());
                } else if (videoUrl.getType() == 3) {
                    getKuhadata(videoUrl.getUrl());
                }
            }
        } else {
            if (item.getType() == 0 || item.getType() == 1) {
                getdata(item.getUrl());
            } else if (item.getType() == 2) {
                getYongjiuData(item.getUrl());
            } else if (item.getType() == 3) {
                getKuhadata(item.getUrl());
            }
        }
    }


    public void initview() {
        //是否可以滑动调整
        player.setIsTouchWiget(true);
        player.getFullscreenButton().setOnClickListener(v -> {
                    player.startWindowFullscreen(VideoDetailActivity.this, false, true);
                }
        );
        player.getBackButton().setOnClickListener(v -> onBackPressed());
        player.setReleaseWhenLossAudio(false);
        player.setAutoFullWithSize(true);
        player.setNeedLockFull(true);
        player.setNeedShowWifiTip(false);
        player.setDismissControlTime(5000);
        player.setEnlargeImageRes(R.mipmap.icon_fullscreen);
        player.setShrinkImageRes(R.mipmap.icon_fullscreen);
        player.setSeekRatio(2f);
        //全屏动画
        player.setShowFullAnimation(true);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new VideoSeriesAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(item -> {
            player.setUp(item.videoUrl, true, item.getVideoSeries());
            player.startPlayLogic();
        });

        realm = Realm.getDefaultInstance();

        RealmResults<VideoRealm> list = realm.where(VideoRealm.class).findAll();
        if (list != null && list.size() > 0) {
            for (VideoRealm video : list) {
                if (video.getName().equals(item.getName())) {
                    isSubsrribe = true;
                    subscribe.setImageResource(R.mipmap.icon_has_subscribe);
                    break;
                }
            }
        }
    }

    public void getdata(String url) {
        OkhClientUtil.getInstance().getHtml(url, result -> {
            Document document = Jsoup.parse(result);
            Elements elements = document.getElementsByClass("vodplayinfo");
            for (Element element : elements) {
                Elements ul = element.getElementsByTag("ul");
                if (ul != null && !TextUtils.isEmpty(ul.toString())) {
                    int i = 0;
                    for (Element ulitem : ul) {
                        Elements suf = element.getElementsByClass("suf");
                        Elements liItem = ulitem.getElementsByTag("li");
                        UrlPlayBean bean = new UrlPlayBean();
                        bean.setUrlType(suf.get(i).text());
                        List<UrlPlayBean.Url> urlist = new ArrayList();
                        for (Element li : liItem) {
                            UrlPlayBean.Url url1 = new UrlPlayBean.Url();
                            url1.setVideoSeries(StringUtil.subString(li.text(), 0));
                            url1.setVideoUrl(StringUtil.subString(li.text(), 1));
                            urlist.add(url1);
                            System.out.println(suf.get(i).text() + "集数：" + StringUtil.subString(li.text(), 0) + "链接:" + StringUtil.subString(li.text(), 1));
                        }
                        bean.setList(urlist);
                        i++;
                        urlPlaylist.add(bean);
                    }
                }
            }

            runOnUiThread(() -> adapter.setPlayBeanList(urlPlaylist));
        });

    }

    public void getYongjiuData(String url) {
        OkhClientUtil.getInstance().getHtml(url, result -> {
            Document document = Jsoup.parse(result);
            Elements elements = document.getElementsByClass("movievod");
            for (Element element : elements) {
                Elements ul = element.getElementsByTag("ul");
                if (ul != null && !TextUtils.isEmpty(ul.toString())) {
                    List<String> titlelist = new ArrayList<>();
                    List<String> urllist = new ArrayList<>();
                    for (Element ulitem : ul) {
                        Elements liItem = ulitem.getElementsByTag("li");
                        for (Element li : liItem) {
                            Element link = li.select("a").first();
                            Element input = li.select("input").first();
                            if (link == null && input == null) {
                                titlelist.add(li.text());
                            } else if (link != null) {
                                urllist.add(li.text());
                            }
                        }
                        int a = urllist.size() / titlelist.size();
                        int b = 0;
                        for (String str : titlelist) {
                            UrlPlayBean urlPlayBean = new UrlPlayBean();
                            List<UrlPlayBean.Url> beanlist = new ArrayList();
                            List sub = urllist.subList(a * b, a * b + a);
                            List<String> array = new ArrayList(sub);
                            System.out.println(array.toString());
                            b++;
                            for (String string : array) {
                                UrlPlayBean.Url bean = new UrlPlayBean.Url();
                                bean.setVideoSeries(StringUtil.subString(string, 0));
                                bean.setVideoUrl(StringUtil.subString(string, 1));
                                beanlist.add(bean);
                            }
                            urlPlayBean.setUrlType(str);
                            urlPlayBean.setList(beanlist);
                            urlPlaylist.add(urlPlayBean);
                        }
                    }
                }
            }

            runOnUiThread(() -> adapter.setPlayBeanList(urlPlaylist));
        });
    }

    public void getKuhadata(String url) {
        OkhClientUtil.getInstance().getHtml(url, result -> {
            Document document = Jsoup.parse(result);
            Elements elements = document.getElementsByClass("vodplayinfo");
            for (Element element : elements) {
                Elements ul = element.getElementsByTag("ul");
                if (ul != null && !TextUtils.isEmpty(ul.toString())) {
                    int i = 0;
                    for (Element ulitem : ul) {
                        Elements h3 = element.getElementsByTag("h3");
                        Elements liItem = ulitem.getElementsByTag("li");
                        UrlPlayBean bean = new UrlPlayBean();
                        bean.setUrlType(h3.get(i).text());
                        List<UrlPlayBean.Url> urlist = new ArrayList();
                        for (Element li : liItem) {
                            UrlPlayBean.Url url1 = new UrlPlayBean.Url();
                            url1.setVideoSeries(StringUtil.subString(li.text(), 0));
                            url1.setVideoUrl(StringUtil.subString(li.text(), 1));
                            urlist.add(url1);
                            System.out.println(h3.get(i).text() + "集数：" + StringUtil.subString(li.text(), 0) + "链接:" + StringUtil.subString(li.text(), 1));
                        }
                        bean.setList(urlist);
                        i++;
                        urlPlaylist.add(bean);
                    }
                }
            }

            runOnUiThread(() -> adapter.setPlayBeanList(urlPlaylist));
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        player.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        GSYVideoManager.instance().clearAllDefaultCache(this);
    }

    @Override
    public void onBackPressed() {
        //释放所有
        if (player.isIfCurrentIsFullscreen()) {
            player.onBackFullscreen();
            return;
        }
        player.setVideoAllCallBack(null);
        super.onBackPressed();
    }

    @OnClick(R.id.subscribe)
    public void onViewClicked() {
        if (item == null) {
            return;
        }
        if (isSubsrribe) {
            RealmResults<VideoRealm> list = realm.where(VideoRealm.class).findAll();
            for (VideoRealm video : list) {
                if (video.getName().equals(item.getName())) {
                    subscribe.setImageResource(R.mipmap.icon_item_describe);
                    realm.executeTransaction(realm -> video.deleteFromRealm());
                    break;
                }
            }
            isSubsrribe = false;
            return;
        }
        realm.executeTransaction(realm -> {
            RealmResults<VideoRealm> list = realm.where(VideoRealm.class).findAll();
            boolean isHas = false;
            if (list != null && list.size() > 0) {
                for (VideoRealm video : list) {
                    if (video.getName().equals(item.getName())) {
                        isHas = true;
                        break;
                    }
                }
            }
            if (!isHas) {
                VideoRealm videoRealm = realm.createObject(VideoRealm.class);
                videoRealm.setName(item.getName());
                videoRealm.setType(item.getType());
                videoRealm.setUrl(item.getUrl());
                runOnUiThread(() -> {
                    subscribe.setImageResource(R.mipmap.icon_has_subscribe);
                });
            }
        });
    }
}
