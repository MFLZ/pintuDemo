package com.example.admin.pintu.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.TextUtils;

/**
 * 吐司帮助类
 * Created by MFLZ on 2017-04-10.
 */
public class DialogHelper {
    /**
     * 获取一个dialog对象
     *
     * @return
     */
    public static AlertDialog.Builder getDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder;
    }

    /**
     * 获取一个进度对话框（耗时操作使用）
     * @param context
     * @param msg
     * @return
     */
    public static ProgressDialog getWaitDialog(Context context, String msg) {
        ProgressDialog waitDialog = new ProgressDialog(context);
        if (!TextUtils.isEmpty(msg)) {
            waitDialog.setMessage(msg);
        }
        return waitDialog;
    }

    public static AlertDialog.Builder getMessageDialog(Context context, String msg,
                                                       DialogInterface.OnClickListener
                                                               onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setMessage(msg);
        builder.setPositiveButton("确定",onClickListener);
        return builder;
    }

    /**
     * 获取一个信息对话框
     * @param context
     * @param msg
     * @return
     */
    public static AlertDialog.Builder getMessageDialog(Context context, String msg){
        return getMessageDialog(context, msg, null);
    }

    public static AlertDialog.Builder getSelectDialog(Context context, String title, String[]
            arrays, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setItems(arrays, onClickListener);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setPositiveButton("取消", null);
        return builder;
    }

    /**
     * 选择对话框
     * @param context
     * @param arrays
     * @param onClickListener
     * @return
     */
    public static AlertDialog.Builder getSelectDialog(Context context, String[] arrays,
                                                      DialogInterface.OnClickListener
                                                              onClickListener) {
        return getSelectDialog(context, "", arrays, onClickListener);
    }

    public static AlertDialog.Builder getCoonfirmDialog(Context context, String title,
                                                        String msg,
                      DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setTitle(title);
        builder.setMessage(Html.fromHtml(msg));
        builder.setPositiveButton("确定", onClickListener);
        builder.setNegativeButton("取消", null);
        return builder;
    }
    public static AlertDialog.Builder getCoonfirmDialog(Context context, String msg,
                                                        DialogInterface.OnClickListener onClickListener,DialogInterface.OnClickListener onCancleClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setMessage(msg);
        builder.setPositiveButton("确定", onClickListener);
        builder.setNegativeButton("取消", onCancleClickListener);
        return builder;
    }
    public static AlertDialog.Builder getCoonfirmDialog(Context context,
                                                        String title,
                                                        String msg,
                                                        String okString,
                                                        String cancleString,
                                                        DialogInterface.OnClickListener onClickListener,
                                                        DialogInterface.OnClickListener onCancleClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        if (!TextUtils.isEmpty(msg)) {
            builder.setTitle(title);
        }
        builder.setMessage(msg);
        builder.setPositiveButton(okString, onClickListener);
        builder.setNegativeButton(cancleString, onCancleClickListener);
        return builder;
    }

    /**
     * 创建可自定义对话框内容和按钮文字的对话框
     * @param context
     * @param msg                   对话框内容
     * @param okString              确定按钮文字
     * @param cancleString          取消按钮文字
     * @param onClickListener       确定监听
     * @param onCancleClickListener 取消监听
     * @return
     */

    public static AlertDialog.Builder getCoonfirmDialog(Context context,
                                                        String msg,
                                                        String okString,
                                                        String cancleString,
                                                        DialogInterface.OnClickListener onClickListener,
                                                        DialogInterface.OnClickListener onCancleClickListener) {

        return getCoonfirmDialog(context, "", msg, okString, cancleString, onClickListener,
                onCancleClickListener);
    }


    public static AlertDialog.Builder getSingleChoiceDialog(Context context, String title,
                                                            String[] arrays, int selectIndex,
                                                            DialogInterface.OnClickListener
                                                                    onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setSingleChoiceItems(arrays, selectIndex, onClickListener);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setNegativeButton("取消",null);
        return builder;
    }


    /**
     * 单选对话框
     * @param context
     * @param arrays   可供选择的数据
     * @param selectIndex  默认选择的索引
     * @param onClickListener
     * @return
     */

    public static AlertDialog.Builder getSingleChoiceDialog(Context context,
                                                            String[] arrays, int selectIndex,
                                                            DialogInterface.OnClickListener
                                                                    onClickListener) {

        return getSingleChoiceDialog(context,"",arrays,selectIndex,onClickListener);
    }
}

