package com.rocketmail.vaishnavanil.handlers;

import com.rocketmail.vaishnavanil.annotations.Saveable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

public abstract class DataHandler {

   public abstract void save(String key,Object obj);
   public abstract void load(String key,Object obj);
}
