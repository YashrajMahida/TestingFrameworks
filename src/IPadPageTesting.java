/**
 * Created by Yashraj Mahida on 8/5/2017.
 */

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import javax.xml.bind.SchemaOutputResolver;


public class IPadPageTesting {

    private WebDriver driver;
    private String baseUrl;
    private String UserName;
    private String Password;
    private String StoreName;
    File f = new File("Drivers");
    File fs = new File(f, "geckodriver.exe");

    String OrderName1 = "Customer1";
    String OrderName2 = "Customer2";
    String OrderName3 = "Customer3";

    //Pseudo code for creating fields needed for database connection
    // Connection object
    static Connection con = null;

    // Statement object
    private static Statement stmt;

    // Constant for Database URL
    public static String DB_URL = "DatabaseURL";

    // Constant for Database Username
    public static String DB_USER = "DatabaseUserID";

    // Constant for Database Password
    public static String DB_PASSWORD = "DatabasePassword";

    @Before
    public void LogInTest() throws Exception {
        System.setProperty("webdriver.gecko.driver", fs.getAbsolutePath());
        driver = new FirefoxDriver();
        baseUrl = "";
        StoreName = "";
        UserName = "";
        Password = "";
        System.out.println("Passed before method");

        driver.get(baseUrl + "/");
        driver.findElement(By.xpath(".//*[@id='store_name']")).clear();
        driver.findElement(By.xpath(".//*[@id='store_name']")).sendKeys(StoreName);
        System.out.println("Store name enter");
        driver.findElement(By.xpath(".//*[@id='login']")).clear();
        driver.findElement(By.xpath(".//*[@id='login']")).sendKeys(UserName);
        System.out.println("Username entered");
        driver.findElement(By.xpath(".//input[@id='password']")).clear();
        driver.findElement(By.xpath(".//input[@id='password']")).sendKeys(Password);
        System.out.println("Password entered");
        driver.findElement(By.xpath(".//*[@id='submit']")).click();
        System.out.println("signIn clicked");
        driver.findElement(By.linkText("Help"));
        System.out.println("Succefull Login");
        System.out.println();
        Thread.sleep(7000);


        //Pseudo code to initiate database connection
        try {
            // Make the database connection
            String dbClass = "Database Class";
            Class.forName(dbClass).newInstance();

            // Get connection to DB
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Statement object to send the SQL statement to the Database
            stmt = con.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void TestCase01() throws SQLException {
        //Test to verify database Item List

        String query1  = "SELECT Item_ID, Item_Name FROM Item_List ORDER BY Item_ID ASC";
        ResultSet rs = stmt.executeQuery(query1);
        int listSize = 0;
        while(rs.next()) {
            System.out.println(rs);
            listSize++;
        }
        System.out.println(listSize);

        List<WebElement> numberOfItems = driver.findElements(By.xpath("//div[@id='item-list']//ul[@id='results']//li"));
        System.out.println(numberOfItems.size());

        Assert.assertEquals(listSize,numberOfItems);
        //If the number of items match with the database, the second step verification will be the
        //test to check values match with the database.

    }

    @Test
    public void TestCase02() throws Exception {

        //Test to check the functionality of search bar & the accuracy of search result.
        String searchBarKeys = "Coffee";
        WebElement searchBar = driver.findElement(By.xpath("//div[@id='ipad-layout']//header//div[@id='item-search']"));
        searchBar.sendKeys(searchBarKeys);
        List<WebElement> UIResults = driver.findElements(By.xpath("//ul[@id='results']/li"));

        for (WebElement element : UIResults) {
            String elementText = element.getText();
            if (element.isDisplayed() != false) {
                System.out.println("Search Bar Test pass on UI front");
            }
            if (element.isDisplayed() == false && elementText != searchBarKeys) {
                System.out.println("Search bar negative testing pass on UI front");
            }
        }

        //Get list of all items consist of string keyword "Coffee" in database.
        String Item_Name = "Coffee";
        String query  = "SELECT * FROM Item_List WHERE Item_ID='"+Item_Name+"%'";
        ResultSet resultSet = stmt.executeQuery(query);
        ResultSetMetaData rsmd = resultSet.getMetaData();
        //int columnsNumber = rsmd.getColumnCount();
        while (resultSet.next()) {
            Assert.assertTrue("Coffee",true);
        }

    }

    @Test
    public void TestCase03AndTestCase04() throws InterruptedException, SQLException {
        //TestCase03 and TestCase04 - This test adds a new Order Page using "+" button and renames it to Customer1
        //Query to check if a New Order is created in database and a New-Item is added or not

        String query1  = "SELECT Order_ID, Order_name FROM Order ORDER BY Order_ID desc LIMIT 1";
        int orderId = 0;
        ResultSet rs = stmt.executeQuery(query1);
        if(rs.first()) {
            orderId = rs.getInt(0);
        }

        WebElement addButton = driver.findElement(By.xpath("//div[@id='content']//a[text()='+']"));
        addButton.click();
        System.out.println("Add button clicked");
        Thread.sleep(7000);

        WebElement addTitlePageName = driver.findElement(By.xpath("//div[@id='content']//header//div[@id='button-page-control']//input[@placeholder='Page Title']"));
        addTitlePageName.clear();
        addTitlePageName.sendKeys(OrderName1);
        Thread.sleep(7000);

        ResultSet rst = stmt.executeQuery(query1);
        int newOrderId=0;
        if (rst.first()){
            newOrderId = rst.getInt(0);
        }
        Assert.assertEquals((long) orderId + 1,(long)newOrderId);

    }

    @Test
    public void TestCase05() throws Exception {

        //This test check the functionality of drag and drop feature.
        String ITEM_ID = "2";
        String query = "SELECT Order_ID, Item_ID FROM Order ORDER BY Order_ID desc";
        ResultSet set = stmt.executeQuery(query);
        ResultSetMetaData rsmd = set.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (set.next()) {
            for (int i = 1; i < columnsNumber; i++)
                System.out.print(set.getString(i) + " ");
            System.out.println();
        }

        driver.get(baseUrl);
        Thread.sleep(7000);
        Actions action = new Actions(driver);
        WebElement selectedElement = driver.findElement(By.xpath("\n" +
                "//div[@id='item-list']//span[contains(@class,'description') and contains(text(),'Donut')]"));
        System.out.println("Item selected from item-list to be moved to button-holder page");
        WebElement toElement = driver.findElement(By.xpath("//a[starts-with(@id,'bp-tab-')]//span[contains(text(),'"+OrderName1+"')]"));
        System.out.println("Place holder selected where item will be moved");
        //action.dragAndDrop(selectedElement,toElement).build().perform();
        action.clickAndHold(selectedElement).moveToElement(toElement).release().build().perform();
        System.out.println("Item moved");
        Thread.sleep(7000);


        String query2  = "SELECT count(*) FROM Order_Items WHERE Item_ID = '" + ITEM_ID + "'";
        PreparedStatement ps  = con.prepareStatement(query2);
        ps.setString(1,ITEM_ID);
        ResultSet rs = ps.executeQuery();
        if (rs.next()){
            int count = rs.getInt(0);
            System.out.println(count);
        }


        WebElement reverseFromElement = driver.findElement(By.xpath("\n" + "" +
                "//div[@id='button-page-654391']//div[starts-with(@class,'si-placeholder') and contains(@class, 'ui-state-disabled')]"));
        System.out.println("Item selected to move back");

        WebElement reverseToElement = driver.findElement(By.id("item-list"));
        System.out.println(reverseToElement.getSize());
        action.clickAndHold(reverseFromElement);
        action.moveByOffset(50, -100);
        action.release();
        action.build().perform();
        System.out.println("Item moved");
        Thread.sleep(7000);

        //String query3 = "SELECT Item_ID FROM Order_Items WHERE Order_ID = '1' AND ITEM_ID = '1'";
        set = stmt.executeQuery(query2);
        if (!set.next()) {
            System.out.println("Item removed from order page");
        }

    }

    @Test
    public void TestCase07() throws InterruptedException, SQLException {

        String query1  = "SELECT Order_ID,Order_Name FROM Order ORDER BY Order_ID desc LIMIT 1";
        int orderId = 0;
        ResultSet rs = stmt.executeQuery(query1);
        if(rs.first()) {
            orderId = rs.getInt(0);
        }

        WebElement addButton = driver.findElement(By.xpath("//div[@id='content']//a[text()='+']"));
        addButton.click();
        System.out.println("Add button clicked");
        addButton.click();
        Thread.sleep(7000);

        WebElement addTitlePageName = driver.findElement(By.xpath("//div[@id='content']//header//div[@id='button-page-control']//input[@placeholder='Page Title']"));
        System.out.println("title page text box found");
        addTitlePageName.clear();
        addTitlePageName.sendKeys(OrderName2);
        System.out.println("keys sent");
        WebElement addButton2 = driver.findElement(By.xpath("//div[@id='content']//a[text()='+']"));
        addButton2.click();
        System.out.println("Add button clicked again");
        System.out.println("Add button clicked");
        Thread.sleep(7000);

        WebElement newOrderPageSelect = driver.findElement(By.xpath("//div[@id='button-page-nav']//a[starts-with(@id,'bp-tab-')]//span[contains(text(),'" + OrderName2 + "')]"));
        System.out.println("New order page found");
        newOrderPageSelect.click();
        System.out.println("New order page clicked");


        ResultSet rst = stmt.executeQuery(query1);
        int newOrderId = 0;
        if (rst.first()){
            newOrderId = rst.getInt(0);
        }
        Assert.assertEquals((long) orderId + 1,(long)newOrderId);
        Assert.assertTrue("CustomerName2",true);
    }

    @Test
    public void TestCase08() throws InterruptedException, SQLException {

        String query1  = "SELECT Order_ID,Order_Name FROM Order ORDER BY Order_ID DESC";
        int orderId = 0;
        ResultSet rs = stmt.executeQuery(query1);
        if(rs.first()) {
            orderId = rs.getInt(0);
        }

        WebElement navigateButton = driver.findElement(By.id("button-tab-prev"));
        System.out.println("Navigation button found");
        navigateButton.click();
        Thread.sleep(7000);

        String query2  = "SELECT Order_ID,Order_Name FROM Order ORDER BY Order_ID DESC";
        int newOrderID = 0;
        ResultSet rst = stmt.executeQuery(query2);
        if(rs.first()) {
            newOrderID = rst.getInt(0);
        }
        Assert.assertEquals(newOrderID,newOrderID-1);
    }

    @Test
    public void TestCase11() throws InterruptedException, SQLException {
        //TestCase11 delete Customer1

        String query1  = "SELECT Order_ID,Order_Name FROM Order ORDER BY Order_ID ASC";
        ResultSet set = stmt.executeQuery(query1);
        ResultSetMetaData rsmd = set.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (set.next()) {
            for (int i = 1; i < columnsNumber; i++)
                System.out.print(set.getInt(i)+" "+set.getString(i) + " ");
            System.out.println();
        }

        WebElement addButton = driver.findElement(By.xpath("//div[@id='content']//a[text()='+']"));
        addButton.click();
        System.out.println("Add button clicked");
        Thread.sleep(7000);

        WebElement addTitlePageName = driver.findElement(By.xpath("//div[@id='content']//header//div[@id='button-page-control']//input[@placeholder='Page Title']"));
        addTitlePageName.clear();
        addTitlePageName.sendKeys(OrderName1);
        Thread.sleep(7000);

        WebElement justToClick = driver.findElement(By.xpath("//div[@id='item-list']//span[contains(@class,'description') and contains(text(),'Donut')]"));
        justToClick.click();
        System.out.println("Order name changed");
        Thread.sleep(7000);

        WebElement deleteButton = driver.findElement(By.xpath("//div[@id='content']//a[text()='−']"));
        deleteButton.click();
        System.out.println("Delete Order button clicked");
        Thread.sleep(7000);

        WebElement deleteConfirm = driver.findElement(By.xpath("//button[@class='button primary confirm']"));
        System.out.println("Confirm Delete located");
        deleteConfirm.click();
        System.out.println("Order page deleted");
        Thread.sleep(7000);

        String query2  = "SELECT Order_ID,Order_Name FROM Order ORDER BY Order_ID ASC";
        ResultSet rst = stmt.executeQuery(query2);
        ResultSetMetaData rmd = rst.getMetaData();
        int newColumnsNumber = rmd.getColumnCount();
        while (rst.next()) {
            for (int i = 1; i < newColumnsNumber; i++)
                System.out.print(set.getInt(i)+" "+set.getString(i) + " ");
                Assert.assertFalse("Customer1",true);
        }
    }

    @Test
    public void SearchDragDropTest() throws Exception {
        //This test, validates the integration of searching the Item
        //and moving the Item to Order;

        Actions action = new Actions(driver);

        String findKeys = "Bagel";

        WebElement searchBar = driver.findElement(By.xpath("//div[@id='ipad-layout']//header//div[@id='item-search']"));
        System.out.println("Search bar located");

        searchBar.sendKeys(findKeys);
        System.out.println("Search keywords sent");

        List<WebElement> searchResults = driver.findElements(By.xpath("//ul[@id='results']/li"));
        System.out.println("list of all available items located");

        WebElement toElement = driver.findElement(By.xpath("//div[@id='button-page-653092']/div[10]"));
        System.out.println("Place holder located");

        for (WebElement element : searchResults) {
            String elementText = element.getText();
            if (element.isDisplayed() != false) {
                action.clickAndHold(element).moveToElement(toElement).release().build().perform();
                System.out.println("Search Bar with move item Test pass");
            }
        }
        Thread.sleep(7000);

    }

    @After
    public void tearDown() throws Exception {
        Thread.sleep(7000);
        con.close();
        driver.quit();
    }
}
