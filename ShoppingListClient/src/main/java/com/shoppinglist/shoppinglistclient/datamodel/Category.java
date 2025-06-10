package com.shoppinglist.shoppinglistclient.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {

    String name;
    ArrayList<Product> products = new ArrayList<>();

    public Category(String name) {
        this.name = name;
    }
}
