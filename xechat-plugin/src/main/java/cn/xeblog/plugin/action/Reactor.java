package cn.xeblog.plugin.action;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

/**
 * @author anlingyi
 * @date 2022/9/19 6:39 AM
 */
@Slf4j
public class Reactor<T> implements Future<T> {

    private CountDownLatch latch;

    private int timeout;

    private TimeUnit timeUnit;

    private T result;

    public Reactor() {
        this(15, TimeUnit.SECONDS);
    }

    public Reactor(int timeout, TimeUnit timeUnit) {
        this.latch = new CountDownLatch(1);
        this.timeout = timeout;
        this.timeUnit = timeUnit;
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
    public T get() {
        return get(timeout, timeUnit);
    }

    @Override
    public T get(long timeout, @NotNull TimeUnit unit) {
        try {
            latch.await(timeout, unit);
        } catch (InterruptedException e) {
            log.error("获取响应结果异常", e);
        }
        return result;
    }

    protected void setResult(T result) {
        this.result = result;
        latch.countDown();
    }

}
