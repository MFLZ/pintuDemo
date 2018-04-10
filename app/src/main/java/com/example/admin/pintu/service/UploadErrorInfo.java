package com.example.admin.pintu.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.example.admin.pintu.utils.MyLog;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by admin on 2017/8/27.
 */

public class UploadErrorInfo extends IntentService {
    private static final String ACTION_ERROR_FILE = "com.subilin.t1clothes.service.action.ERROR_MSG";

    private static final String TO_MAIN = "com.subilin.t1clothes.service.extra.PARAM1";
    private static final String SUBJECT = "com.subilin.t1clothes.service.extra.PARAM2";
    private static final String CONTENT = "com.subilin.t1clothes.service.extra.PARAM3";
    private static final String PATH = "com.subilin.t1clothes.service.extra.PARAM4";

    private Session sendMailSession;

    public static void startSend(Context context, String toMain, String subject, String content, String path) {

        Intent intent = new Intent(context, UploadErrorInfo.class);
        intent.setAction(ACTION_ERROR_FILE);
        intent.putExtra(TO_MAIN, toMain);
        intent.putExtra(SUBJECT, subject);
        intent.putExtra(CONTENT, content);
        intent.putExtra(PATH, path);
        context.startService(intent);
    }


    public UploadErrorInfo () {
        super("UploadErrorInfo");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_ERROR_FILE.equals(action)) {
                String param1 = intent.getStringExtra(TO_MAIN);
                String param2 = intent.getStringExtra(SUBJECT);
                String param3 = intent.getStringExtra(CONTENT);
                String param4 = intent.getStringExtra(PATH);
                handleActionFoo(param1, param2,param3,param4);
            }
        }
    }

    private void handleActionFoo(String toMain, String subject,String content,String path) {
        MailInfo info=new MailInfo();
        info.setToAddress(toMain);
        info.setSubject(subject);
        info.setContent(content);
        System.out.println(info);
        boolean flag=sendTextMail(info);
        if (flag && !TextUtils.isEmpty(path)) {
            MyLog.i("UploadErrorInfo发送成功");
            File file = new File(path);
            if (file.exists()) file.delete();
        }
    }

    private Session getSendMailSession(Properties pro, String userName, String password){
        if (sendMailSession == null) {
            // 如果需要身份认证，则创建一个密码验证器
            MyAuthenticator authenticator = new MyAuthenticator(userName, password);
            // 根据邮件会话属性和密码验证器构造一个发送邮件的session
            sendMailSession = Session.getDefaultInstance(pro, authenticator);
        }
        return sendMailSession;
    }

    /**
     * 以文本格式发送邮件
     * @param mailInfo 待发送的邮件的信息
     */
    public boolean sendTextMail(MailInfo mailInfo) {
        Session sendMailSession;
        if (mailInfo.isValidate()) {// 判断是否需要身份认证
            sendMailSession = getSendMailSession(mailInfo.getProperties(), mailInfo.getUserName(), mailInfo.getPassword());
        } else
            sendMailSession = getSendMailSession(mailInfo.getProperties(), null, null);

        if (sendMailSession == null) return false;
//        //System.out.println(mailInfo.toString());
//        MyAuthenticator authenticator = null;
//
//        Properties pro = mailInfo.getProperties();
//        if (mailInfo.isValidate()) {
//            // 如果需要身份认证，则创建一个密码验证器
//            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
//        }
//        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
//       Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            Address to = new InternetAddress(mailInfo.getToAddress());
            mailMessage.setRecipient(Message.RecipientType.TO,to);
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // 设置邮件消息的主要内容
            String mailContent = mailInfo.getContent();
            mailMessage.setText(mailContent);
            // 发送邮件
            Transport.send(mailMessage);
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace();
            System.out.println("send error-->"+ex.toString());
        }
        return false;
    }

    private class MailInfo {
        /** 发送邮件的服务器的IP*/
        private String mailServerHost="smtp.qq.com";

        /** 发送邮件的服务器的端口*/
        private String mailServerPort = "25";

        /**邮件发送者的地址 */
        private String fromAddress="253870614@qq.com";

        /** 邮件接收者的地址*/
        private String  toAddress;

        /** 登陆邮件发送服务器的用户名  */
        private String userName="253870614@139.com";

        /** 登陆邮件发送服务器的密码   */
        private String password="abc1233";

        /** 是否需要身份验证    */
        private boolean validate = true;

        /**邮件主题 */
        private String subject;

        /** 邮件的文本内容    */
        private String content;

        /**
         * 获得邮件会话属性
         */
        public Properties getProperties() {
            Properties p = new Properties();
            p.put("mail.smtp.host", this.mailServerHost);
            p.put("mail.smtp.port", this.mailServerPort);
            p.put("mail.smtp.auth", validate ? "true" : "false");
            return p;
        }

        public String getMailServerHost() {
            return mailServerHost;
        }

        public String getMailServerPort() {
            return mailServerPort;
        }

        public String getFromAddress() {
            return fromAddress;
        }

        public String getToAddress() {
            return      toAddress;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }

        public boolean isValidate() {
            return validate;
        }

        public String getSubject() {
            return subject;
        }

        public String getContent() {
            return content;
        }

        public void setToAddress(String toAddress) {
            this.toAddress = toAddress;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "MailInfo{" +
                    "mailServerHost='" + mailServerHost + '\'' +
                    ", mailServerPort='" + mailServerPort + '\'' +
                    ", fromAddress='" + fromAddress + '\'' +
                    ", toAddress='" + toAddress + '\'' +
                    ", userName='" + userName + '\'' +
                    ", password='" + password + '\'' +
                    ", validate=" + validate +
                    ", subject='" + subject + '\'' +
//                    ", content='" + content + '\'' +
                    '}';
        }
    }

    private class MyAuthenticator extends Authenticator {
        String userName = null;
        String password = null;

        public MyAuthenticator(String username, String password) {
            this.userName = username;
            this.password = password;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userName, password);
        }
    }

}
