package com.example.demo;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class MainPage {
    public SelenideElement buyMoisturizersButton = $x("//button [text() = 'Buy moisturizers']");
    public SelenideElement buySunscreensButton = $x("//button [text() = 'Buy sunscreens']");
    public SelenideElement temperatureField = $("#temperature");
}
