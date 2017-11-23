package fsn_bc_scope1;

import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class Fsn_Bc_Automation_Primary extends LoginLogout{
   
	@Parameters({"INPUT_ID"})
	@Test
	public void onStart(String INPUT_ID) throws Exception  {
		boolean result;
		List<String> actions = new ArrayList<String>();
		actions.add("Started");
		actions.add("Auto Approval");
		actions.add("Bank Transmission OK");
		actions.add("Bank Rejection");
		System.out.println(actions);
		
	fDesign= new Fsn_Design_Page(driver);
	try 
	{
		Thread.sleep(20000);
    } 
	catch (InterruptedException e) 
	{
		e.printStackTrace();
	}
	 
		fDesign.onClick(INPUT_ID);
	
	result= fDesign.testIfExists(actions, driver);
	if(result)
		System.out.println("Validation Passed");
	else
		System.out.println("Validation Failed");
	}
	}