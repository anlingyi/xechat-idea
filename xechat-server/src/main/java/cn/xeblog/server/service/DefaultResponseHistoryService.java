package cn.xeblog.server.service;

import cn.xeblog.commons.entity.Response;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author anlingyi
 * @date 2021/9/11 5:10 下午
 */
public class DefaultResponseHistoryService extends AbstractResponseHistoryService {

    private final ArrayBlockingQueue<Response> queue;

    private final ReentrantLock lock = new ReentrantLock(true);

    private final int cpuNum = Runtime.getRuntime().availableProcessors();

    private final ExecutorService threadPool = new ThreadPoolExecutor(cpuNum, cpuNum * 2, 60,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(500), new ThreadPoolExecutor.CallerRunsPolicy());

    public DefaultResponseHistoryService() {
        this(10);
    }

    public DefaultResponseHistoryService(int size) {
        this.queue = new ArrayBlockingQueue<>(size, true);
    }

    @Override
    protected void addHistoryHandler(Response response) {
        threadPool.execute(() -> {
            if (!queue.offer(response)) {
                lock.lock();
                try {
                    queue.poll();
                    queue.offer(response);
                } finally {
                    lock.unlock();
                }
            }
        });
    }

    @Override
    public List<Response> getHistory() {
        return queue.stream().collect(Collectors.toList());
    }

}
