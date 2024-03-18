package com.readease.documentservice.Controller.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateNoteReq {
    private int documentID;
    private String content;
    private String position;
}
