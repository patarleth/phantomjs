package com.espn.phantomjs.client;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author arlethp1
 */
class PhantomJsThread extends Thread {

    WebDriver driver;

    public PhantomJsThread(Runnable r, String phantomJsBinaryLocation, long timeout) {
        super(r);
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true); //< not really needed: JS enabled by default
        caps.setCapability("takesScreenshot", true); //< yeah, GhostDriver haz screenshotz!
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomJsBinaryLocation);
        caps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.60 Safari/537.17");
        // Launch driver (will take care and ownership of the phantomjs process)
        driver = new PhantomJSDriver(caps);
        driver.manage().timeouts().setScriptTimeout(timeout, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(timeout, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);

        //System.out.println("PhantomJsThread PhantomJSDriver created");
    }
}
