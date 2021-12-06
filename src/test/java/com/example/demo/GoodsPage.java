package com.example.demo;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class GoodsPage {
    public SelenideElement goToCartButtonText = $x("//*[@id=\"cart\"]");
    public SelenideElement goToCartButton = $x("//*[@onclick=\"goToCart()\"]");
}
