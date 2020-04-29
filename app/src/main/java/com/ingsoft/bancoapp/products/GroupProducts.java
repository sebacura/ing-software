package com.ingsoft.bancoapp.products;

import java.util.ArrayList;
import java.util.List;

public class GroupProducts {

    public String title;
    public String description;
    public int image;
    public final List<String> children = new ArrayList<String>();

    public GroupProducts( int image,String title, String description) {
        this.title = title;
        this.description = description;
        this.image = image;
    }

}