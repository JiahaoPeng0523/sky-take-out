package com.sky.utils;

import org.yaml.snakeyaml.events.Event;

/* 为Servlet线程创建一个存储空间，用于随时存取id */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
