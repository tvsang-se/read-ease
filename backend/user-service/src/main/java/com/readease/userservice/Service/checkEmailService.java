package com.readease.userservice.Service;

import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Request;

public class checkEmailService {
    public String checkMail(){
        Content content = null;
        try {
            content = Request
                    .get("https://emailvalidation.abstractapi.com/v1/?api_key=052baafa9901416da56289e52b046788&email=nlnktpm@gmail.com")
                    .execute().returnContent();
            System.out.println(content);
        } catch (Exception e) {
            System.out.println(e);
        }
        return "";
    }

}
