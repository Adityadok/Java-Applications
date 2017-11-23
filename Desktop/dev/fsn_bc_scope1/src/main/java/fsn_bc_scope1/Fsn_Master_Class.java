package fsn_bc_scope1;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import com.relevantcodes.extentreports.ExtentTest;

     // The above class Contains the Webdriver_Wait method 

    public class Fsn_Master_Class {
	public WebDriver driver;
	protected ExtentTest logger;
	
	protected Reporting Log;
	protected String[] corporateDetails = {"Privacy", "Terms of Use", "Legal Disclosure", "Copyrights"};
	protected String[] webpageTitle = {"SAP Privacy Statement", "Terms of Use Agreement For SAP.com", "Legal Disclosure", "Copyright Information"};
	public Actions action;
	protected String javaScriptcopyPasteargs = "arguments[0].value = arguments[1];";
	
	public Fsn_Master_Class(WebDriver driver) {
		Log= new Reporting(driver);
		PageFactory.initElements(driver, this);
		this.driver = driver;
		action=new Actions(driver);
	}
	public Fsn_Master_Class(WebDriver driver, ExtentTest logger, Reporting Log) {
		this.driver = driver;
		this.logger = logger;
		this.Log = Log;
	}

	public Fsn_Master_Class() {
	}

	public void webdriver_wait(String xpath_wait, int time_out){
	   FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver)
		.withTimeout(time_out, TimeUnit.SECONDS)
		.pollingEvery(2, TimeUnit.SECONDS)
		.ignoring(Throwable.class);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath_wait)));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath_wait)));
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath_wait)));
	}
	
	public void webdriver_webelement_wait(WebElement element, int time_out){
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver)
		.withTimeout(time_out, TimeUnit.SECONDS)
		.pollingEvery(1, TimeUnit.SECONDS)
		.ignoring(Throwable.class);
		wait.until(ExpectedConditions.visibilityOf(element));
		wait.until(ExpectedConditions.elementToBeClickable(element));
	}
	
	public void sleep(long time){
		try{
			Thread.sleep(time);
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	}

