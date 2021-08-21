package cn.xeblog.plugin.util;

import cn.xeblog.commons.enums.Action;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.builder.RequestBuilder;
import cn.xeblog.plugin.cache.DataCache;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author anlingyi
 * @date 2020/9/10
 */
public class UploadUtils {

    private static boolean UPLOADING;
    private static final String PREFIX = "--";
    private static final String BOUNDARY = "*****";
    private static final String WRAP = "\r\n";
    private static final String TRANSPORT_START = PREFIX + BOUNDARY + WRAP;
    private static final String TRANSPORT_END = PREFIX + BOUNDARY + WRAP;
    private static final String ACCEPT_IMAGE_TYPE = "jpg,jpeg,gif,png";

    private static String upload(byte[] bytes, String fileName) {
        UPLOADING = true;
        ConsoleAction.showSimpleMsg("图片上传中...");

        String imgUrl = null;
        try {
            URL url = new URL("https://sm.ms/api/v2/upload");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.addRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
            connection.addRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36");

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(TRANSPORT_START);
            out.writeBytes("Content-Disposition: form-data; name=smfile; filename=" + fileName + WRAP);
            out.writeBytes(WRAP);
            out.write(bytes);
            out.writeBytes(WRAP);
            out.writeBytes(TRANSPORT_END);
            out.flush();
            out.close();

            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                InputStream in = connection.getInputStream();
                String str = IOUtils.toString(in, "UTF-8");
                in.close();

                Gson gson = new Gson();
                JsonObject json = gson.fromJson(str, JsonObject.class);
                JsonObject data = json.get("data") == null ? null : json.get("data").getAsJsonObject();
                if (data == null) {
                    imgUrl = json.get("images") == null ? null : json.get("images").getAsString();
                } else {
                    imgUrl = data.get("url").getAsString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ConsoleAction.showSimpleMsg("图片上传失败！");
        } finally {
            UPLOADING = false;
        }

        return imgUrl;
    }

    public static void uploadImageFile(File file) {
        String fileType = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        if (ACCEPT_IMAGE_TYPE.indexOf(fileType.toLowerCase()) < 0) {
            ConsoleAction.showSimpleMsg("不支持的图片类型！");
            return;
        }

        try (FileInputStream inputStream = new FileInputStream(file)) {
            sendImgAsync(IOUtils.toByteArray(inputStream), file.getName());
        } catch (Exception e) {
            e.printStackTrace();
            ConsoleAction.showSimpleMsg("图片上传失败！");
        }
    }

    public static void uploadImage(Image image) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write((BufferedImage) image, "jpg", out);
            sendImgAsync(out.toByteArray(), System.currentTimeMillis() + ".jpg");
        } catch (IOException e) {
            e.printStackTrace();
            ConsoleAction.showSimpleMsg("图片上传失败！");
        }
    }

    private static void sendImgAsync(byte[] bytes, String fileName) {
        if (!DataCache.isOnline) {
            ConsoleAction.showLoginMsg();
            return;
        }
        if (UPLOADING) {
            ConsoleAction.showSimpleMsg("请等待之前的图片上传完成！");
            return;
        }

        new Thread(() -> {
            String url = upload(bytes, fileName);
            if (StringUtils.isBlank(url)) {
                ConsoleAction.showSimpleMsg("图片上传失败！");
                return;
            }

            MessageAction.send(RequestBuilder.build(url, Action.CHAT));
        }).start();
    }
}
