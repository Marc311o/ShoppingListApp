package com.shoppinglist.shoppinglistclient.datamodel;

import java.util.ArrayList;

public class Category {

    String name;
    ArrayList<Product> products = new ArrayList<>();

    public Category(String name) {
        this.name = name;
    }
}
