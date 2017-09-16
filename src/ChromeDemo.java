import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created by Yashraj Mahida on 8/2/2017.
 */
public class ChromeDemo {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver","C:\\Users\\Yashraj Mahida\\Downloads\\selenium\\driver\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        String baseURL = "http://www.google.com";

        driver.get(baseURL);
    }
}
