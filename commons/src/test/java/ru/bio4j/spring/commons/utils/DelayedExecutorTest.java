package ru.bio4j.spring.commons.utils;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.*;

public class DelayedExecutorTest {
    private static DelayedExecutorImpl<String> delayedExecutor;

    @BeforeClass
    public static void setUp() {
        delayedExecutor = new DelayedExecutorImpl<>(1000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testSingle() throws ExecutionException {
        String res = delayedExecutor.execute(() -> "first", "111");
        Assert.assertEquals(res, "first");
    }

    @Test
    public void testSeveral() throws ExecutionException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
        ScheduledFuture<String> res1 = executor.schedule(() -> delayedExecutor.execute(() -> "first", "111"), 0, TimeUnit.MILLISECONDS);
        ScheduledFuture<String> res2 = executor.schedule(() -> delayedExecutor.execute(() -> "second", "111"), 200, TimeUnit.MILLISECONDS);
        ScheduledFuture<String> res3 = executor.schedule(() -> delayedExecutor.execute(() -> "third", "111"), 1500, TimeUnit.MILLISECONDS);
        ScheduledFuture<String> res4 = executor.schedule(() -> delayedExecutor.execute(() -> "other", "222"), 300, TimeUnit.MILLISECONDS);
        try {
            Assert.assertNull(res1.get());
        } catch (InterruptedException | CancellationException e) {
            Assert.assertTrue(true);
        }
        try {
            Assert.assertEquals(res2.get(), "second");
        } catch (InterruptedException | CancellationException e) {
            Assert.assertTrue(true);
        }
        try {
            Assert.assertEquals(res3.get(), "third");
        } catch (InterruptedException | CancellationException e) {
            Assert.assertTrue(true);
        }
        try {
            Assert.assertEquals(res4.get(), "other");
        } catch (InterruptedException | CancellationException e) {
            Assert.assertTrue(true);
        }
    }
}
