package org.jboss.as.quickstarts.kitchensink.test;

import com.saucelabs.rest.Credential;
import com.saucelabs.selenium.client.factory.SeleniumFactory;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;

/**
 * This class demonstrates how to write a Selenium test using <a href="">Arquillian</a> to verify the behaviour of the
 * JBoss Kitchen Sink application.
 *
 * In order for Arquillian to perform a push to the OpenShift Git repository, the SSH_PASSPHRASE environment variable
 * should be set to the value of the passphrase for the SSH key associated with your OpenShift account.
 *
 * @author Ross Rowe
 */
@RunWith(Arquillian.class)
public class MemberRegistrationArquillianTest {

    /**
     *
     * @return a {@link WebArchive} instance that represents the actual Kitchen Sink web application.  This is used
     * by Arquillian to determine the specific web application being tested.
     *
     * @see <a href="https://docs.jboss.org/author/display/ARQ/Deployment+archives">Arquillian Deployment Archives</a>
     *
     */
    @Deployment(testable = false)
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "ROOT.war").addClass(Object.class);
    }

    /**
     * Populated via dependency injection.  This variable is populated with the URL of the site being tested, based on
     * the value returned by {@link #createTestArchive()}
     */
    @ArquillianResource
    URL deploymentURL;

    /**
     * The default value for the SELENIUM_DRIVER property.  This will be used to construct the specific WebDriver instance to be used
     * to run the tests.  The default value is to run the Selenium tests using Sauce OnDemand with a operating system of Windows 2008, using Firefox 4.
     *
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

        //attempt to retrieve Sauce OnDemand Credential information from ~/.sauce-ondemand first
        String userName;
        String accessKey;
        try {
            Credential credential = new Credential();
            userName = credential.getUsername();
            accessKey = credential.getKey();
        } catch (IOException e) {
            //attempt to set variables via properties
            userName = System.getProperty("sauce.user");
            accessKey = System.getProperty("access.key");
        }

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

    @After
    public void tearDown() throws Exception {
        webDriver.quit();
    }

}


