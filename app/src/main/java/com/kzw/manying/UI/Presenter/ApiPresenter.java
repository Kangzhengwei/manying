package com.kzw.manying.UI.Presenter;

import android.text.TextUtils;

import com.kzw.manying.Bean.SearchItem;
import com.kzw.manying.Bean.UrlPlayBean;
import com.kzw.manying.Interface.VideoSearchInterface;
import com.kzw.manying.Interface.VideoSeriesInterface;
import com.kzw.manying.Util.Constant;
import com.kzw.manying.Util.OkhClientUtil;
import com.kzw.manying.Util.StringUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: kang4
 * Date: 2019/10/25
 * Description:
 */
public class ApiPresenter {

    private VideoSeriesInterface callBack;
    private static volatile ApiPresenter sInstance;
    private VideoSearchInterface searchlistener;

    public static ApiPresenter getInstance() {
        if (sInstance == null) {
            synchronized (ApiPresenter.class) {
                if (sInstance == null) {
                    sInstance = new ApiPresenter();
                }
            }
        }
        return sInstance;
    }

    public void getSeries(SearchItem item, VideoSeriesInterface listener) {
        setCallBackListener(listener);
        getList(item);
    }

    public void setSearchListener(VideoSearchInterface listener) {
        this.searchlistener = listener;
    }

    public void search(String key) {
        searchKYData(key);
        searchZDData(key);
        searchYJData(key);
        searchKHData(key);
    }


    private void setCallBackListener(VideoSeriesInterface listener) {
        this.callBack = listener;
    }

    private void getList(SearchItem item) {
        List<SearchItem.VideoUrl> videoUrlList = item.getList();
        if (videoUrlList != null && videoUrlList.size() > 0) {
            for (SearchItem.VideoUrl videoUrl : videoUrlList) {
                if (videoUrl.getType() == 0 || videoUrl.getType() == 1) {
                    getCommon(videoUrl.getUrl());
                } else if (videoUrl.getType() == 2) {
                    getYongjiuData(videoUrl.getUrl());
                } else if (videoUrl.getType() == 3) {
                    getKuhadata(videoUrl.getUrl());
                }
            }
        } else {
            if (item.getType() == 0 || item.getType() == 1) {
                getCommon(item.getUrl());
            } else if (item.getType() == 2) {
                getYongjiuData(item.getUrl());
            } else if (item.getType() == 3) {
                getKuhadata(item.getUrl());
            }
        }
    }

    private void getCommon(String url) {
        OkhClientUtil.getInstance().getHtml(url, result -> {
            Document document = Jsoup.parse(result);
            Elements elements = document.getElementsByClass("vodplayinfo");
            List<UrlPlayBean> urlPlaylist = new ArrayList<>();
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
            if (callBack != null) {
                callBack.returnPlayList(urlPlaylist);
            }
        });
    }

    private void getYongjiuData(String url) {
        OkhClientUtil.getInstance().getHtml(url, result -> {
            Document document = Jsoup.parse(result);
            Elements elements = document.getElementsByClass("movievod");
            List<UrlPlayBean> urlPlaylist = new ArrayList<>();
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
            if (callBack != null) {
                callBack.returnPlayList(urlPlaylist);
            }
        });
    }

    private void getKuhadata(String url) {
        OkhClientUtil.getInstance().getHtml(url, result -> {
            Document document = Jsoup.parse(result);
            Elements elements = document.getElementsByClass("vodplayinfo");
            List<UrlPlayBean> urlPlaylist = new ArrayList<>();
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
            if (callBack != null) {
                callBack.returnPlayList(urlPlaylist);
            }
        });
    }


    private void searchKYData(String keyWord) {
        Map<String, Object> params = new HashMap<>();
        params.put("wd", keyWord);
        params.put("submit", "search");
        OkhClientUtil.getInstance().postHtml(Constant.SEARCH, params, result -> {
            Document document = Jsoup.parse(result);
            Elements elements = document.getElementsByClass("xing_vb");
            List<SearchItem> items = new ArrayList<>();
            for (Element postItem : elements) {
                //像jquery选择器一样，获取文章标题元素
                Elements list = postItem.getElementsByClass("xing_vb4");
                for (Element item : list) {
                    Element link = item.select("a").first();
                    String relHref = link.attr("href");
                    SearchItem search = new SearchItem(item.text(), Constant.BASEURL + relHref, "酷云资源网");
                    search.setType(0);
                    items.add(search);
                }
            }
            if (searchlistener != null) {
                searchlistener.returnSearchList(items);
            }
        });
    }

    private void searchZDData(String keyWord) {
        Map<String, Object> params = new HashMap<>();
        params.put("wd", keyWord);
        params.put("submit", "search");
        OkhClientUtil.getInstance().postHtml(Constant.ZUIDA_SEARCH, params, result -> {
            Document document = Jsoup.parse(result);
            Elements elements = document.getElementsByClass("xing_vb");
            List<SearchItem> items = new ArrayList<>();
            for (Element postItem : elements) {
                //像jquery选择器一样，获取文章标题元素
                Elements list = postItem.getElementsByClass("xing_vb4");
                for (Element item : list) {
                    Element link = item.select("a").first();
                    String relHref = link.attr("href");
                    SearchItem search = new SearchItem(item.text(), Constant.ZUIDA_BASEURL + relHref, "最大资源网");
                    search.setType(1);
                    items.add(search);
                }
            }
            if (searchlistener != null) {
                searchlistener.returnSearchList(items);
            }
        });
    }

    private void searchYJData(String keyWord) {
        Map<String, Object> params = new HashMap<>();
        params.put("wd", keyWord);
        OkhClientUtil.getInstance().postHtml(Constant.YONGJIU_SEARCH, params, result -> {
            Document document = Jsoup.parse(result);
            Elements elements = document.getElementsByClass("tbody");
            List<SearchItem> items = new ArrayList<>();

            for (Element postItem : elements) {
                //像jquery选择器一样，获取文章标题元素
                Elements list = postItem.getElementsByClass("DianDian");
                for (Element item : list) {
                    Element link = item.select("a").first();
                    String relHref = link.attr("href");
                    SearchItem search = new SearchItem(link.text(), Constant.YONGJIU_BASEURL + relHref, "永久资源网");
                    search.setType(2);
                    items.add(search);
                    System.out.println(link.text());
                }
            }
            if (searchlistener != null) {
                searchlistener.returnSearchList(items);
            }
        });
    }

    private void searchKHData(String keyWord) {
        Map<String, Object> params = new HashMap<>();
        params.put("wd", keyWord);
        params.put("submit", "search");
        OkhClientUtil.getInstance().postHtml(Constant.KUHA_SEARCH, params, result -> {
            List<SearchItem> items = new ArrayList<>();
            Document document = Jsoup.parse(result);
            Elements elements = document.getElementsByClass("xing_vb");
            for (Element postItem : elements) {
                //像jquery选择器一样，获取文章标题元素
                Elements list = postItem.getElementsByClass("xing_vb4");
                for (Element item : list) {
                    Element link = item.select("a").first();
                    String relHref = link.attr("href");
                    SearchItem search = new SearchItem(item.text(), Constant.KUHA_BASEURL + relHref, "哈酷资源网");
                    search.setType(3);
                    items.add(search);
                }
            }
            if (searchlistener != null) {
                searchlistener.returnSearchList(items);
            }
        });
    }

}
