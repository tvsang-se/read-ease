package com.readease.userservice.Controller.User.Response;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Collection {
    private int ID;
    private String name;

    private  String userId;
    private Set<Document> documents = new HashSet<>();
}
