package cn.xeblog.plugin.game.read.util;

import cn.hutool.core.util.StrUtil;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;

/**
 * @author LYF
 * @date 2022-07-21
 */
public class CharsetConverter extends Converter<Charset> {

    @Override
    public @Nullable Charset fromString(@NotNull String value) {
        if (StrUtil.isNotBlank(value)) {
            return Charset.forName(value);
        }
        return null;
    }

    @Override
    public @Nullable String toString(@NotNull Charset value) {
        return StrUtil.toStringOrNull(value);
    }
}
