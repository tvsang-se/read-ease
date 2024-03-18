package com.readease.userservice.Controller.User;

import com.readease.userservice.Controller.User.Request.ChangePasswordRequest;
import com.readease.userservice.Model.User;
import com.readease.userservice.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/user/account")
@RequiredArgsConstructor
public class AccountController {
    private final UserRepo userRepo;
    private  final PasswordEncoder passwordEncoder;

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

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountInfor(@PathVariable("id") String userID){

        User resUser = userRepo.findById(userID).orElse(null);
        if ( resUser == null)
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(new HashMap<String, Object> (){
            {
                put("email", resUser.getEmail());
                put("avatar", resUser.getAvatar());
                put("totalAccessTime", resUser.getTotalAccessTime());
                put("totalCapacity", resUser.getTotalCapacity());
            }
        }, HttpStatus.OK);
    }
    @PutMapping("/update-information")
    public ResponseEntity<?> updateInformation(@RequestBody User req){
        User resUser = userRepo.findUserByEmail(req.getEmail()).orElse(null);
        if ( resUser == null)
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

        resUser.setAvatar(req.getAvatar());
        resUser.setTargetLanguage(req.getTargetLanguage());

        userRepo.save(resUser);

        return new ResponseEntity<>("Information update successfully!!!",HttpStatus.OK);
    }

    @PutMapping("/update-capacity")
    public String updateInformation(@RequestParam("userId") String userId,
                                               @RequestParam("sizeOfNewDocument") float sizeOfNewDocument){
        System.out.println("update-capacity: "+userId+", "+sizeOfNewDocument);
        User resUser = userRepo.findById(userId).orElse(null);
        if ( resUser == null)
            return "User not found";

        resUser.setTotalCapacity(resUser.getTotalCapacity() + sizeOfNewDocument);

        userRepo.save(resUser);

        return String.valueOf(resUser.getTotalCapacity());
    }

}
