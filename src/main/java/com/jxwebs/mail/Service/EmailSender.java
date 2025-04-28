package com.jxwebs.mail.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class EmailSender {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${sender}")
    private String from;

    private static final Map<String , String > DOMAIN_MAP = new HashMap<>();
    static  {
        DOMAIN_MAP.put("gmail.com","gmail.com");
        DOMAIN_MAP.put("yahoo.com","yahoo.com.tw");
        DOMAIN_MAP.put("yahoo.com.tw","yahoo.com.tw");
    }

    private MimeMessage emailMaker(String to , String[] cc , String subject , String  content , MultipartFile file ) {
        try {
            MimeMessage email = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(email, true);

            helper.setTo(to); // 收件者
            /*
                這邊要精進，把domain 有問題的剔除
             */
            if (cc.length > 0) {
                helper.setCc(cc); // CC
            }
            helper.setSubject(subject); // 標題
            helper.setText(content , true); // 內容
            helper.setFrom(from); // 寄件者
            if (file != null ){
                helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file); // 附件
            }
            return email;
        }catch(MailException  | MessagingException e ){
            e.printStackTrace();
            return null;
        }
    }

    private String checkDomainCorrect(String emailCheck){
        String emailHandle = emailCheck.toLowerCase();
        String[] parts = emailHandle.split("@");
        String domain = parts[1];
        if(DOMAIN_MAP.containsKey(domain)){
            domain = DOMAIN_MAP.get(domain);
            emailCheck = parts[0]+"@"+domain;
            return emailCheck;
        }else {
            return null;
        }
    }

    public String sendEmail( String to , String[] cc , String  title , String  content , MultipartFile file ) {
        try {

            String toIsCheck = checkDomainCorrect(to); // 檢查收件人電子郵件是否 正確 or 有支援
            if (toIsCheck == null){
                return "電子信箱找不到 or 不支援";
            }

            List<String> ccArrayList = new ArrayList<>(); // 移除 cc 電子郵件是否 正確 or 有支援
            if (cc != null){
                for (String ccCheck : cc ){
                    String result = checkDomainCorrect(ccCheck);
                    if (result != null){
                        ccArrayList.add(result);
                    }
                }
            }
            String[] ccIsCheck = ccArrayList.toArray(new String[0]);

            MimeMessage email = emailMaker( toIsCheck , ccIsCheck , title , content , file ); // 組信
            if (email != null ){
                mailSender.send(email); // 發信
                return "寄送成功";
            }else {
                return "寄送失敗";
            }
        } catch (MailException e) {
            e.printStackTrace();
            return "發生錯誤";
        }
    }
}
