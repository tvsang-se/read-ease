package com.readease.userservice.Controller.User.Response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    private long ID;
    private String name;
    private String url;
    private String thumbnailLink;
    private int numberOfPagesReading;
    private float star;
    private float size;
    private int totalPages;
    private Date lastRead;
    private Date createAt;
    private  String userId;
    private Set<Collection> collections;
}
