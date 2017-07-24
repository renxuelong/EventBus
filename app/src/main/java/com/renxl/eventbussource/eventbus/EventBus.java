package com.renxl.eventbussource.eventbus;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by renxl
 * On 2017/7/22 21:04.
 */

public class EventBus {
    private static final EventBus ourInstance = new EventBus();

    /**
     * 获取单例对象的接口
     *
     * @return EventBus
     */
    public static EventBus getInstance() {
        return ourInstance;
    }

    /**
     * 存放所有已注册的类以及其中使用了 Subscrible 注解的方法
     */
    private Map<Object, List<SubscribleMethod>> cacheMap = new HashMap<>();

    private ExecutorService mExecutorService = Executors.newCachedThreadPool();

    private MainHandler mMainHandler = new MainHandler(Looper.getMainLooper());

    private static class MainHandler extends Handler {
        MainHandler(Looper mainLooper) {
            super(mainLooper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    private EventBus() {
    }

    /**
     * 遍历 正在注册的类中所有使用了 Subscrible 注解的方法
     *
     * @param activity 正在注册的类
     */
    public void register(Object activity) {

        // 在已经注册的列表中出去当前类对应的使用了 Subscrible 注解的方法
        List<SubscribleMethod> list = cacheMap.get(activity);

        // 等于空就去遍历，不为空说明已注册，已注册就不用再处理
        if (list == null) {
            List<SubscribleMethod> methods = getSubscribleMethods(activity);
            cacheMap.put(activity, methods);
        }
    }

    /**
     * 寻找岗位
     *
     * @param activity 注册类
     * @return 该类中包含的所有被注解的方法
     */
    private List<SubscribleMethod> getSubscribleMethods(Object activity) {
        List<SubscribleMethod> list = new ArrayList<>();
        Class clazz = activity.getClass();

        while (clazz != null) {
            String name = clazz.getName();
            if (name.startsWith("java.") || name.startsWith("javax") || name.startsWith("android"))
                break;

            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                // 是否含有 Subscrible 注解
                Subscrible subscrible = method.getAnnotation(Subscrible.class);
                if (subscrible == null) continue;

                Class<?>[] parametes = method.getParameterTypes();
                if (parametes.length != 1) {
                    throw new RuntimeException("EventBus methods not support parametes");
                }

                // 取出注解值，声明了需要方法执行所在的线程
                ThreadMode threadMode = subscrible.threadMode();

                SubscribleMethod subscribleMethod = new SubscribleMethod(method, threadMode, parametes[0]);
                list.add(subscribleMethod);
            }

            // 如果父类同时也注册，此时需要遍历父类
            // 不断地寻找父类中需要接受消息的方法
            // 因为子类会继承父类所有的方法，如果父类中声明了某接收方法，那么继承了该类的类在收到消息时是需要处理该方法的
            clazz = clazz.getSuperclass();
        }
        return list;
    }


    /**
     * 发送消息
     *
     * @param message 发送的消息
     */
    public void post(final Object message) {
        // 遍历 Map 中所有的类，并遍历其对应的方法集合，找到参数为当前类型消息的方法
        Set<Object> set = cacheMap.keySet();

        // 遍历 Map 中所有类
        for (final Object activity : set) {

            List<SubscribleMethod> methods = cacheMap.get(activity);
            // 遍历该类中所有被注解的方法
            for (final SubscribleMethod subscribleMethod : methods) {

                // 根据传递类型跟接收者类型是否一致来判断
                // 如果对应则将调用其中的方法
                if (subscribleMethod.getEventType().isAssignableFrom(message.getClass())) {

                    // 判断接收消息指定的线程
                    switch (subscribleMethod.getThreadMode()) {
                        case PostThread: // 当前线程，直接处理
                            invoke(subscribleMethod, activity, message);
                            break;
                        case MainThread:
                            // 如果在主线程
                            if (Looper.myLooper() == Looper.getMainLooper())
                                invoke(subscribleMethod, activity, message);
                            else {
                                // 线程切换 使用 Handler
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribleMethod, activity, message);
                                    }
                                });
                            }
                            break;
                        case Async:
                            if (Looper.myLooper() == Looper.getMainLooper()) { // 通过线程池执行
                                // 线程切换
                                mExecutorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribleMethod, activity, message);
                                    }
                                });
                            } else
                                invoke(subscribleMethod, activity, message);
                            break;
                    }
                }
            }
        }
    }

    /**
     * 通过反射调用方法
     */
    private void invoke(SubscribleMethod subscribleMethod, Object activity, Object message) {
        Method method = subscribleMethod.getMethod();
        try {
            method.invoke(activity, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
