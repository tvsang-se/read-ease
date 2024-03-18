package com.readease.documentservice.Controller.Request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DocumentReq {
    private String userID;
    private String name;
    private String url;
    private int totalPages;
    private float size;
    private String thumbnailLink;

}
