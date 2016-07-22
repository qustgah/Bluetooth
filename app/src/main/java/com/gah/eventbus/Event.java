package com.gah.eventbus;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by GaoAhui on 2016/7/5.
 */
public class Event {

    public static final int EVENT_BT_CONNECTED_DEVICES = 10001;//蓝牙连接

    private int id;
    private Object object;
    private Object object2;

    public Event(int id, Object object, Object object2) {
        this.id = id;
        this.object = object;
        this.object2 = object2;
    }

    public Event() {
    }

    public Event(int id) {
        this.id = id;
    }

    public Event(int id, Object object) {
        this.id = id;
        this.object = object;
    }

    public void send()
    {
        EventBus.getDefault().post(this);
    }

    public int getId() {
        return id;
    }

    public Object getObject() {
        return object;
    }

    public Object getObject2() {
        return object2;
    }
}
