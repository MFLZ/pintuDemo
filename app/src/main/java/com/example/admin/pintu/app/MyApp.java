package com.example.admin.pintu.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.example.admin.pintu.utils.SPUtils;

import java.util.LinkedList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**程序主入口
 * Created by MFLZ on 2017-03-08.
 */
public class MyApp extends Application {
    private static Context context;

    private LinkedList<Activity> backStack;

    public void addBackStack(Activity activity){
        backStack.add(activity);
    }

    public void removeBackStack(){
        backStack.removeLast();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (backStack==null){
            backStack =new LinkedList<>();
        }

        context = getApplicationContext();
        SPUtils.initSP(getApplicationContext());
        //初始化Realm
        Realm.init(this);
        RealmConfiguration config = new  RealmConfiguration.Builder()
                .name("myRealm.realm")//文件名
                .schemaVersion(0) //版本号
                .deleteRealmIfMigrationNeeded()// 声明版本冲突时自动删除原数据库
                .build();
        Realm.setDefaultConfiguration(config);
        //检测是否有错误信息
//        CrashErrorHandler.getInstance().init(getApplicationContext());
    }

    public static Context getContext() {
        return context;
    }


    /**
     * 退出程序
     */
    public void existApp(){
        int size = null == backStack ? 0 : backStack.size();
        for (int i = 0; i < size; i++) {
            backStack.removeLast().finish();
        }
        System.exit(0);
    }

}
