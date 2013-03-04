package com.espn.phantomjs.client;


/**
 *
 * @author arlethp1
 */
public interface PhantomJsWebDriverClientInterface {

    public <T> T call(PhantomJsCallable<T> callable) throws Exception;

    public void quit();
}
