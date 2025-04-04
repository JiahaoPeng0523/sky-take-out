package com.sky.utils;

import com.alibaba.ttl.TransmittableThreadLocal;

public class BaseContextTransmittableThreadLocal {
    private static TransmittableThreadLocal<Long> threadLocal = new TransmittableThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
