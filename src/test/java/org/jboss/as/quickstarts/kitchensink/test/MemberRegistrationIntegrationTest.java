package org.jboss.as.quickstarts.kitchensink.test;

import com.saucelabs.selenium.client.factory.SeleniumFactory;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.net.URL;
import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;

/**
 * @author Ross Rowe
 */
@RunWith(Arquillian.class)
public class MemberRegistrationIntegrationTest {

    @Deployment(testable = false)
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "ROOT.war").addClass(Object.class);
    }

    @ArquillianResource
    URL deploymentURL;

    protected static final String DEFAULT_SAUCE_DRIVER = "sauce-ondemand:?max-duration=60&os=windows 2008&browser=firefox&browser-version=4.&username={0}&access-key={1}";

    private WebDriver webDriver;

    @Before
    public void setUp() throws Exception {

        String userName = System.getProperty("sauce.user");
        String accessKey = System.getProperty("access.key");
        System.setProperty("SELENIUM_STARTING_URL", deploymentURL.toString());
        String driver = System.getenv("SELENIUM_DRIVER");
        if (driver == null || driver.equals("")) {
            System.setProperty("SELENIUM_DRIVER", MessageFormat.format(DEFAULT_SAUCE_DRIVER, userName, accessKey));
        }
        webDriver = SeleniumFactory.createWebDriver();

    }

    @Test
    public void verifyKitchenSink() throws Exception {
        //verify that the kitchen sink page is okay
        webDriver.get(deploymentURL.toString());
        assertEquals("Welcome to JBoss AS 7!", webDriver.findElement(By.cssSelector("h1")).getText());
    }

    @Test
    public void addRegistration() throws Exception {
        //adds a registration with all the details
        webDriver.get(deploymentURL.toString());
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
        webDriver.get(deploymentURL.toString());
        webDriver.findElement(By.id("reg:register")).click();

        //verify that an error is displayed
        assertEquals("size must be between 1 and 25", webDriver.findElement(By.cssSelector("span.invalid")).getText());
    }

}


