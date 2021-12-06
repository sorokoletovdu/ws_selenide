package com.example.demo;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class FinalPage {
    public SelenideElement orderConfirmationSuccess = $x("//p[text()=\"Your payment was successful. You should receive a follow-up call from our sales team.\"]");
    public SelenideElement orderConfirmationFail = $x("//p[text()=\"Oh, oh! Your payment did not go through. Please bang your head against a wall, curse the software gods and then try again.\"]");
    public SelenideElement orderConfirmation = $x("//p[@class=\"text-justify\"]");
    public  String success = "Your payment was successful. You should receive a follow-up call from our sales team.";
    public String fail = "Oh, oh! Your payment did not go through. Please bang your head against a wall, curse the software gods and then try again.";
}
