package com.readease.userservice.Controller.Admin.Admin2;


import com.readease.userservice.Controller.Admin.Admin2.Request.ChangePasswordRequest;
import com.readease.userservice.Model.User;
import com.readease.userservice.Repo.RoleRepo;
import com.readease.userservice.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/admin2/account-management")
@RequiredArgsConstructor
public class Admin2AccountController {

//    @Value("${application.cross-origin}")
//    private String domain;
//    @Value("${application.security.jwt.refresh-token.expiration}")
//    private int maxAgeCookie;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest req){
        User resUser = userRepo.findById(req.getUserID()).orElse(null);
        if ( resUser == null)
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

        if(!passwordEncoder.matches(req.getOldPwd(), resUser.getPassword()))
            return new ResponseEntity<>("Password is not valid",HttpStatus.BAD_REQUEST);
        resUser.setPassword(passwordEncoder.encode(req.getNewPwd()));
        userRepo.save(resUser);
        return new ResponseEntity<>("Change Password Successfully!!", HttpStatus.OK);
    }
}
