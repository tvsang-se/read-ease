package com.readease.userservice.Controller.Admin;

import com.readease.userservice.Config.JwtService;
import com.readease.userservice.Model.TokenType;
import com.readease.userservice.Model.User;
import com.readease.userservice.Repo.RoleRepo;
import com.readease.userservice.Repo.TokenRepo;
import com.readease.userservice.Model.Token;
import com.readease.userservice.Repo.UserRepo;
import com.readease.userservice.Service.Admin2Service;
import com.readease.userservice.Service.EmailService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AuthAdminController {
    private final EmailService emailService;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepo tokenRepo;

    private final Admin2Service admin2Service;

    @PostMapping("/login")
    public ResponseEntity<?> loginStep2(@Nonnull HttpServletResponse response,@RequestBody User req) {
        if (userRepo.countUserByEmail(req.getEmail()) == 0)
            return new ResponseEntity<>("Not found",HttpStatus.NOT_FOUND);

        User user = userRepo.findUserByEmail(req.getEmail()).orElseThrow();
        if(user.getRole().getID() == 1)
            return new ResponseEntity<>("Unauthorized access",HttpStatus.FORBIDDEN);
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            return new ResponseEntity<>("Password is not valid", HttpStatus.BAD_REQUEST);

        String jwtToken = jwtService.generateToken(user);

        //Update token into database
        checkExistAndSaveToken(user, jwtToken, TokenType.ACCESS);


        user.setLastAccess(new Date());
        userRepo.save(user);

//        Cookie cookie = new Cookie("refreshToken", jwtService.generateRefreshToken(user));
//        cookie.setMaxAge(maxAgeCookie/1000);
//        cookie.setHttpOnly(true);
////        cookie.setDomain("http://localhost:3000");
//        response.addCookie(cookie);

        HashMap<String, Object> res = new HashMap<>();
        res.put("userID", user.getID());
        res.put("email", user.getEmail());
        res.put("role", user.getRole().getRole());
        res.put("token", jwtToken);
        res.put("userCreatedByDate",admin2Service.getCountCreatedCustom("date"));
        res.put("users", admin2Service.getUserInformation());
        res.put("targetLanguageCount", admin2Service.getCountTargetLanguage());
        res.put("totalDocuments", admin2Service.getTotalDocuments());
        res.put("totalUsers", admin2Service.getTotalUsers());
        res.put("totalAnnotations", admin2Service.getTotalAnnotation());
        res.put("totalCapacity", admin2Service.getTotalCapacity());
        res.put("listYear", admin2Service.getListYear());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/logout")
    public ResponseEntity<?> logout(@Nonnull HttpServletResponse response,@RequestBody User req) {

        User user = userRepo.findUserByEmail(req.getEmail()).orElse(null);

        if(user == null) return  new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);

        long totalTime = Duration.between(user.getLastAccess().toInstant(), new Date().toInstant()).getSeconds();

        user.setLastAccess(new Date());
        user.setTotalAccessTime(user.getTotalAccessTime() + totalTime);
        userRepo.save(user);
        tokenRepo.deleteTokenByUserID(user.getID());

        return new ResponseEntity<>("Log out successfully", HttpStatus.OK);
    }


    private void saveUserToken(User user, String jwt, TokenType tokenType) {
        tokenRepo.save(Token.builder()
                .user(user)
                .expriedAt(new Date(new Date().getTime() + getExpiration(tokenType)))
                .token(jwt)
                .type(tokenType)
                .build());
    }

    private void checkExistAndSaveToken(User user, String jwt, TokenType tokenType) {

        tokenRepo.deleteTokenByUserID(user.getID());
        saveUserToken(user, jwt, tokenType);
    }

    @Value("${spring.application.security.jwt.expiration}")
    private long  accessTokenExpiration;
    @Value("${spring.application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;
    @Value("${spring.application.security.jwt.expiration-reset-password}")
    private  long resetPasswordExpiration;
    private long getExpiration(TokenType tokenType) {
        return tokenType == TokenType.ACCESS ?  accessTokenExpiration
                : tokenType == TokenType.REFRESH ? refreshTokenExpiration
                :resetPasswordExpiration;
    }


}
