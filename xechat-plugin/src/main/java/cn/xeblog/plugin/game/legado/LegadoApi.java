package cn.xeblog.plugin.game.legado;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.xeblog.commons.entity.game.legado.BookInfo;
import cn.xeblog.commons.entity.game.legado.LegadoChapter;
import cn.xeblog.plugin.util.NotifyUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LYF
 * @date 2022-07-14
 */
public class LegadoApi {

    private final String server;
    private final HttpClient client;

    public LegadoApi(String server) {
        this.server = server;
        this.client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(5000)).build();
    }

    public List<BookInfo> getBookshelf() {
        String url = UrlBuilder.of(this.server).addPath("getBookshelf").build();
        String errorMsg = "书架获取失败";
        JSONObject result = get(url, errorMsg);
        return handleArray(result, BookInfo.class, errorMsg);
    }

    public List<LegadoChapter> getChapterList(String bookUrl) {
        String url = UrlBuilder.of(this.server).addPath("getChapterList").addQuery("url", bookUrl).build();
        String errorMsg = "章节列表获取失败";
        JSONObject result = get(url, errorMsg);
        return handleArray(result, LegadoChapter.class, errorMsg);
    }

    public String getBookContent(String bookUrl, int index) {
        String url = UrlBuilder.of(this.server)
                .addPath("getBookContent")
                .addQuery("url", bookUrl)
                .addQuery("index", index)
                .build();
        String errorMsg = "书籍内容获取失败";
        JSONObject result = get(url, errorMsg);
        return handleStr(result, errorMsg);
    }

    public void saveBookProgress(BookInfo bookInfo) {
        String url = UrlBuilder.of(this.server).addPath("saveBookProgress").build();
        post(url, bookInfo);
    }

    private JSONObject get(String url, String errorMsg) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return JSONUtil.parseObj(response.body());
            }
        } catch (Exception e) {
            errorNotify(errorMsg);
        }
        return null;
    }

    private void post(String url, Object body) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(JSONUtil.toJsonStr(body)))
                .header("Content-Type", "application/json")
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ignored) { }
    }

    private <T> List<T> handleArray(JSONObject result, Class<T> elementType, String errorMsg) {
        if (result != null && result.getBool("isSuccess")) {
            JSONArray data = result.getJSONArray("data");
            return JSONUtil.toList(data, elementType);
        }
        errorNotify(errorMsg);
        return new ArrayList<>();
    }

    private String handleStr(JSONObject result, String errorMsg) {
        if (result != null && result.getBool("isSuccess")) {
            return result.getStr("data", "");
        }
        errorNotify(errorMsg);
        return "";
    }

    private void errorNotify(String errorMsg) {
        NotifyUtils.error("Api Error", errorMsg);
    }
}
