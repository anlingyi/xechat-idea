package cn.xeblog.plugin.tools.read.api;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.xeblog.plugin.tools.read.error.LegadoApiException;
import cn.xeblog.plugin.tools.read.entity.Chapter;
import cn.xeblog.plugin.tools.read.entity.LegadoBook;

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

    private static final int PORT = 1122;

    private final String server;
    private final HttpClient client;

    public LegadoApi(String host) {
        if (!host.contains(":")) {
            host = host + ":" + PORT;
        }

        this.server = StrUtil.format("http://{}", host);
        this.client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(5000)).build();
    }

    public boolean testConnect() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(server)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception ignored) { }
        return false;
    }

    public List<LegadoBook> getBookshelf() throws LegadoApiException {
        String url = UrlBuilder.of(this.server).addPath("getBookshelf").build();
        String errorMsg = "[Legado]书架获取失败";
        JSONObject result = get(url, errorMsg);
        return handleArray(result, LegadoBook.class, errorMsg);
    }

    public List<Chapter> getChapterList(String bookUrl) throws LegadoApiException {
        String url = UrlBuilder.of(this.server).addPath("getChapterList").addQuery("url", bookUrl).build();
        String errorMsg = "[Legado]章节列表获取失败";
        JSONObject result = get(url, errorMsg);
        return handleArray(result, Chapter.class, errorMsg);
    }

    public String getBookContent(String bookUrl, int index) {
        String url = UrlBuilder.of(this.server)
                .addPath("getBookContent")
                .addQuery("url", bookUrl)
                .addQuery("index", index)
                .build();
        String errorMsg = "书籍内容获取失败";
        try {
            JSONObject result = get(url, errorMsg);
            if (result != null && StrUtil.isNotBlank(result.getStr("errorMsg"))) {
                return result.getStr("errorMsg");
            }
            return handleStr(result, errorMsg);
        } catch (LegadoApiException e) {
            return errorMsg;
        }
    }

    public void saveBookProgress(LegadoBook bookInfo) {
        String url = UrlBuilder.of(this.server).addPath("saveBookProgress").build();
        post(url, bookInfo);
    }

    private JSONObject get(String url, String errorMsg) throws LegadoApiException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return JSONUtil.parseObj(response.body());
            }
        } catch (Exception e) {
            LegadoApiException.throwException(errorMsg);
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

    private <T> List<T> handleArray(JSONObject result, Class<T> elementType, String errorMsg) throws LegadoApiException {
        if (result != null && result.getBool("isSuccess")) {
            JSONArray data = result.getJSONArray("data");
            return JSONUtil.toList(data, elementType);
        }
        LegadoApiException.throwException(errorMsg);
        return new ArrayList<>();
    }

    private String handleStr(JSONObject result, String errorMsg) throws LegadoApiException {
        if (result != null && result.getBool("isSuccess")) {
            return result.getStr("data", "");
        }
        LegadoApiException.throwException(errorMsg);
        return "";
    }
}
