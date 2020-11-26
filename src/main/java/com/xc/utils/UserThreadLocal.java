package com.xc.utils;


import com.xc.pojo.User;

public class UserThreadLocal {

    private static ThreadLocal<User> userThreadLocal =  new ThreadLocal<User>();

    public static void set(User user){

        userThreadLocal.set(user);

    }

    public static User get(){

        return userThreadLocal.get();

    }

    public static void remove(){

        //防止内存泄漏

        userThreadLocal.remove();

    }

}
