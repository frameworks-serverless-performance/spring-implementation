package com.test.api.models;

public class PriceRequestResponse {

    private String itemId;
    private int quantity;
    private int perItemPrice;
    private int totalPricePreTax;
    private float taxRate;
    private int totalPriceWithTax;

    public void calculatePrices() {
        totalPricePreTax = quantity * perItemPrice;
        totalPriceWithTax = Math.round(totalPricePreTax * (1 + taxRate));
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setPerItemPrice(int perItemPrice) {
        this.perItemPrice = perItemPrice;
    }

    public int getPerItemPrice() {
        return perItemPrice;
    }

    public void setTotalPricePreTax(int totalPricePreTax) {
        this.totalPricePreTax = totalPricePreTax;
    }

    public int getTotalPricePreTax() {
        return totalPricePreTax;
    }

    public void setTaxRate(float taxRate) {
        this.taxRate = taxRate;
    }

    public float getTaxRate() {
        return taxRate;
    }

    public void setTotalPriceWithTax(int totalPriceWithTax) {
        this.totalPriceWithTax = totalPriceWithTax;
    }

    public int getTotalPriceWithTax() {
        return totalPriceWithTax;
    }
}
