package fsn_bc_scope1;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.support.PageFactory;
import fsn_bc_scope1.Reporting;

public class Fsn_Discover_Page extends Fsn_Master_Class{
	public Fsn_Discover_Page(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver, this);
		this.driver = driver;
		Log=new Reporting(driver);
	}
	
}