package com.rocketmail.vaishnavanil.test;

import com.rocketmail.vaishnavanil.annotations.Saveable;

import java.io.Serializable;

@Saveable.SaveableClass(tableName = "TedtTable")
public class Test implements Serializable {
    @Saveable.SaveableField(name = "id",type = "int")
    private int id;
    @Saveable.SaveableField(name = "name",type = "varchar(10)")
    private String name;


    public Test(int id,String n){
        this.id = id;
        this.name = n;
    }
}
