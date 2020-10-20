import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSeed {
    public static void seedMail(String mailTitle, String mailPath, String content) {
        Transport transport = null;
        try {
            //创建一个配置文件并保存
            Properties properties = new Properties();

            properties.setProperty("mail.host","smtp.qq.com");

            properties.setProperty("mail.transport.protocol","smtp");

            properties.setProperty("mail.smtp.auth","true");


            //QQ存在一个特性设置SSL加密
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.ssl.socketFactory", sf);

            //创建一个session对象
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("1295831067@qq.com","dqybtqltvvjgbabb");
                }
            });

            //开启debug模式
            session.setDebug(false);

            //获取连接对象
            transport = session.getTransport();

            //连接服务器
            transport.connect("smtp.qq.com","1295831067@qq.com","dqybtqltvvjgbabb");

            //创建邮件对象
            MimeMessage mimeMessage = new MimeMessage(session);

            //邮件发送人
            mimeMessage.setFrom(new InternetAddress("1295831067@qq.com"));

            //邮件接收人
            mimeMessage.setRecipient(Message.RecipientType.TO,new InternetAddress(mailPath));

            //邮件标题
            mimeMessage.setSubject(mailTitle);

            //邮件内容
            mimeMessage.setContent(content,"text/html;charset=UTF-8");

            //发送邮件
            transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException m) {
                    m.printStackTrace();
                }
            }
        }
    }
}
