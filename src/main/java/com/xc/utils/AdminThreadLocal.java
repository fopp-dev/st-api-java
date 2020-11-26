package com.xc.utils;

import com.xc.pojo.SiteAdmin;

public class AdminThreadLocal {

    private static ThreadLocal<SiteAdmin> adminThreadLocal =  new ThreadLocal<SiteAdmin>();

    public static void set(SiteAdmin siteAdmin){

        adminThreadLocal.set(siteAdmin);

    }

    public static SiteAdmin get(){

        return adminThreadLocal.get();

    }

    public static void remove(){

        //防止内存泄漏

        adminThreadLocal.remove();

    }

}
