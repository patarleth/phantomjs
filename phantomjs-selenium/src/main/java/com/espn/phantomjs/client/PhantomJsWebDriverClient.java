package com.espn.phantomjs.client;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * @author arlethp1
 */
public class PhantomJsWebDriverClient implements PhantomJsWebDriverClientInterface {

    String phantomJsBinaryLocation;
    long timeout = 60;

    public PhantomJsWebDriverClient(String phantomJsBinaryLocation, long timeout) {
        this.phantomJsBinaryLocation = phantomJsBinaryLocation;
    }

    private WebDriver buildDriver() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true); //< not really needed: JS enabled by default
        caps.setCapability("takesScreenshot", true); //< yeah, GhostDriver haz screenshotz!
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomJsBinaryLocation);
        // Launch driver (will take care and ownership of the phantomjs process)
        WebDriver driver = new PhantomJSDriver(caps);
        driver.manage().timeouts().setScriptTimeout(60l, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(60l, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(60l, TimeUnit.SECONDS);

        return driver;
    }

    @Override
    public <T> T call(PhantomJsCallable<T> callable) throws Exception {
        WebDriver driver = buildDriver();
        T result = null;
        try {
            result = callable.callPhantomJs(driver);
        } finally {
            driver.quit();
        }
        return result;
    }

    @Override
    public void quit() {
    }
}
