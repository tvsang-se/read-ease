package com.readease.userservice.Controller.Admin.Admin1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readease.userservice.Config.GeneratePassword;
import com.readease.userservice.Model.Role;
import com.readease.userservice.Model.Token;
import com.readease.userservice.Model.User;
import com.readease.userservice.Repo.RoleRepo;
import com.readease.userservice.Repo.TokenRepo;
import com.readease.userservice.Repo.UserRepo;
import com.readease.userservice.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/admin/admin1/admin2-management")
@RequiredArgsConstructor
public class Admin1Controller {
    @Value("${spring.application.check-email-url}")
    private String checkEmailUrl;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private final TokenRepo tokenRepo;

    @PostMapping("/create")
    public ResponseEntity<?> createNewAdmin2(@RequestBody User req) {
        if (userRepo.countUserByEmail(req.getEmail()) == 1)
            return new ResponseEntity<>("Email already exists!!!", HttpStatus.BAD_REQUEST);

        if(!isExistEmail(req.getEmail()))
            return new ResponseEntity<>("Email not real", HttpStatus.UNAUTHORIZED);

        GeneratePassword generatePassword = new GeneratePassword();
        String password = generatePassword.generateStrongPassword(8);
        Role role = roleRepo.findById(3).orElse(null);

        User user = new User( req.getEmail(),passwordEncoder.encode(password),role,"","");
        userRepo.save(user);
        emailService.sendWelcomeEmailForAdmin2(req.getEmail(),password);


        return new ResponseEntity<>("Create account successfully!", HttpStatus.CREATED);
    }

    private boolean isExistEmail(String email){
        Content content = null;
        try {
            String url = new String(this.checkEmailUrl);
            url += email;
            content = Request
                    .get(url)
                    .execute().returnContent();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(content.toString());

                // Get the value of the "deliverability" key
                String deliverability = jsonNode.get("deliverability").asText();
                if(deliverability.equals("DELIVERABLE"))
                    return true;
                return false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
    // Reset password:
    // B1: Tìm admin2 bằng ID, nếu không thấy trả về lỗi 404.
    // B2: Tạo mật khẩu mới rồi cập nhật mk mới cho admin.
    // B3: Gửi mail cho admin2

    //Tao moi 1 ham trong EmailUntil
    //Tao ham moi trong EmailService, Goi ham moi tao trong EmailUntil trong EmailService.
    //Dung ham moi trong EmailService trong AdminContrller

    @PutMapping("/reset-password/{id}")
    public ResponseEntity<?> resetPassword(@PathVariable("id") String userID) {
        User resUser = userRepo.findById(userID).orElse(null);
        if ( resUser == null)
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        Role role = resUser.getRole();
        if( !role.getRole().equals("admin2"))
            return  new ResponseEntity<>("User is not admin2", HttpStatus.BAD_REQUEST);

        GeneratePassword generatePassword = new GeneratePassword();
        String password = generatePassword.generateStrongPassword(8);
//        Role role = roleRepo.findById(2).orElse(null);

        resUser.setPassword(passwordEncoder.encode(password));
        userRepo.save(resUser);
        emailService.sendResetPasswordEmailForAdmin2(resUser.getEmail(),password);

        return new ResponseEntity<>("Reset password successfully!!!",HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAdmin2(@PathVariable("id") String userID) {
        User resUser = userRepo.findById(userID).orElse(null);
        if ( resUser == null)
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

        Token token =  tokenRepo.findTokenByUserIDAndType(userID, "ACCESS").orElse(null);
        if(token != null)
            return new ResponseEntity<>("Can not delete now", HttpStatus.BAD_REQUEST);

        userRepo.deleteById(userID);

        return new ResponseEntity<>("Delete account admin2 successfully!!!",HttpStatus.OK);
    }

//    @PutMapping("admin2/change-password")
//    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest req){
//        User resUser = userRepo.findById(req.getUserID()).orElse(null);
//        if ( resUser == null)
//            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
//
//        if(!passwordEncoder.matches(req.getOldPwd(), resUser.getPassword()))
//            return new ResponseEntity<>("Password is not valid",HttpStatus.OK);
//        resUser.setPassword(passwordEncoder.encode(req.getNewPwd()));
//        userRepo.save(resUser);
//        return new ResponseEntity<>("Change Password Successfully!!", HttpStatus.OK);
//    }
    @GetMapping("/get-all-admin2")
    public ResponseEntity<?> getAllAdmin2(){
        List<User> admin2List = userRepo.getAllAdmin2();
        List<HashMap<String, Object>> res = new ArrayList<>();
        for(User u : admin2List){
            HashMap<String,Object> temp = new HashMap<>();
            temp.put("ID", u.getID());
            temp.put("email",u.getEmail());
            temp.put("createAt",u.getCreateAt());
            temp.put("lastAccess", u.getLastAccess());
            res.add(temp);
        }
        return new ResponseEntity<>(res,HttpStatus.OK);
    }
}
