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
    public void testExecJs() throws Exception {
        PhantomJsWebDriverClientInterface client =
                new PhantomJsWebDriverClient("/usr/local/bin/phantomjs", 60);
        PhantomJs js = new PhantomJs(client);
        String script = "function() {\n"
                + "\n"
                + "        var imgs = $('#body img'), arr = [];\n"
                + "        $.each(imgs, function(i, image) {\n"
                + "            var src = $(this).attr('src'),\n"
                + "            thumb = null, full = null,\n"
                + "            href = $(this).parent('a').attr('href'),\n"
                + "            type = null, year = null, team = null, guid = null;\n"
                + "            \n"
                + "            if (href && src) {\n"
                + "                /* derive image sources */\n"
                + "                if (src.indexOf('thumbs') > -1) {\n"
                + "                    thumb = src;\n"
                + "                    full = src.replace('thumbs', 'full');\n"
                + "                }\n"
                + "                \n"
                + "                /* derive hierarchy */\n"
                + "                var ontology = href;\n"
                + "                if (ontology.charAt(ontology.length-1) == '/') {\n"
                + "                    ontology = ontology.substring(0, ontology.length-1);\n"
                + "                }\n"
                + "                var parts = ontology.split('/');\n"
                + "                if (parts.length == 6) {\n"
                + "                    type = parts[parts.length-1];\n"
                + "                    year = parts[parts.length-2];\n"
                + "                    team = parts[parts.length-3];\n"
                + "                    guid = parts[parts.length-4];\n"
                + "                    \n"
                + "                    var obj = { type: type, year: year, team: team, thumb: thumb, full: full, href: href };\n"
                + "                    arr.push(obj);\n"
                + "                }\n"
                + "            }\n"
                + "        });\n"
                + "\n"
                + "        var str = JSON.stringify(arr); \n"
                + "        //console.log(str);\n"
                + "        return str;\n"
                + "    }();";
        String msg;
        script = "return 'hello';";

        try {
            msg = js.executeScript("http://www.sportslogos.net/logos/list_by_team/53/Boston_Red_Sox/", script, "disclaimer");
        } catch (Exception eee) {
            eee.printStackTrace();
            throw eee;
        }
        //String msg = js.executeScript("http://espn.go.com", script);
        System.out.println(msg);
    }
    /*
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
     */

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
