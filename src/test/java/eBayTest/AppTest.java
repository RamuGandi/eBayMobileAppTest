package eBayTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.Keys;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.webdriven.commands.KeyEvent;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.android.AndroidKeyCode;
import resources.ExcelUtils;

public class AppTest {
	
	private static AndroidDriver driver;
	
	/*Variables to store Product information from ProductDetailsPage*/
	String productNameInPDP = null;
	String productDescriptionInPDP = null;
	String productPriceInPDP = null;
	/*Variables to store Product information from CartPage*/
	String productNameInCartPage = null;
	String productDescriptionInCartPage = null;
	String productPriceInCartPage = null;
	
	
	@BeforeTest()
	public void startApp() throws MalformedURLException, FileNotFoundException
	{
		DesiredCapabilities cap = new DesiredCapabilities();
		
		File targetApp = new File("C:\\Users\\Ramu\\Desktop\\Amazon_shopping.apk"); //APK file path 
			
		cap.setCapability("deviceName", "394ff933"); 
		cap.setCapability("platformName", "Android");
		cap.setCapability("app", targetApp.getAbsolutePath());
		cap.setCapability("appPackage", "com.amazon.mShop.android.shopping");
		cap.setCapability("appActivity", "com.amazon.mShop.home.HomeActivity");
		driver = new AndroidDriver<WebElement>(new URL("http://0.0.0.0:4723/wd/hub"), cap);
		driver.manage().timeouts().implicitlyWait(30, java.util.concurrent.TimeUnit.SECONDS);
		
		/*Screen Orientation check*/
		ScreenOrientation orientation = driver.getOrientation();
		System.out.println("By Default: "+orientation.value());
		if(orientation.value().contains("landscape"))
		{
			driver.rotate(ScreenOrientation.PORTRAIT);
			System.out.println("Orientation Changed to: "+driver.getOrientation().value());
		}
	}
	
	@Test(dataProvider = "testData")
	public void searchAndAddProductToCart(String sNo, String productToBeSearched) 
	{	
		
		System.out.println("****Executing test no: " + sNo + "****");
		
		driver.findElementById("com.amazon.mShop.android.shopping:id/sso_continue").click();
		driver.findElementById("com.amazon.mShop.android.shopping:id/rs_search_src_text").clear();
		driver.findElementById("com.amazon.mShop.android.shopping:id/rs_search_src_text").click();
		driver.findElementById("com.amazon.mShop.android.shopping:id/rs_search_src_text").sendKeys(productToBeSearched + "\n");		
		driver.findElementByXPath("(//*[@class='android.widget.ImageView'])[10]").click();
		/*Storing Product details from Product details page*/
		productNameInPDP = driver.findElementById("bylineInfo").getText();
		productDescriptionInPDP = driver.findElementById("title").getText();
		productPriceInPDP = driver.findElementById("ourPrice_availability").getText();
		
		System.out.println("PRODUCT NAME: " + productNameInPDP);
		System.out.println("PRODUCT DESCRIPTION: " + productDescriptionInPDP);
		System.out.println("PRODUCT PRICE: " + productPriceInPDP);
		
		/*Scrolling to Add to cart button*/
		String id = "add-to-cart-button";
		MobileElement el = (MobileElement) driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView("
				+ "new UiSelector().resourceIdMatches(\"" + id + "\"));");
		el.click();
		
		/*Clicking on cart Icon*/

//		driver.findElementByAccessibilityId("Cart");
		AndroidElement searchElement = (AndroidElement) new WebDriverWait(driver, 30).until(
		        ExpectedConditions.elementToBeClickable(MobileBy.AccessibilityId("Cart")));
		    searchElement.click();
	}

    @Test(dependsOnMethods = { "SearchAndAddProductToCart" })
    public void validateProductInCartPage()
    {
    	/*Storing Product details from Cart Page*/    	
    	productNameInCartPage = driver.findElementByXPath("(//*[@class='android.view.View'])[5]").getText();
    	productDescriptionInCartPage = driver.findElementByXPath("(//*[@class='android.view.View'])[5]").getText();
    	productPriceInCartPage = driver.findElementByXPath("(//*[@class='android.view.View'])[6]").getText();
    	
    	/*Validation of Product name, description and price from PDP and cart pages*/
    	try{
    	Assert.assertEquals(productNameInPDP, productNameInCartPage);
    	} catch (Exception e){
    		System.out.println("Failed at Product Name comparison with exception: "+ e);
    	}
    	try{
    	Assert.assertEquals(productDescriptionInPDP, productDescriptionInCartPage);
    	}catch (Exception e){
    		System.out.println("Failed at Product Description comparison with exception: "+ e);
    	} try {
    	Assert.assertEquals(productPriceInPDP, productPriceInCartPage);   
    	}catch (Exception e){
    		System.out.println("Failed at Product Price comparison with exception: "+ e);
    	}
    }
    
    @AfterTest
 	public void quit()
	{
		driver.quit();
	}
    
    @DataProvider(name = "testData")
    public Object[][] testData()
    {    	
    	ExcelUtils er = new ExcelUtils();
    	Object data[][] = er.readData();
		return data;
    	
    }   
    
}
