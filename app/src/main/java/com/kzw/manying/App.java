package com.kzw.manying;

import android.app.Application;

import com.kzw.manying.Util.Constant;
import com.kzw.manying.Util.OkhClientUtil;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * author: kang4
 * Date: 2019/9/24
 * Description:
 */
public class App extends Application {
    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        OkhClientUtil.getInstance().init();
        initDataBase();
    }

    public static Application getApplication() {
        return instance;
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
