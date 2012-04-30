package org.jboss.as.quickstarts.kitchensink.test;

import com.saucelabs.selenium.client.factory.SeleniumFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * This class demonstrates how to write a Selenium test to verify the behaviour of the JBoss Kitchen Sink application.
 *
 * The test uses the <a href="http://selenium-client-factory.infradna.com/">Selenium Client Factory</a> library to simplify configuring the test to run using
 * <a href="http://saucelabs.com">Sauce OnDemand</a>.
 *
 * @author Ross Rowe
 */
public class MemberRegistrationWebDriverTest {

    private String startingURL;

    /**
     * Default value to be used for the SELENIUM_DRIVER environment variable.
     */
    protected static final String DEFAULT_SAUCE_DRIVER = "sauce-ondemand:?max-duration=60&os=windows 2008&browser=firefox&browser-version=4.&username={0}&access-key={1}";

    private WebDriver webDriver;

    /**
     * Populates a new {@link WebDriver} instance. using the {@link com.saucelabs.selenium.client.factory.SeleniumFactory#createWebDriver()}
     * helper method.
     *
     * @see <a href="http://selenium-client-factory.infradna.com/">Selenium Client Factory</a> documentation
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        String userName = System.getProperty("sauce.user");
        String accessKey = System.getProperty("access.key");
        this.startingURL = System.getenv("SELENIUM_STARTING_URL");
        if (startingURL == null) {
            fail("Starting URL not defined");
        }

        String driver = System.getenv("SELENIUM_DRIVER");
        if (driver == null || driver.equals("")) {
            System.setProperty("SELENIUM_DRIVER", MessageFormat.format(DEFAULT_SAUCE_DRIVER, userName, accessKey));
        }
        webDriver = SeleniumFactory.createWebDriver();

    }

    @Test
    public void verifyKitchenSink() throws Exception {
        //verify that the kitchen sink page is okay
        webDriver.get(startingURL);
        assertEquals("Welcome to JBoss AS 7!", webDriver.findElement(By.cssSelector("h1")).getText());
    }

    @Test
    public void addRegistration() throws Exception {
        //adds a registration with all the details
        webDriver.get(startingURL);
        webDriver.findElement(By.id("reg:name")).clear();
        webDriver.findElement(By.id("reg:name")).sendKeys("Valid Name");
        webDriver.findElement(By.id("reg:email")).clear();
        webDriver.findElement(By.id("reg:email")).sendKeys("testing@test.org");
        webDriver.findElement(By.id("reg:phoneNumber")).clear();
        webDriver.findElement(By.id("reg:phoneNumber")).sendKeys("1234567890");
        webDriver.findElement(By.id("reg:register")).click();

        //verify that a new row has been added
        //assertEquals("1234567890", webDriver.findElement(By.xpath("//div[@id='content']/table/tbody/tr[2]/td[4]")).getText());

    }

    @Test
    public void noName() throws Exception {
        //add a registration with no name
        webDriver.get(startingURL);
        webDriver.findElement(By.id("reg:register")).click();

        //verify that an error is displayed
        assertEquals("size must be between 1 and 25", webDriver.findElement(By.cssSelector("span.invalid")).getText());
    }

    @After
    public void tearDown() throws Exception {
        webDriver.quit();
    }

}

