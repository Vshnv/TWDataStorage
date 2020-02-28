package com.rocketmail.vaishnavanil.test;

import com.rocketmail.vaishnavanil.annotations.Saveable;

import java.util.UUID;
@Saveable.SaveableClass(tableName = "testPlayers")
public class TestPlayer {
    @Saveable.SaveableField(name = "pid",type = "varchar(32)")
    String uuid = UUID.randomUUID().toString().substring(0,32);

    @Saveable.SaveableField(name = "level",type = "int")
    int level = 15;




    int legs = 45;
}
