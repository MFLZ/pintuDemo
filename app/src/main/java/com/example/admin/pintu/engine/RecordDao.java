package com.example.admin.pintu.engine;

import com.example.admin.pintu.model.Record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by admin on 2017/9/20.
 */

public class RecordDao extends BaseDao {
    public RecordDao(Realm realm) {
        super(realm);
    }
    //init data
    public boolean init() {
        /**
         * 此处要注意，方法最后调用的是添加或者修改的方法。
         * 如果list的数据都不给id，则第一条记录添加成功后的id为0，后面的都在此基础上修改。
         * 最后的效果是，数据库只有一条记录，id为0，其他字段被更新为了最后一个对象的数据
         */
        List<Record> list = new ArrayList<>();
        list.add(new Record("莫封浪子", "简单","20170922", 999));
        list.add(new Record("莫封浪子", "困难","20170922", 999));
        list.add(new Record("莫封浪子", "炼狱","20170922", 999));
        list.add(new Record("莫封浪子", "深渊","20170922", 999));
        return insert(list);
    }

    /**
     * 条件查询
     *
     * @return 返回结果集合
     */
    public RealmResults<Record> findByAnyParams(HashMap<Object, Object> params) {
        //realm.where(TestUser.class)
        //可跟查询条件
        //.or()                      或者
        //.beginsWith()              以xxx开头
        //.endsWith()                以xxx结尾
        //.greaterThan()             大于
        //.greaterThanOrEqualTo()    大于或等于
        //.lessThan()                小于
        //.lessThanOrEqualTo()       小于或等于
        //.equalTo()                 等于
        //.notEqualTo()              不等于
        //.findAll()                 查询所有
        //.average()                 平均值
        //.beginGroup()              开始分组
        //.endGroup()                结束分组
        //.between()                 在a和b之间
        //.contains()                包含xxx
        //.count()                   统计数量
        //.distinct()                去除重复
        //.findFirst()               返回结果集的第一行记录
        //.isNotEmpty()              非空串
        //.isEmpty()                 为空串
        //.isNotNull()               非空对象
        //.isNull()                  为空对象
        //.max()                     最大值
        //.maximumDate()             最大日期
        //.min()                     最小值
        //.minimumDate()             最小日期
        //.sum()                     求和
        return realm.where(Record.class).findAll();
    }



}
