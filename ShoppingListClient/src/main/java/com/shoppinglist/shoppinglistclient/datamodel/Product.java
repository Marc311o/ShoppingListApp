package com.shoppinglist.shoppinglistclient.datamodel;

import java.io.Serializable;

public class Product implements Serializable {

    private String name, category, unit, type;
    private double amount;
    private int quantity;

    public Product(String name, String category, String unit, String typ, double amount) {
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.amount = amount;
        this.type = typ;
        this.quantity = (int) amount;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return this.amount;
    }

    String getType() {
        return this.type;
    }

    String getName() {
        return this.name;
    }

    String getCategory() {
        return this.category;
    }

    String getUnit() {
        return this.unit;
    }


}

