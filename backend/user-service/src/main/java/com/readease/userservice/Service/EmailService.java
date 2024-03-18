package com.readease.userservice.Service;

import com.readease.userservice.Utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class EmailService {
//    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String RESET_PASSWORD_EMAIL_TEMPLATE = "reset-password-template";
    public static final String GG_PASSWORD_EMAIL_TEMPLATE = "signup-with-google-template";

//    public static final String TEXT_HTML_ENCONDING = "text/html";
    public static final String RESET_YOUR_READEASE_PASSWORD = "Đặt lại mật khẩu tài khoản ReadEase";
    public static final String READ_EASE_PASSWORD = "Mật khẩu tài khoản ReadEase liên kết với Google";
//    @Value("${spring.mail.verify.host}")
//    private String host;
//    @Value("${spring.mail.username}")
//    private String fromEmail;
    @Value("${spring.application.cross-origin}")
    private String clientHost;

    @Autowired
    private RestClient restClient;

    private static final String emailServiceUri = "http://localhost:8080/api/email/send";
    @Async
    public void sendHTMLEmail(String to, String token){
        try{
            Map<String, String> templateVars = new HashMap<>();
            templateVars.put("url", EmailUtils.getVerificationUrl(clientHost,token));
            EmailBody emailBody = new EmailBody(to,RESET_YOUR_READEASE_PASSWORD,RESET_PASSWORD_EMAIL_TEMPLATE,templateVars);
            String result = restClient.post()
                    .uri(emailServiceUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(emailBody)
                    .retrieve()
                    .body(String.class);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw  new RuntimeException(e.getMessage());
        }
    }

    public void sendPasswordForGGLogin(String to, String pwd){
        try{
            Map<String, String> templateVars = new HashMap<>();
            templateVars.put("pwd", pwd);
            EmailBody emailBody = new EmailBody(to,READ_EASE_PASSWORD,GG_PASSWORD_EMAIL_TEMPLATE,templateVars);
            String result = restClient.post()
                    .uri(emailServiceUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(emailBody)
                    .retrieve()
                    .body(String.class);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw  new RuntimeException(e.getMessage());
        }
    }

    public void sendResetPasswordEmailForAdmin2(String to, String pwd){
        try{
            Map<String, String> templateVars = new HashMap<>();
            templateVars.put("email", to);
            templateVars.put("password", pwd);
            EmailBody emailBody = new EmailBody(to,"[ReadEase] Thiết lập lại mật khẩu tài khoản QTV mức 2","reset-password-admin-2-template",templateVars);
            String result = restClient.post()
                    .uri(emailServiceUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(emailBody)
                    .retrieve()
                    .body(String.class);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw  new RuntimeException(e.getMessage());
        }
    }

    public void sendWelcomeEmailForAdmin2(String to, String pwd){
        try{
            Map<String, String> templateVars = new HashMap<>();
            templateVars.put("email", to);
            templateVars.put("password", pwd);
            EmailBody emailBody = new EmailBody(to,"Chào mừng bạn đến với ReadEase - QTV mức 2","welcome-admin-2-template",templateVars);
            String result = restClient.post()
                    .uri(emailServiceUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(emailBody)
                    .retrieve()
                    .body(String.class);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw  new RuntimeException(e.getMessage());
        }
    }


}
