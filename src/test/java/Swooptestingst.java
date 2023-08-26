import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class Swooptestingst {
    WebDriver driver;

    WebDriverWait wait;
    @BeforeTest
    @Parameters("browser")
    public void startTest(@Optional("chrome") String browser) throws Exception{
        if (browser.equalsIgnoreCase("chrome")){
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        } else if (browser.equalsIgnoreCase("Edge")){
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();
        } else {
            throw new Exception("Browser is not correct");
        }


        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        String url = "https://www.swoop.ge/";
        driver.get(url);
    }
    @Test
    public  void testSwoop(){
        //       - Go to 'კინო'
        driver.findElement(By.linkText("კინო")).click();

        //        - Select the first movie in the returned list and click on ‘ყიდვა’ button

        WebElement movies = driver.findElement(By.xpath("//div[@class='movies-deal']"));
        List<WebElement> moviesList =movies.findElements(By.xpath("//div[@class='movies-deal']"));
        WebElement firstMovie = moviesList.get(0); //იმ შემთხვევაში თუ პირველ ფილმს არ აქვს ისთ ფოინთი შვცვალოთ ინდექსი
        Actions actions = new Actions(driver);
        actions.moveToElement(firstMovie).perform();
        WebElement clickButton = firstMovie.findElement(By.className("info-cinema-ticket"));
        clickButton.click();



        // - Scroll vertically (if necessary), and horizontally and choose ‘კავეა ისთ ფოინთი’
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, 500)");

//           - Check that only ‘კავეა ისთ ფოინთი’ options are returned
        WebElement eastPoint = driver.findElement(By.id("ui-id-6"));
        Actions caveaEastPointSection = new Actions(driver);
        caveaEastPointSection.moveToElement(eastPoint).click().build().perform();

        wait = new WebDriverWait(driver, 10);
        WebElement checkCavea = wait.until((ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"ui-id-6\"]"))));

        if (checkCavea.getText().equals("კავეა ისთ ფოინთი")) {
            System.out.println("pased");
        }else{
            System.out.println("failed");
        }

        //        - Click on last date
        WebElement daysListElement = driver.findElement(By.xpath("//div[@id='384933']//div[@class='calendar-tabs ui-tabs ui-widget ui-widget-content ui-corner-all']"));

        WebElement lastDayList = daysListElement.findElements(By.tagName("li")).get(daysListElement.findElements(By.tagName("li")).size() - 1);
        lastDayList.click();

//        and then click on last option

        WebElement date = lastDayList.findElement(By.xpath("//div[@id='384933']//div[@class='calendar-tabs ui-tabs ui-widget ui-widget-content ui-corner-all']"));
        WebElement lastSession = date.findElement(By.xpath("div[last()]"));
        lastSession.click();

//           - Check in opened popup that movie name, cinema and datetime is valid
//         ვამოწმებთ ვალიდურობას


        String popTitle = driver.findElement(By.className("name")).getText();
        String popCinemaName = "კავეა ისთ ფოინთი";
        String poplDateTime = driver.findElement(By.xpath("//p[@class='movie-cinema'][2]")).getText();
        String actualTitle = driver.findElement(By.xpath("//p[@class='name']")).getText();
        String actualCinema = eastPoint.getText();

        Assert.assertEquals(actualCinema,popCinemaName);
        Assert.assertEquals(actualTitle,popTitle);



        //         - Choose any vacant place (Click the first free seat which is anable)

        List<WebElement> anableSeats = driver.findElements(By.xpath("//div[contains(@class,'seat free')]/div[@class='seat-new-part']"));
        if (anableSeats.get(0).isEnabled()) {
            anableSeats.get(0).click();
            System.out.println("აირჩია პირველივე თავისუფალი ადგილი");
        } else {
            driver.findElement(By.xpath("//input[@name='SeatIds[0]']//ancestor-or-self::div[@class='seat-new-part']")).click();
            System.out.println("თავისუფალი ადგილი არ არის ხელმისაწვდომი");
        }


//        - Register for a new account
//        going to the Registration
        WebElement registerButton =driver.findElement(By.xpath("//p[@class='register']"));
        wait.until(ExpectedConditions.elementToBeClickable(registerButton));
        registerButton.click();

        driver.findElement(By.id("pFirstName")).sendKeys("mariami");
        driver.findElement(By.id("pLastName")).sendKeys("Barbakadze");
        driver.findElement(By.id("pEmail")).sendKeys("mariamibarbagmail.com");
        driver.findElement(By.id("pPhone")).sendKeys("555-55-55-55");

        driver.findElement(By.id("pDateBirth")).sendKeys("11", "1", "2002");

        Select contactPersonGender = new Select(driver.findElement(By.xpath("//select[@name='Gender']")));
        contactPersonGender.selectByVisibleText("ქალი");



        // - Check that error message ‘მეილის ფორმატი არასწორია!’ is appear
        driver.findElement(By.xpath("//*[@id=\"register-content-1\"]/a/div")).click();
        WebElement newWb = driver.findElement(By.id("physicalInfoMassage"));
        wait.until(ExpectedConditions.elementToBeClickable(newWb));
        WebElement errorMessage = driver.findElement(By.xpath("//p[text()='მეილის ფორმატი არასწორია!']"));
        js.executeScript("arguments[0].scrollIntoView();", errorMessage);
        String messageText= errorMessage.getText();
        Assert.assertEquals(messageText, "მეილის ფორმატი არასწორია!" );

    }
    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }


}
