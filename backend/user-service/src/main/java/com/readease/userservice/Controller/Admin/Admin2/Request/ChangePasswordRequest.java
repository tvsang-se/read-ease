package com.readease.userservice.Controller.Admin.Admin2.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    private String userID;
    private String oldPwd;
    private String newPwd;

}
