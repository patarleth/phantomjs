package com.espn.phantomjs.client;


import java.util.ArrayList;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author arlethp1
 */
public class ConcurrentPhantomJsWebDriverClient implements PhantomJsWebDriverClientInterface {

    private final String phantomJsBinaryLocation;
    private long timeout = 60;
    private int corePoolSize = 10;
    private int maximumPoolSize = 10;
    private int keepAliveTime = 1;
    private BlockingQueue blockingQueue;
    private ArrayList<PhantomJsThread> threadList = new ArrayList<PhantomJsThread>();
    private ThreadFactory threadFactory = null;
    final ThreadPoolExecutor executor;

    public ConcurrentPhantomJsWebDriverClient(final String phantomJsBinaryLocation, int threadCount, int queueSize, final long timeout) {
        this.timeout = timeout;
        this.threadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            PhantomJsThread t = new PhantomJsThread(r, phantomJsBinaryLocation, timeout);

            //System.out.println("new PhantomJsThread created");
            threadList.add(t);
            return t;
        }
    };
        this.phantomJsBinaryLocation = phantomJsBinaryLocation;
        this.corePoolSize = threadCount;
        this.maximumPoolSize = threadCount;
        this.blockingQueue = new ArrayBlockingQueue(queueSize);
        this.executor = new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize + 1, keepAliveTime, TimeUnit.DAYS, blockingQueue,
                threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public <T> T call(final PhantomJsCallable<T> jsCallable) throws InterruptedException, ExecutionException {
        Callable<T> callable = new Callable<T>() {
            @Override
            public T call() {
                //System.out.println("call called");
                T result = null;
                try {
                    PhantomJsThread thread = (PhantomJsThread) Thread.currentThread();
                    result = jsCallable.callPhantomJs(thread.driver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;

            }
        };
        Future<T> future = this.executor.submit(callable);
        //System.out.println("future submitted");
        return future.get();
    }

    @Override
    public void quit() {
        synchronized (this.executor) {
            for (PhantomJsThread t : this.threadList) {
                try {
                    t.driver.quit();
                } catch (Throwable throwable) {
                }
            }
            this.executor.shutdown();
        }
    }
}
