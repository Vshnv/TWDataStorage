package com.rocketmail.vaishnavanil.handlers;

import com.rocketmail.vaishnavanil.annotations.Saveable;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.HashMap;

import com.rocketmail.vaishnavanil.annotations.Saveable.SaveableField;
import com.rocketmail.vaishnavanil.annotations.Saveable.SaveableClass;

public class SQLHandler extends DataHandler {
    private String host,username,password,port,db;
    HashMap<String,?> cache;
    Class c;
    public SQLHandler(String host,String username,String password,String port,String db,Class type){
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
        this.db = db;
        c = type;
        setup();
        cache = new HashMap<String, Object>();
    }
    private void setup(){
        getConnection();
        Field[] f = c.getDeclaredFields();
        StringBuilder types = new StringBuilder();
        for(Field field:f){
            if(field.isAnnotationPresent(SaveableField.class)){
                types.append(field.getAnnotation(SaveableField.class).name());
                types.append(" ");
                types.append(field.getAnnotation(SaveableField.class).type());
                types.append(", ");
            }

        }
        types.setLength(types.length()-2);
        try {
            getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS "+((SaveableClass)c.getAnnotation(SaveableClass.class)).tableName()+" ( keyid varchar(32), "+types.toString()+")").execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeActive();
        }
    }
    public void save( String key,Object obj) {
        Class c = obj.getClass();
        Field[] f = c.getDeclaredFields();

        String table = ((SaveableClass)c.getAnnotation(SaveableClass.class)).tableName();
        if(keyExist(key,table)){
            try {
                StringBuilder statement = new StringBuilder("UPDATE ");
                statement.append(table);
                statement.append( " set ");
                for(Field field:f){
                    if(field.isAnnotationPresent(SaveableField.class)) {
                        boolean noAccess = !field.isAccessible();
                        try {
                            if (noAccess) field.setAccessible(true);
                            statement.append(field.getAnnotation(SaveableField.class).name() + "=");
                            statement.append("\"" + field.get(obj).toString() + "\",");
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } finally {
                            if (noAccess) field.setAccessible(false);
                        }
                    }
                }
                statement.setLength(statement.length()-1);
                statement.append(" where keyid=\""+key+"\"");
                PreparedStatement update = getConnection().prepareStatement(statement.toString());
                update.executeUpdate();


            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                closeActive();
            }
        }else{
            StringBuilder types = new StringBuilder();
            StringBuilder values = new StringBuilder();
            types.append("keyid , ");
            values.append("\""+key+"\"");
            values.append(", ");
            for(Field field:f){
                if(field.isAnnotationPresent(SaveableField.class)){
                    types.append(field.getAnnotation(SaveableField.class).name());
                    types.append(", ");
                    boolean noAccess = !field.isAccessible();
                    try {
                        if(noAccess)field.setAccessible(true);
                        Object val;
                        val = field.get(obj);

                            values.append("\""+(val)+"\"");

                        values.append(", ");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }finally {
                        if(noAccess)field.setAccessible(false);
                    }
                }

            }
            types.setLength(types.length()-2);
            values.setLength(values.length()-2);
            try {
                PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO " + table + " (" + types.toString()+") values (" + values.toString()+")" );
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                closeActive();
            }
        }


    }

    private boolean keyExist(String key,String table){
        try {
            PreparedStatement stmt = getConnection().prepareStatement("SELECT keyid FROM " + table + " where keyid =  ? LIMIT 0,1");
            stmt.setString(1,key);
            ResultSet  res = stmt.executeQuery();
            return res.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void load(String key,Object obj) {
        String table = ((SaveableClass)c.getAnnotation(SaveableClass.class)).tableName();
        try {
            StringBuilder types = new StringBuilder();
            for(Field field:c.getDeclaredFields()){
                if(field.isAnnotationPresent(SaveableField.class)){
                    types.append(field.getAnnotation(SaveableField.class).name());
                    types.append(", ");
                }

            }
            types.setLength(types.length()-2);
            PreparedStatement stmt = getConnection().prepareStatement("SELECT "+types.toString()+" FROM " + table + " where keyid =  ? LIMIT 0,1");
            stmt.setString(1,key);
            ResultSet res = stmt.executeQuery();
            if(res.next()){
                for(Field field:c.getDeclaredFields()){
                    if(field.isAnnotationPresent(SaveableField.class)){
                        boolean noAccess = !field.isAccessible();
                        try {
                            if(noAccess)field.setAccessible(true);
                            String name = field.getAnnotation(SaveableField.class).name();

                            field.set(obj,res.getObject(name));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }finally {
                            if(noAccess)field.setAccessible(false);
                        }
                    }

                }
            }else{
                closeActive();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private Connection active;
    private Connection getConnection(){
        if(active==null){
            try {
                active = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+ db,username,password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return active;
    }
    private void closeActive(){
        if(active!=null){
            try {
                active.close();
                active = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
