package fsn_bc_scope1;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Fsn_Design_Page extends Fsn_Discover_Page{
	Actions action;
	
	String bank_response= "//div[contains(@id,'chart-bar-ontrack')]";
	String search="//input[contains(@id,'__field0-I')]";
	String journal= "//div[contains(@class,'sapMITBText') and text()='Journal']";
	String payment_batch= "//span[contains(@id,'__icon0')]";
	
	@FindBy(xpath="//span[contains(@id,'__icon0')]")
	private WebElement Payment_Batch;
	@FindBy(xpath="//div[contains(@id,'chart-bar-ontrack')]")
	private WebElement BankResponse;
	@FindBy(xpath="//input[contains(@id,'__field0-I')]")
	private WebElement Search;
	@FindBy(xpath="//div[contains(@class,'sapMITBText') and text()='Journal']")
	private WebElement Journal;
	
	public Fsn_Design_Page(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver, this);
		this.driver = driver;
		Log = new Reporting(driver);
		action = new Actions(driver);
		}

	public void onClick(String INPUT_ID) throws Exception{
		Payment_Batch.click();
		try{
		webdriver_wait(bank_response,5);
		}
		catch(Exception e)
		{
			throw new Exception("No New Order With Bank Response For INPUT_ID:-"+INPUT_ID);
		}
		BankResponse.click();
		webdriver_wait(search,5);
		Search.click();
		Search.sendKeys(INPUT_ID);
		try{
			Robot robot= new Robot();
			robot.delay(2000);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
		}
		catch (AWTException e){
			e.printStackTrace();
		}
		try{
			webdriver_wait("//span[text()='"+INPUT_ID+"']", 20);
			}
			catch(Exception e){
				throw new Exception("No Transaction With INPUT ID:-"+INPUT_ID);	
			}
		driver.findElement(By.xpath("//span[text()='"+INPUT_ID+"']")).click();
		webdriver_wait(journal,5);
		sleep(3000);
		Journal.click();
		try{
			Robot robot= new Robot();
			robot.delay(2000);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
		}
		catch (AWTException e){
			e.printStackTrace();
		}
		}
	
	public boolean testIfExists(List<String> actions,WebDriver driver) {
		int i=0;
		for(String action:actions) {
			sleep(1000);
			if(driver.findElements(By.xpath("//*[contains(@id,'-rows-row"+i+"') and contains(@class,'sapUiTableRow')] //span[text()='"+action+"']")).size()!=1){
				System.out.println(actions.get(i)+ " Event Does Not Exist");
				return false;
			}
			i++;	
		}
		return true;
	}
	}