package ru.bio4j.spring.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.bio4j.spring.commons.types.DelayedExecutor;

import java.util.concurrent.*;

@Service
public class DelayedExecutorImpl<T> implements DelayedExecutor<T> {
    private static final Logger LOG = LoggerFactory.getLogger(DelayedExecutorImpl.class);

    private final Long defaultDelay;
    private final TimeUnit defaultTimeUnit;
    private final ConcurrentHashMap<String, ScheduledFuture<T>> queriesQueue;
    private final ScheduledExecutorService executor;

    /**
     * Создаёт экземпляр со значением задержки по умолчанию (600 мсек.).
     */
    public DelayedExecutorImpl() {
        this(600, TimeUnit.MILLISECONDS);
    }

    /**
     * Создаёт экземпляр с указанным значением задержки по умолчанию.
     */
    public DelayedExecutorImpl(long delay, TimeUnit timeUnit) {
        this.defaultDelay = delay;
        this.defaultTimeUnit = timeUnit;
        this.queriesQueue = new ConcurrentHashMap<>();
        this.executor = Executors.newScheduledThreadPool(5);
    }

    public T execute(Callable<T> action, String requestHash, long delay, TimeUnit timeUnit) throws ExecutionException {
        ScheduledFuture<T> newFuture = executor.schedule(action, delay, timeUnit);
        ScheduledFuture<T> oldFuture = queriesQueue.put(requestHash, newFuture);
        if (oldFuture != null)
            oldFuture.cancel(false);
        try {
            return newFuture.get();
        } catch (Exception e) {
            if (e instanceof InterruptedException || e instanceof CancellationException)
                LOG.debug("Delayed execution interrupted.");
            else
                LOG.debug("Some other exception occurred.", e);
            return null;
        }
    }

    public T execute(Callable<T> action, String requestHash) throws ExecutionException {
        return execute(action, requestHash, this.defaultDelay, this.defaultTimeUnit);
    }
}
