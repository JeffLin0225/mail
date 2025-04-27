package com.jxwebs.mail.Controller;

import com.jxwebs.mail.Service.EmailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
public class EmailController {
    private final EmailSender emailSender;

    public EmailController(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @PostMapping("/sendEmail")
    public String sendEmail(
                @RequestParam("to") String to ,
                @RequestParam(value = "cc" , required = false) String[] cc ,
                @RequestParam("subject") String subject ,
                @RequestParam("content") String content ,
                @RequestParam(value = "file" , required = false) MultipartFile file
            ) throws Exception {
        String sendMailSuccess = emailSender.sendEmail( to , cc ,subject , content , file);
        System.out.println(sendMailSuccess);
        return sendMailSuccess;
    }
}
