package com.example.admin.pintu.model;

import io.realm.RealmObject;

/**
 * Created by admin on 2017/9/20.
 */

public class Record extends RealmObject {
    private String name;//玩家名字
    private String type;//游戏难度
    private String date;//日期
    private int time; //完成游戏的步数

    public Record() {
    }

    public Record(String name, String type, String date, int time) {
        this.name = name;
        this.type = type;
        this.date = date;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Record{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
