package com.example.demo;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.exist;

import static com.codeborne.selenide.Selenide.*;

public class MainPageTest {
    MainPage mainPage = new MainPage();
    GoodsPage goodsPage = new GoodsPage();
    CartPage cartPage = new CartPage();
    FinalPage finalPage = new FinalPage();

    @BeforeClass
    public static void setUpAll() {
        Configuration.browserSize = "1280x800";
//        SelenideLogger.addListener("allure", new AllureSelenide());
        Configuration.remote = "http://localhost:4444/wd/hub";
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("enableVNC", true);
        capabilities.setCapability("enableVideo", false);
        Configuration.browserCapabilities = capabilities;
        Configuration.screenshots = false;
        Configuration.headless = false;
        Configuration.pageLoadStrategy = "eager";
        Configuration.savePageSource = false;
    }


    @Test
    public void e2e_chrome() throws Exception {
        Configuration.browser = "chrome";
        open("https://weathershopper.pythonanywhere.com/");
        System.out.println("Test running in chrome with thread_id = " + Thread.currentThread().getId());
        $("title").shouldHave(attribute("text", "Current Temperature"));

        // Get current temperature
        String temperatureString = mainPage.temperatureField.getOwnText();


        // Determinate condition depends on current temperature and proceed purchase
        if (Integer.parseInt(temperatureString) < 19) {
            mainPage.buyMoisturizersButton.click();
            makePurchase("moisturizers");

        } else if (Integer.parseInt(temperatureString) > 34) {
            mainPage.buySunscreensButton.click();
            makePurchase("sunscreens");
        } else {
            throw new Exception("There is no requirements for temperature range from 19 to 34");
        }
    }

    @Test
    public void e2e_firefox() throws Exception {
        Configuration.browser = "firefox";
        open("https://weathershopper.pythonanywhere.com/");
        System.out.println("Test running in firefox with thread_id = " + Thread.currentThread().getId());
        $("title").shouldHave(attribute("text", "Current Temperature"));

        // Get current temperature
        String temperatureString = mainPage.temperatureField.getOwnText();


        // Determinate condition depends on current temperature and proceed purchase
        if (Integer.parseInt(temperatureString) < 19) {
            mainPage.buyMoisturizersButton.click();
            makePurchase("moisturizers");

        } else if (Integer.parseInt(temperatureString) > 34) {
            mainPage.buySunscreensButton.click();
            makePurchase("sunscreens");
        } else {
            throw new Exception("There is no requirements for temperature range from 19 to 34");
        }
    }


    /**
     * Processes the purchase depends on product type
     * @param condition - product type
     */
    public void makePurchase(String condition) throws Exception {

        Map<String, Map<Integer, String>> goodsByTypes = getAllGoods(condition);

        String firstGood = "";
        String secondGood = "";

        // Get the first good
        firstGood = getLeastExpensiveItem(goodsByTypes.get("first"));
        System.out.println("The first chose good = " + makeExpectedGoodMapItem(firstGood).get("name") +
                " with price = " + makeExpectedGoodMapItem(firstGood).get("price"));

        // Get the second good
        secondGood = getLeastExpensiveItem(goodsByTypes.get("second"));
        System.out.println("The second chose good = " + makeExpectedGoodMapItem(secondGood).get("name") +
                " with price = " + makeExpectedGoodMapItem(secondGood).get("price"));

        // Put the first good into cart
        $x("//*[@onclick=\"" + firstGood + "\"]").click();
        goodsPage.goToCartButtonText.shouldHave(exactOwnText("1 item(s)"));

        // Put the second good into cart
        $x("//*[@onclick=\"" + secondGood + "\"]").click();
        goodsPage.goToCartButtonText.shouldHave(exactOwnText("2 item(s)"));

        // Go to the cart
        goodsPage.goToCartButton.click();

        $("title").shouldHave(attribute("text", "Cart Items"));

        // Check the total sum
        Integer actualTotalSum = getActualTotalSum(firstGood, secondGood);

        // Go to payment window
        cartPage.payWithCardButton.click();
        switchTo().frame("stripe_checkout_app");

        // Check the submitted sum equal the total sum on the cart page
        /*Integer submittedSum = Integer.parseInt(cartPage.getSubmitButtonText.getOwnText()
                .split(" ")[1]
                .split(",")[0]);

        Assert.assertEquals(actualTotalSum, submittedSum);*/

        // Input payment data

        // Input email
        cartPage.email.should(exist);
        cartPage.email.sendKeys("test@test.test");
        sleep(500);

        //Input card number
        cartPage.pan.should(exist);
        cartPage.pan.sendKeys("5105");
        cartPage.pan.sendKeys("1051");
        cartPage.pan.sendKeys("0510");
        cartPage.pan.sendKeys("5100");
        sleep(500);

        //Input expire date
        cartPage.expireDate.should(exist);
        cartPage.expireDate.sendKeys("04");
        cartPage.expireDate.sendKeys("25");
        sleep(500);

        // Input CVC
        cartPage.cvc.should(exist);
        cartPage.cvc.val("123");

        // Input ZIP Code
        cartPage.zip.should(exist);
        cartPage.zip.val("12345");


        // Submit payment
        cartPage.submitButton.click();

        //Check payment proceeded, successfully
        cartPage.succesfulPaymentIndicator.should(exist);

        // Check order confirmation
        sleep(1000);
        finalPage.orderConfirmation.shouldBe(visible);
        if(finalPage.orderConfirmation.getOwnText().equals(finalPage.success)) {
            String header = $x("//h2").getOwnText();
            Assert.assertEquals(header, "PAYMENT SUCCESS");
        }else if (finalPage.orderConfirmation.getOwnText().equals(finalPage.fail) ) {
            String header = $x("//h2").getOwnText();
            Assert.assertEquals(header, "PAYMENT FAILED");
            System.out.println("Customer didn't receive the payment! There is 5% chance!");
        }else {
            throw new Exception("TEST FAILED IN UNEXPECTED CONDITION");
        }

    }

    /**
     * Puts all goods from goods page depends on condition - type of products depends on temperature
     * @param condition - type of products
     * @return - map with a map of the first and a map of the second types of goods
     */
    public Map<String, Map<Integer, String>> getAllGoods(String condition) throws Exception {

        Map<String, Map<Integer, String>> goodsByTypes = new HashMap<>();

        Map<Integer, String> firstTypeGoods = new HashMap<>();
        Map<Integer, String> secondTypeGoods = new HashMap<>();

        String firstGoodCondition = "";
        String secondGoodCondition = "";

        // Determinate condition of the first and the second types of goods depends on product types
        switch (condition) {
            case "moisturizers":
                firstGoodCondition = "^.* [A|a]loe .*$";
                secondGoodCondition = "^.* [A|a]lmond .*$";
                break;
            case "sunscreens":
                firstGoodCondition = "^.* [S|s][P|p][F|f]-50.*$";
                secondGoodCondition = "^.* [S|s][P|p][F|f]-30.*$";
                break;
            default:
                throw new Exception("Unexpected purchase type");
        }

        // Get all elements with the "Add" button
        ElementsCollection allGoods = $$x("//button[text() = 'Add']");

        for (SelenideElement item : allGoods) {

            String itemAttribute = item.getAttribute("onclick");

            //Check the condition and prepare a map with two goods
            if (itemAttribute != null && itemAttribute.matches(firstGoodCondition)) {
                firstTypeGoods = makeItemsArray(firstTypeGoods, itemAttribute);
                goodsByTypes.put("first", firstTypeGoods);
            } else if (itemAttribute != null && itemAttribute.matches(secondGoodCondition)) {
                secondTypeGoods = makeItemsArray(secondTypeGoods, itemAttribute);
                goodsByTypes.put("second", secondTypeGoods);
            }
        }
        return goodsByTypes;
    }

    /**
     * Prepares key=price and value=good_attribute values from good attribute string  and adds it into map
     * @param itemsArray - map with all goods
     * @param itemAttribute - good attribute string
     * @return itemsArray - added map
     */
    public Map<Integer, String> makeItemsArray(Map<Integer, String> itemsArray, String itemAttribute) {
        String[] itemKeyValue = itemAttribute.split(",");
        String keyString = itemKeyValue[itemKeyValue.length - 1];
        itemsArray.put(Integer.parseInt(keyString.substring(0, keyString.length() - 1)), itemAttribute);
        return itemsArray;
    }

    /**
     * Finds the least expensive good by sorting a source map by key (price) and getting the first element
     * @param unsortedArray - source map
     * @return - the least expensive item
     */
    public String getLeastExpensiveItem(Map<Integer, String> unsortedArray) {
        Map<Integer, String> sortedAloeItems = new TreeMap<Integer, String>(unsortedArray);
        Map.Entry<Integer, String> leastExpensiveItem = sortedAloeItems.entrySet()
                .stream()
                .findFirst()
                .get();
        return leastExpensiveItem.getValue();
    }

    /**
     * Makes a map from a string containing the "name" and the "price" elements
     * @param goodString - string with name and price
     * @return expectedGood - map containing the "name" and the "price" elements
     */
    public Map<String, Object> makeExpectedGoodMapItem(String goodString) {
        Map<String, Object> expectedGood = new HashMap<>();
        String cleanedGoodString = goodString.substring(10, goodString.length() - 1);
        String[] goodArrayRaw = cleanedGoodString.split(",");
        String rawName = goodArrayRaw[0].trim();
        expectedGood.put("name", rawName.substring(1, rawName.length() - 1));
        expectedGood.put("price", Integer.parseInt(goodArrayRaw[1].trim()));
        return expectedGood;
    }

    /**
     * Returns an actual total sum calculated as the value of the total sum element on the cart page.
     * Also, checks that the value of the total sum element on the cart page equals the sum of the values in the price column.
     * @param firstGood - the first good in the cart
     * @param secondGood - the second good in the cart
     * @return actualTotalSum
     */
    public Integer getActualTotalSum(String firstGood, String secondGood) {
        Map<Integer, Map<String, Object>> goodsInOrderArray = new HashMap<>();
        Map<Integer, Map<String, Object>> expectedGoodsInOrderArray = new HashMap<>();

        // Check that the order consists of only  two elements
        ElementsCollection goodsInOrder = $$x("//tbody/tr");
        Assert.assertEquals(goodsInOrder.size(), 2);

        // Parse values from the "Price", and the "Item" columns for both elements and put elements into a map
        int orderPositionKey = 0;
        for (SelenideElement orderPosition : goodsInOrder) {
            Map<String, Object> good = new HashMap<>();
            List<SelenideElement> columns = orderPosition.findAll(By.tagName("td"));
            good.put("name", columns.get(0).getOwnText());
            good.put("price", Integer.parseInt(columns.get(1).getOwnText()));
            goodsInOrderArray.put(orderPositionKey, good);
            orderPositionKey += 1;
        }

        // Prepare a map with price values, and name values for both goods obtained in the previous step
        expectedGoodsInOrderArray.put(0, makeExpectedGoodMapItem(firstGood));
        expectedGoodsInOrderArray.put(1, makeExpectedGoodMapItem(secondGood));

        // Check goods obtained in the previous step and goods in the cart are the same
        Assert.assertEquals(expectedGoodsInOrderArray, goodsInOrderArray);

        Integer actualTotalSum = Integer.parseInt(cartPage.totalSumText.getOwnText().split(" ")[2]);
        Integer expectedTotalSum = (int) goodsInOrderArray.get(0).get("price") + (int) goodsInOrderArray.get(1).get("price");

        // Check that sums are equal
        Assert.assertEquals(expectedTotalSum, actualTotalSum);

        return actualTotalSum;
    }
}
