package com.yolo.demo.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadUtil {

    private static final Logger logger = LoggerFactory.getLogger(ThreadUtil.class);

    static ExecutorService executorService = new ThreadPoolExecutor(2, 3,
            10000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());

    private static String simpleName = null;

    public static <V, T extends Callable<V>> List<V> executeCompletionService(List<T> tasks) throws Exception {
        if (CollectionUtils.isEmpty(tasks)) {
            return new ArrayList<>(1);
        }
        List<V> result = new ArrayList<>(tasks.size());
        CompletionService<V> completionService = new ExecutorCompletionService<>(executorService);
        for (T task : tasks) {
            Class<? extends Callable> aClass = task.getClass();
            simpleName = aClass.getSimpleName();
            completionService.submit(task);
        }
        Future<V> take = null;
        for (int index = 0; index < tasks.size(); index++) {
            try {
                take = completionService.take();
                V res = take.get();
                if (res != null) {
                    result.add(res);
                }
            } catch (Exception e) {
                if (take != null && !take.isDone()) {
                    take.cancel(true);
                }
                logger.info("simpleName==>" + simpleName);
                logger.warn(simpleName + " execute completion service error, message is " + e.getMessage());
                throw new Exception(e);
            }
        }
        return result;
    }

}
