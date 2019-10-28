package com.kzw.manying.UI;

import android.os.Bundle;
import android.widget.ImageView;

import com.kzw.manying.UI.Adapter.VideoSeriesAdapter;
import com.kzw.manying.UI.Presenter.ApiPresenter;
import com.kzw.manying.Bean.SearchItem;
import com.kzw.manying.Bean.UrlPlayBean;
import com.kzw.manying.Bean.VideoRealm;
import com.kzw.manying.Interface.VideoSeriesInterface;
import com.kzw.manying.R;
import com.kzw.manying.widget.VideoPlayer;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class VideoPlayActivity extends AppCompatActivity implements VideoSeriesInterface {

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.player)
    VideoPlayer player;
    @InjectView(R.id.subscribe)
    ImageView subscribe;
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
        ApiPresenter.getInstance().getSeries(item,this);
    }

    public void initview() {
        //是否可以滑动调整
        player.setIsTouchWiget(true);
        player.getFullscreenButton().setOnClickListener(v -> player.startWindowFullscreen(VideoPlayActivity.this, false, true)
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
                videoRealm.setSiteName(item.getSiteName());
                runOnUiThread(() -> {
                    subscribe.setImageResource(R.mipmap.icon_has_subscribe);
                });
            }
        });
    }

    @Override
    public void returnPlayList(List<UrlPlayBean> list) {
        runOnUiThread(() -> adapter.setPlayBeanList(list));
    }
}
