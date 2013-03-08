package com.espn.phantomjs.client;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

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

    private WebDriver phantomWebDriver() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true); //< not really needed: JS enabled by default
        caps.setCapability("takesScreenshot", true); //< yeah, GhostDriver haz screenshotz!
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomJsBinaryLocation);
        caps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.60 Safari/537.17");
        caps.setCapability("phantomjs.page.settings.host", "www.sportslogos.net");
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] {
            "--web-security=false",
            "--ssl-protocol=any",
            "--ignore-ssl-errors=true"
        });
 
        // Launch driver (will take care and ownership of the phantomjs process)
        WebDriver driver = new PhantomJSDriver(caps);
        
        driver.manage().timeouts().setScriptTimeout(600l, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(600l, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(600l, TimeUnit.SECONDS);
        //WebDriverWait wait = new WebDriverWait(driver, 10);
        return driver;
    }

    @Override
    public <T> T call(PhantomJsCallable<T> callable) throws Exception {
        WebDriver driver = phantomWebDriver();
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
