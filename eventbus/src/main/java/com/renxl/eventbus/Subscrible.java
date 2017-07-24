package com.renxl.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by renxl
 * On 2017/7/22 21:13.
 * <p>
 * 需要处理的注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscrible {
    ThreadMode threadMode() default ThreadMode.MainThread;
}
