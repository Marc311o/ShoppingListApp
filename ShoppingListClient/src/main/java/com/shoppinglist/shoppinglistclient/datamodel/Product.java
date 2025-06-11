package com.shoppinglist.shoppinglistclient.datamodel;

import java.io.Serial;
import java.io.Serializable;

public class Product {


    public String name, category, unit, type;
    public double amount;
    public int quantity;

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

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setType(String type) {
        this.type = type;
    }
}

