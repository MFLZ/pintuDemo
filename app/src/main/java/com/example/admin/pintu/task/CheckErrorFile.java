package com.example.admin.pintu.task;

import android.content.Context;
import android.text.TextUtils;

import com.example.admin.pintu.service.UploadErrorInfo;
import com.example.admin.pintu.utils.NetworkUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**检测程序异常信息
 * Created by MFLZ on 2017-03-08.
 */
public class CheckErrorFile  extends BaseTask<String,Void,String> {
    public CheckErrorFile(Context context) {
        super(context);
    }

    @Override
    protected String doInBackground(String... params) {
        System.out.println("---->>>检测是否有错误信息");
        if (!NetworkUtils.isAvailableByPing(context)) {
            return null;//没有网络不检测
        }
        String path=params[0];
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                File[] files = file.listFiles();
                int len = null == files ? 0 : files.length;
                if (len > 0) {
                    String subject=getSubject();
                    for (int i = 0; i < len; i++) {
                        file=files[i];

                        System.out.println("error path-->" + file.getPath());
                        String content = getFileContent(file);

                        System.out.println("---->>准备发送邮件了");
                        UploadErrorInfo.startSend(context, "1951239060@qq.com", subject, content, null);
                        UploadErrorInfo.startSend(context,"15387093326@163.com",subject,content,file.getPath());
                    }
                }
            }
        }
        return null;
    }

    private String getSubject(){
        return "拼图游戏软件异常的错误信息:";
    }


    private String getFileContent(File f){
        FileInputStream is=null;
        try {
            is = new FileInputStream(f);
            InputStreamReader reader = new InputStreamReader(is, "GBK");
            BufferedReader br = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str + "\n");
            }
            reader.close();

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (is!=null)is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
