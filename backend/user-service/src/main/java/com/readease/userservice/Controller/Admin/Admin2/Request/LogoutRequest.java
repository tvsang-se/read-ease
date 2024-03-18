package com.readease.userservice.Controller.Admin.Admin2.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogoutRequest {
    private String email;
    private String loginTime;

}
