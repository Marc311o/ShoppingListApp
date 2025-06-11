package com.shoppinglist.shoppinglistclient.datamodel;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Category {


    public String name;
    public ArrayList<Product> products = new ArrayList<>();

    public Category(String name) {
        this.name = name;
    }
}
