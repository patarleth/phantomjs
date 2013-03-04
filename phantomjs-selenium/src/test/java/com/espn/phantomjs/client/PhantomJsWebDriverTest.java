package com.espn.phantomjs.client;

import com.espn.phantomjs.PhantomJs;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * @author arlethp1
 */
public class PhantomJsWebDriverTest {

    @Test
    public void testClient() throws Exception {
        //three threads, queue size = 100
        PhantomJsWebDriverClientInterface client =
                new PhantomJsWebDriverClient("/usr/local/bin/phantomjs", 60);
        screenshot(client);
    }

    @Test
    public void testConcurrentClient() throws Exception {
        //three threads, queue size = 100, timeout 60 seconds
        PhantomJsWebDriverClientInterface client =
                new ConcurrentPhantomJsWebDriverClient("/usr/local/bin/phantomjs", 3, 100, 60);
        screenshot(client);
    }

    @Test
    public void testPhantomJs() throws Exception {
        PhantomJsWebDriverClientInterface client =
                new PhantomJsWebDriverClient("/usr/local/bin/phantomjs", 60);
        PhantomJs js = new PhantomJs(client);
        byte[] bytes = js.screenshot("http://espn.go.com");
        System.out.println("bytes.length " + bytes.length);
    }

    private void screenshot(PhantomJsWebDriverClientInterface client) throws Exception {
        try {
            //System.out.println("client created");

            String url = "http://espn.go.com";
            PhantomJsCallable<byte[]> work =
                    buildPngScreenshotPhantomJsCallable(url);
            //System.out.println("work created");

            byte[] bytes = client.call(work);
            System.out.println(" length " + bytes.length);
        } finally {
            client.quit();
        }
    }

    private static void simpleMain(String[] args) throws Exception {
        // prepare capabilities
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);                //< not really needed: JS enabled by default
        caps.setCapability("takesScreenshot", true);    //< yeah, GhostDriver haz screenshotz!
        caps.setCapability(
                PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                "/usr/local/bin/phantomjs");

        // Launch driver (will take care and ownership of the phantomjs process)
        WebDriver driver = new PhantomJSDriver(caps);

        // Load Google.com
        driver.get("http://www.google.com");
        // Locate the Search field on the Google page
        driver.manage().timeouts().setScriptTimeout(60l, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(60l, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(60l, TimeUnit.SECONDS);

        WebElement element = driver.findElement(By.name("q"));
        // Type Cheese
        String strToSearchFor = "Cheese!";
        element.sendKeys(strToSearchFor);
        // Submit form
        element.submit();

        System.out.println("google.com title contains Cheese! " + driver.getTitle().toLowerCase().contains(strToSearchFor.toLowerCase()));

        // done
        driver.quit();
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
}
