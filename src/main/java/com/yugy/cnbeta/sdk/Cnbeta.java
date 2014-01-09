package com.yugy.cnbeta.sdk;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.yugy.cnbeta.network.RequestManager;
import com.yugy.cnbeta.utils.DebugUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;

/**
 * Created by yugy on 14-1-6.
 */
public class Cnbeta {

    private static final String API_URL         = "http://api.cnbeta.com/capi/phone";
    private static final String API_NEWSLIST = "/newslist";
    private static final String API_COMMENT     = "/comment";
    private static final String API_HOTCOMMENT = "/homecomment";
    private static final String API_TOP10       = "/top10";
    private static final String API_NEWSCONTENT = "/newscontent";

    public static void getNewsList(Context context, String fromArticleId,
                            Response.Listener<JSONArray> listener,
                            Response.ErrorListener errorListener){
        RequestManager.getInstance().addRequest(context, new JsonArrayRequest(
                API_URL + API_NEWSLIST + "?fromArticleId=" + fromArticleId + "&limit=40",
                listener,
                errorListener
        ));
    }

    public static void getHotComment(Context context, String fromHMCommentId,
                                     Response.Listener<JSONArray> listener,
                                     Response.ErrorListener errorListener){
        RequestManager.getInstance().addRequest(context, new JsonArrayRequest(
                API_URL + API_HOTCOMMENT + "?fromHMCommentId=" + fromHMCommentId + "&limit=40",
                listener,
                errorListener
        ));
    }

    public static void getTopTenList(Context context, Response.Listener<JSONArray> listener,
                                     Response.ErrorListener errorListener){
        RequestManager.getInstance().addRequest(context, new JsonArrayRequest(
                API_URL + API_TOP10,
                listener,
                errorListener
        ));
    }

    public static void getNewsComment(Context context, String articleId,
                                      Response.Listener<JSONArray> listener,
                                      Response.ErrorListener errorListener){
        RequestManager.getInstance().addRequest(context, new JsonArrayRequest(
                API_URL + API_COMMENT + "?article=" + articleId,
                listener,
                errorListener
        ));
    }

    public static void getNewsContent(Context context, String articleId,
                                      final Response.Listener<JSONArray> listener,
                                      Response.ErrorListener errorListener){
        RequestManager.getInstance().addRequest(context, new StringRequest(
                API_URL + API_NEWSCONTENT + "?articleId=" + articleId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        JSONArray jsonArray = new JSONArray();
                        Document document = Jsoup.parse(s);
                        //选出主题
                        Element element = document.body().select("span#content").first();
                        //去除标题
                        element.select("span[style=text-align:center;]").remove();
//                        DebugUtils.log(result);
                        //划分文字
                        final String[] textPieces = element.html().split("<img[^>]+>");
                        //选出所有图片
                        Elements imgElements = element.select("img");
                        final String[] imgPieces = new String[imgElements.size()];
                        for(int i = 0; i < imgPieces.length; i++){
                            imgPieces[i] = imgElements.get(i).absUrl("src");
                        }
                        //组成jsonArray
                        int length = Math.min(textPieces.length, imgPieces.length);
                        try{
                            for(int i = 0; i < length; i++){
                                final int finalI = i;
                                jsonArray.put(new JSONObject(){{
                                    put("type", "text");
                                    put("value", textPieces[finalI]);
                                }});
                                jsonArray.put(new JSONObject(){{
                                    put("type", "img");
                                    put("value", imgPieces[finalI]);
                                }});
                            }
                            if(textPieces.length > imgPieces.length){
                                for(int i = imgPieces.length; i < textPieces.length; i++){
                                    final int finalI = i;
                                    jsonArray.put(new JSONObject(){{
                                        put("type", "text");
                                        put("value", textPieces[finalI]);
                                    }});
                                }
                            }else if(textPieces.length > imgPieces.length){
                                for(int i = textPieces.length; i < imgPieces.length; i++){
                                    final int finalI = i;
                                    jsonArray.put(new JSONObject(){{
                                        put("type", "img");
                                        put("value", imgPieces[finalI]);
                                    }});
                                }
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        DebugUtils.log(jsonArray);
                        listener.onResponse(jsonArray);
                    }
                },
                errorListener
        ){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String str = null;
                try {
                    str = new String(response.data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
            }
        });
    }











//    public abstract static class GetNewestTask extends AsyncTask<Integer, Void, ArrayList<NewsListModel>>{
//
//        @Override
//        protected ArrayList<NewsListModel> doInBackground(Integer... params) {
//            ArrayList<NewsListModel> datas = null;
//            try {
//                Document document = Jsoup.connect("http://m.cnbeta.com/list_latest_" + params[0] + ".htm").get();
//                Elements elements = document.select("body #page_list .module .module_list .clear");
//                datas = new ArrayList<NewsListModel>();
//                for(Element element : elements){
//                    NewsListModel data = new NewsListModel();
//                    String url = element.select("div a").attr("href");
//                    Pattern pattern = Pattern.compile("http://m\\.cnbeta\\.com/view_([0-9]+)\\.htm");
//                    Matcher matcher = pattern.matcher(url);
//                    if(matcher.find()){
//                        data.id = matcher.group(1);
//                        data.title = element.select("div a").text();
//                        data.hotCount = element.select(".list_ico .ico_view").text();
//                        datas.add(data);
//                    }
//                }
////                DebugUtils.log(elements.toString());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return datas;
//        }
//
//        @Override
//        protected abstract void onPostExecute(ArrayList<NewsListModel> datas);
//
//    }

}
