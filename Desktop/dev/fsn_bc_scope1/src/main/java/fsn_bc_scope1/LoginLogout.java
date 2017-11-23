package fsn_bc_scope1;

import java.io.File;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import fsn_bc_scope1.Fsn_Design_Page;
import fsn_bc_scope1.Fsn_Discover_Page;


    public class LoginLogout {
	protected WebDriver driver;
	
	protected Fsn_Discover_Page fDiscover;
	protected Fsn_Design_Page fDesign;
	protected String FIREFOX = "firefox";
	protected String PERF = "UI_perf_Firefox";
	protected String GECKODRIVERLOCATION = "./geckodriver-v0.17.0-win64/geckodriver.exe";
	protected String GECKODRIVER = "webdriver.gecko.driver";
	protected String CHROMEINITIALIZ = "webdriver.chrome.driver";
	protected String CHROMELOCATION = "./chromedriver_win32/chromedriver.exe";
	protected String CHROME = "chrome";
	
  @Parameters({"USERNAME"})
    @AfterTest
		public void scenarioQuit(String USERNAME)
		{		
		 driver.findElement(By.xpath("//span[contains(@id,'__item0-name') and text()='"+USERNAME+"']")).click();
		 driver.findElement(By.xpath("//span[contains(@id,'__button3-content') and text()='Log Out']")).click();
		 try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		 driver.quit();
		}

	@Parameters({"URL", "USERNAME", "PASSWORD","driverOption"})
   	@BeforeTest()
	public void scenarioStart(String URL, String USERNAME, String PASSWORD,String driverOption) throws InterruptedException {
		if(driverOption.equalsIgnoreCase(FIREFOX)){
			System.setProperty(GECKODRIVER,GECKODRIVERLOCATION);
			FirefoxProfile profile=new FirefoxProfile();
			profile.setPreference("browser.download.dir", "C:\\SELENIUMFILE\\");
			profile.setPreference("browser.download.folderList", 2);
			driver = new FirefoxDriver(profile);
			driver.manage().deleteAllCookies();
			driver.navigate().to(URL);
			driver.manage().window().maximize();
            //driver.manage().window().setSize(new Dimension(1936, 1056));
			
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		}else if(driverOption.equalsIgnoreCase(CHROME)){
			System.setProperty(CHROMEINITIALIZ, CHROMELOCATION);
			new File("C:\\SeleniumDownloadFiles\\").mkdir();
			try {
				Thread.sleep(3000);
		
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (new File("C:\\SeleniumDownloadFiles\\").isDirectory()) {
				System.out.println("Directory Created");
			} else {
				System.out.println("Directory Not Created");
			}
			String downloadFilepath = "C:\\SeleniumDownloadFiles\\";
			java.util.HashMap<String, Object> chromePrefs = new java.util.HashMap<String, Object>();
			chromePrefs.put("profile.default_content_settings.popups", 0);
			chromePrefs.put("download.default_directory", downloadFilepath);
			chromePrefs.put("safebrowsing.enabled", "true");
			org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();
			options.setExperimentalOption("prefs", chromePrefs);
			options.addArguments("--test-type");
			options.addArguments("--test-type");
			options.addArguments("--disable-extensions");
			options.addArguments("--disable-infobars");
			DesiredCapabilities cap = new DesiredCapabilities();
			cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			cap.setCapability(ChromeOptions.CAPABILITY, options);
			driver = new ChromeDriver(cap);
			driver.manage().window().setSize(new Dimension(1936,1056));
			if(URL.contains("WorkflowTask"))
				URL = URL.concat("&/");
			driver.navigate().to(URL);
			driver.manage().timeouts().implicitlyWait(80, TimeUnit.SECONDS);
			Thread.sleep(3000);
			String str = driver.getCurrentUrl();
			System.out.println(str);
			driver.manage().window().setSize(new Dimension(1936,1056));
			System.out.println(driver.manage().window().getSize());	
		}
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[contains(@id,'username')]")).sendKeys(USERNAME);
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[contains(@id,'password')]")).sendKeys(PASSWORD);
		Thread.sleep(2000);
		driver.findElement(By.xpath("//span[contains(@id,'logon_button')]")).click();	
}
}