package com.readease.documentservice.Controller.Request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CollectionReq {
    private String userId;
    private String collectionName;
}
