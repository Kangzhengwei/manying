package com.kzw.manying;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * author: kang4
 * Date: 2019/9/24
 * Description:
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkhClientUtil.getInstance().loadUrl(Constant.BASEURL);
        OkhClientUtil.getInstance().loadUrl(Constant.ZUIDA_BASEURL);
        OkhClientUtil.getInstance().loadUrl(Constant.YONGJIU_BASEURL);
        OkhClientUtil.getInstance().loadUrl(Constant.KUHA_BASEURL);
        initDataBase();
    }
    /**
     * 初始化数据库
     */
    private void initDataBase() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("myrealm.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
