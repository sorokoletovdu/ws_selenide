package com.example.demo;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class CartPage {
    public SelenideElement payWithCardButton = $x("//button/span[text()=\"Pay with Card\"]");
    public SelenideElement email = $x("//input[@placeholder=\"Email\"]");
    public SelenideElement pan = $x("//input[@placeholder=\"Card number\"]");
    public SelenideElement expireDate = $x("//input[@placeholder=\"MM / YY\"]");
    public SelenideElement cvc = $x("//input[@placeholder=\"CVC\"]");
    public SelenideElement zip = $x("//input[@placeholder=\"ZIP Code\"]");
    public SelenideElement submitButton = $x("//button[@id=\"submitButton\"]");
    public SelenideElement succesfulPaymentIndicator = $x("//div[@class=\"button submit success\"]");
    public SelenideElement getSubmitButtonText = $x("//button[@id=\"submitButton\"]/span/span");
    public SelenideElement totalSumText = $x("//*[@id=\"total\"]");
}
