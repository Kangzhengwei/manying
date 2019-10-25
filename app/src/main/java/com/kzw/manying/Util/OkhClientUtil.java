package com.kzw.manying.Util;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author: kang4
 * Date: 2019/9/23
 * Description:
 */
public class OkhClientUtil {
    private static volatile OkhClientUtil sInstance;
    private OkHttpClient mOkHttpClient;

    private OkhClientUtil() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public static OkhClientUtil getInstance() {
        if (sInstance == null) {
            synchronized (OkhClientUtil.class) {
                if (sInstance == null) {
                    sInstance = new OkhClientUtil();
                }
            }
        }
        return sInstance;
    }

    public void init() {
        List<String> list = new ArrayList<>();
        list.add(Constant.BASEURL);
        list.add(Constant.ZUIDA_BASEURL);
        list.add(Constant.YONGJIU_BASEURL);
        list.add(Constant.KUHA_BASEURL);
        for (String url : list) {
            loadUrl(url);
        }
    }

    public void loadUrl(String url) {
        Request.Builder builder = new Request.Builder()
                .addHeader("connection", "Keep-Alive")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36");

        Request request = builder
                .url(url)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            }
        });
    }

    public void getHtml(String url, final getResult result) {
        Request.Builder builder = new Request.Builder()
                .addHeader("connection", "Keep-Alive")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36");

        Request request = builder
                .url(url)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (result != null) {
                    result.result(response.body().string());
                }
            }
        });

    }

    public void postHtml(String url, Map<String, Object> params, final getResult result) {
        FormBody.Builder bodybuilder = new FormBody.Builder();
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value != null) {
                bodybuilder.add(key, value.toString());
            }
        }
        FormBody body = bodybuilder.build();
        Request.Builder builder = new Request.Builder().url(url).post(body)
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36")
                .addHeader("connection", "Keep-Alive");

        final Request request = builder.build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (result != null) {
                    result.result(response.body().string());
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
        });
    }

    public interface getResult {
        void result(String result);
    }
}
