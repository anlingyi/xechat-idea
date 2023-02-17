package cn.xeblog.plugin.util;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.xeblog.commons.entity.react.React;
import cn.xeblog.commons.entity.react.request.UploadReact;
import cn.xeblog.commons.entity.react.result.UploadReactResult;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.ReactAction;
import cn.xeblog.plugin.action.handler.ReactResultConsumer;
import com.intellij.util.ui.ImageUtil;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author anlingyi
 * @date 2020/9/10
 */
public class UploadUtils {

    private static boolean UPLOADING;
    private static final String[] ACCEPT_IMAGE_TYPE = new String[]{
            ImgUtil.IMAGE_TYPE_GIF,
            ImgUtil.IMAGE_TYPE_JPG,
            ImgUtil.IMAGE_TYPE_JPEG,
            ImgUtil.IMAGE_TYPE_BMP,
            ImgUtil.IMAGE_TYPE_PNG
    };

    public static void uploadImageFile(File file) {
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            inputStream.mark(28);
            String fileType = FileTypeUtil.getType(inputStream);
            if (!ArrayUtil.contains(ACCEPT_IMAGE_TYPE, fileType)) {
                throw new Exception("不支持的图片类型！");
            }

            inputStream.reset();
            sendImgAsync(IOUtils.toByteArray(inputStream), generateFileName(fileType));
        } catch (Exception e) {
            e.printStackTrace();
            ConsoleAction.showSimpleMsg(e.getMessage());
        }
    }

    public static void uploadImage(Image image) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            BufferedImage bufferedImage = ImageUtil.toBufferedImage(image);
            ImageIO.write(bufferedImage, "png", out);
            sendImgAsync(out.toByteArray(), generateFileName("png"));
        } catch (IOException e) {
            e.printStackTrace();
            ConsoleAction.showSimpleMsg("图片上传失败！");
        }
    }

    private static String generateFileName(String fileType) {
        fileType = fileType == null ? "jpg" : fileType;
        return IdUtil.fastUUID() + "." + fileType;
    }

    private static void sendImgAsync(byte[] bytes, String fileName) {
        if (UPLOADING) {
            ConsoleAction.showSimpleMsg("请等待之前的图片上传完成！");
            return;
        }

        UPLOADING = true;
        ConsoleAction.showSimpleMsg("图片上传中...");

        UploadReact uploadReact = new UploadReact();
        uploadReact.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
        uploadReact.setBytes(bytes);
        ReactAction.request(uploadReact, React.UPLOAD, new ReactResultConsumer<UploadReactResult>() {
            @Override
            public void succeed(UploadReactResult body) {
                UPLOADING = false;
                ConsoleAction.showSimpleMsg("图片上传成功！");
            }

            @Override
            public void failed(String msg) {
                UPLOADING = false;
                ConsoleAction.showSimpleMsg("图片上传失败！原因：" + msg);
            }
        });
    }
}
