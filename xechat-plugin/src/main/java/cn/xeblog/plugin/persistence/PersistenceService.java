package cn.xeblog.plugin.persistence;

import cn.xeblog.commons.constants.Commons;
import cn.xeblog.plugin.cache.DataCache;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author anlingyi
 * @date 2022/6/27 5:43 上午
 */
@State(name = Commons.KEY_PREFIX + "data", storages = {@Storage(Commons.KEY_PREFIX + "data.xml")})
public class PersistenceService implements PersistentStateComponent<PersistenceData> {

    private static PersistenceData data = new PersistenceData();

    @Override
    public @Nullable PersistenceData getState() {
        data.setUsername(DataCache.username);
        data.setMsgNotify(DataCache.msgNotify);
        return data;
    }

    @Override
    public void loadState(@NotNull PersistenceData state) {
        data = state;
        DataCache.username = data.getUsername();
        int msgNotify = data.getMsgNotify();
        if (msgNotify == 0) {
            msgNotify = 1;
        }
        DataCache.msgNotify = msgNotify;
    }

}
