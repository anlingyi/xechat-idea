package cn.xeblog.plugin.game.box.util;

import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImagesUtils {

    private static final Map<Integer, Image> imageMap = new HashMap<>(13);

    public static void initMapDataDefault() {
        for (int i = 1; i <= 13; i++) {
            URL url = MapsUtils.class.getResource("/images/pic" + i + ".png");
            if (url != null) {
                imageMap.put(i, Toolkit.getDefaultToolkit().getImage(url));
            }
        }
    }

    public static Map<Integer, Image> getImageMap() {
        return imageMap;
    }

    public int getTotal() {
        return imageMap.size();
    }
}
