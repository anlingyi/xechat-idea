package cn.xeblog.plugin.action;

import cn.xeblog.commons.entity.react.result.ReactResult;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

/**
 * @author anlingyi
 * @date 2022/9/19 6:39 AM
 */
@Slf4j
public class Reactor<T> implements Future<ReactResult<T>> {

    private CountDownLatch latch;

    private int timeout;

    private TimeUnit timeUnit;

    private ReactResult<T> result;

    private Channel channel;

    private boolean completed;

    public Reactor() {
        this(15, TimeUnit.SECONDS);
    }

    public Reactor(int timeout, TimeUnit timeUnit) {
        this.latch = new CountDownLatch(1);
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return result != null;
    }

    @Override
    public ReactResult<T> get() {
        return get(timeout, timeUnit);
    }

    @Override
    public ReactResult<T> get(long timeout, @NotNull TimeUnit unit) {
        try {
            latch.await(timeout, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return result;
    }

    protected void setResult(ReactResult<T> result) {
        if (completed) {
            return;
        }

        this.completed = true;
        this.result = result;
        latch.countDown();
        close();
    }

    public void close() {
        if (channel != null && channel.isActive()) {
            channel.close();
        }
    }

}
