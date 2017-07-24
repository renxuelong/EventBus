package com.renxl.eventbus;

/**
 * Created by renxl
 * On 2017/7/22 21:15.
 * <p>
 * 声明方法执行所在线程的枚举
 */

public enum ThreadMode {
    /**
     * 当前线程
     */
    PostThread,

    /**
     * 主线程
     */
    MainThread,

    /**
     * 子线程
     */
    Async
}
