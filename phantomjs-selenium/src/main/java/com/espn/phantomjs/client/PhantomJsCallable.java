package com.espn.phantomjs.client;

import java.util.concurrent.Callable;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author arlethp1
 */
public abstract interface PhantomJsCallable<T> {

    public abstract T callPhantomJs(WebDriver driver) throws Exception;
    
}
