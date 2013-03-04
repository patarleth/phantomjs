package com.espn.phantomjs.client;

import org.openqa.selenium.WebDriver;

/**
 *
 * @author arlethp1
 */
public interface PhantomJsCallable<T> {

    public T callPhantomJs(WebDriver driver) throws Exception;
    
}
