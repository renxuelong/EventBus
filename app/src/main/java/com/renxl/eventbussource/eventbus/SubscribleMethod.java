package com.renxl.eventbussource.eventbus;

import java.lang.reflect.Method;

/**
 * Created by renxl
 * On 2017/7/22 21:18.
 * <p>
 * 将要执行的方法封装成对象
 * 其中包括要执行的方法、方法要执行的线程
 */

public class SubscribleMethod {

    /**
     * 要执行的方法
     */
    private Method method;

    /**
     * 发布者要求的执行线程
     */
    private ThreadMode mThreadMode;

    /**
     * 方法的参数类型
     */
    Class<?> eventType;

    public SubscribleMethod(Method method, ThreadMode threadMode, Class<?> eventType) {
        this.method = method;
        mThreadMode = threadMode;
        this.eventType = eventType;
    }

    public Method getMethod() {
        return method;
    }

    public ThreadMode getThreadMode() {
        return mThreadMode;
    }


    public Class<?> getEventType() {
        return eventType;
    }
}
