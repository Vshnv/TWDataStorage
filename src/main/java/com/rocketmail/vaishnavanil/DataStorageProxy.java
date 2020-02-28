package com.rocketmail.vaishnavanil;

import com.rocketmail.vaishnavanil.handlers.DataHandler;
import com.rocketmail.vaishnavanil.handlers.SQLHandler;
import com.rocketmail.vaishnavanil.test.Test;
import com.rocketmail.vaishnavanil.test.TestPlayer;

public class DataStorageProxy {
    public static void main(String[] args) {
        TestPlayer p = new TestPlayer();
        DataHandler tp  = new SQLHandler("localhost","root","19BCG10015","3306","wilddatatest",TestPlayer.class);
        tp.save("test",p);

    }
}
