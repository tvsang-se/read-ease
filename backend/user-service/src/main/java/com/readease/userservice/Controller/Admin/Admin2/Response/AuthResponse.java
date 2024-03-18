package com.readease.userservice.Controller.Admin.Admin2.Response;

import com.readease.userservice.Controller.User.Response.Collection;
import com.readease.userservice.Controller.User.Response.Document;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String userID;
    private String email;
    private String avatar;
    private String token;
    private Document currentDocumentReading;
    private Set<Document> documents;
    private Set<Collection> collections;
    private int totalDocument;
}
