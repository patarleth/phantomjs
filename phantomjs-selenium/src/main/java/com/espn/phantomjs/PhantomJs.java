package com.espn.phantomjs;

import com.espn.phantomjs.client.PhantomJsCallable;
import com.espn.phantomjs.client.PhantomJsWebDriverClient;
import com.espn.phantomjs.client.PhantomJsWebDriverClientInterface;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author arlethp1
 */
public class PhantomJs {

    private PhantomJsWebDriverClientInterface client;

    public PhantomJs(String phantomjsBinaryLocation, int timeout) {
        this(new PhantomJsWebDriverClient(phantomjsBinaryLocation, timeout));
    }

    public PhantomJs(PhantomJsWebDriverClientInterface client) {
        this.client = client;
    }

    private static PhantomJsCallable<byte[]> buildPngScreenshotPhantomJsCallable(final String url) {
        PhantomJsCallable<byte[]> work = new PhantomJsCallable<byte[]>() {
            @Override
            public byte[] callPhantomJs(WebDriver webDriver) throws Exception {
                byte[] bytes;

                // Load Google.com
                webDriver.get(url);

                // Take screenshot
                File srcFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
                FileInputStream fis = new FileInputStream(srcFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                bytes = new byte[(int) srcFile.length()];

                for (int i = 0; i < bytes.length; i++) {
                    int next = bis.read();
                    bytes[i] = (byte) next;
                }

                System.out.println(srcFile);

                // done
                return bytes;
            }
        };
        return work;
    }

    private static PhantomJsCallable<String> buildExecScriptPhantomJsCallable(final String url, final String scriptSrc, final String className) {
        PhantomJsCallable<String> work = new PhantomJsCallable<String>() {
            @Override
            public String callPhantomJs(WebDriver webDriver) throws Exception {
                // Load Google.com

                webDriver.get(url);
                if (className != null) {
                    WebDriverWait wait = new WebDriverWait(webDriver, 10);
                    //wait.until(ExpectedConditions.presenceOfElementLocated(By.name("disclaimer")));
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.className(className)));
                }
                JavascriptExecutor js = (JavascriptExecutor) webDriver;
                return "" + js.executeScript(scriptSrc);
            }
        };
        return work;
    }

    public byte[] screenshot(String url) throws Exception {
        return client.call(buildPngScreenshotPhantomJsCallable(url));
    }

    public String executeScript(String url, String scriptSrc, String waitUntilElementCSSClass) throws Exception {
        return client.call(buildExecScriptPhantomJsCallable(url, scriptSrc, waitUntilElementCSSClass));
    }
}
