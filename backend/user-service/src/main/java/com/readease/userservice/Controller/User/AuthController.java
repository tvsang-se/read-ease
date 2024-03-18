package com.readease.userservice.Controller.User;

import com.readease.userservice.Config.GeneratePassword;
import com.readease.userservice.Config.JwtService;
import com.readease.userservice.Controller.User.Response.AuthResponse;
import com.readease.userservice.Controller.User.Response.Collection;
import com.readease.userservice.Model.Role;
import com.readease.userservice.Model.Token;
import com.readease.userservice.Model.TokenType;
import com.readease.userservice.Model.User;
import com.readease.userservice.Repo.RoleRepo;
import com.readease.userservice.Repo.TokenRepo;
import com.readease.userservice.Repo.UserRepo;
import com.readease.userservice.Service.DocumentService;
//import com.readease.userservice.Service.DriveService;
import com.readease.userservice.Service.EmailService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
//import java.util.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Value("${spring.application.check-email-url}")
    private String checkEmailUrl;
    @Value("${spring.application.avatar-default}")
    private String avatarDefault;
    @Value("${spring.application.cross-origin}")
    private String domain;
    @Value("${spring.application.security.jwt.refresh-token.expiration}")
    private int maxAgeCookie;
    private final EmailService emailService;
    private final DocumentService documentService;
    private final UserRepo userRepo;
    private final UserDetailsService userDetailsService;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepo tokenRepo;
    @Autowired
    private RestClient restClient;

//    private final DriveService driveService;


    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User req) throws GeneralSecurityException, IOException {
        if (userRepo.countUserByEmail(req.getEmail()) == 1)
            return new ResponseEntity<>("Email already exists!!!", HttpStatus.BAD_REQUEST);

        if (!isExistEmail(req.getEmail()))
            return new ResponseEntity<>("Email not real", HttpStatus.UNAUTHORIZED);

        Role role = roleRepo.findById(1).orElseThrow();
        User user = new User(req.getEmail(), passwordEncoder.encode(req.getPassword()), role, avatarDefault, req.getTargetLanguage());
        userRepo.save(user);
//        String folderID = driveService.createFolder(user.getID());
        String folderID = restClient.post()
                .uri("http://DOCUMENT-SERVICE/api/user/create-folder/"+user.getID())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);
        user.setIdDriveFolder(folderID);
        userRepo.save(user);
        return new ResponseEntity<>("Sign up successfully!!", HttpStatus.CREATED);
    }

    private boolean isExistEmail(String email) {
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
                if (deliverability.equals("DELIVERABLE"))
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

    @PostMapping("/login")
    public ResponseEntity<?> loginWithGoogle(@Nonnull HttpServletResponse response, @RequestBody User req) throws GeneralSecurityException, IOException {
        User user = userRepo.findUserByEmail(req.getEmail()).orElse(null);
        boolean isFirstLogin = false;
        if (user == null) {
            GeneratePassword generatePassword = new GeneratePassword();
            String pwd = generatePassword.generateStrongPassword(8);

            emailService.sendPasswordForGGLogin(req.getEmail(),pwd);
            user = roleRepo.findById(1).map(role -> {
                User _user = new User(req.getEmail(), passwordEncoder.encode(pwd), role, req.getAvatar(), req.getTargetLanguage());
                _user.setAvatar(req.getAvatar());
                return userRepo.save(_user);
            }).orElseThrow();

//            String folderID = driveService.createFolder(user.getID());
            String folderID = restClient.post()
                    .uri("http://DOCUMENT-SERVICE/api/user/create-folder/"+user.getID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(String.class);
            user.setIdDriveFolder(folderID);
            userRepo.save(user);

            isFirstLogin = true;
        }
        String jwtToken = jwtService.generateToken(user);

        //Update token into database
        checkExistAndSaveToken(user, jwtToken, TokenType.ACCESS);


        user.setLastAccess(new Date());
        userRepo.save(user);

        Cookie cookie = new Cookie("refreshToken", jwtService.generateRefreshToken(user));
        cookie.setMaxAge(maxAgeCookie / 1000);
        cookie.setHttpOnly(true);
//        cookie.setDomain("http://localhost:3000");
        response.addCookie(cookie);


        AuthResponse res = AuthResponse.builder()
                .userID(user.getID())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .targetLanguage(user.getTargetLanguage())
                .idDriveFolder(user.getIdDriveFolder())
                .token(jwtToken)
                .build();


        if (!isFirstLogin) {
            res.setCurrentDocumentReading(documentService.getLastReadingDocument(user.getID())); //user.getLastReadingDocument()
            res.setCollections(documentService.getCollectionCustom(user.getID())); //getCollectionCustom(user.getCollections())
            res.setDocuments(documentService.getDocumentCustom(user.getID(),0,10)); //user.getDocumentCustom(0, 10)
            res.setTotalCapacity(user.getTotalCapacity());
        }

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/login/step1")
    public ResponseEntity<?> loginStep1(@RequestBody User req) {
        if (userRepo.countUserByEmail(req.getEmail()) == 0)
            return new ResponseEntity<>("Email is not valid!!!", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>("Email is valid", HttpStatus.OK);
    }

    @PostMapping("/login/step2")
    public ResponseEntity<?> loginStep2(@Nonnull HttpServletResponse response, @RequestBody User req) {
        if (userRepo.countUserByEmail(req.getEmail()) == 0)
            return new ResponseEntity<>("Email is not valid!!!", HttpStatus.BAD_REQUEST);

        User user = userRepo.findUserByEmail(req.getEmail()).orElseThrow();
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            return new ResponseEntity<>("Password is not valid", HttpStatus.NOT_ACCEPTABLE);

        String jwtToken = jwtService.generateToken(user);

        //Update token into database
        checkExistAndSaveToken(user, jwtToken, TokenType.ACCESS);


        user.setLastAccess(new Date());
        userRepo.save(user);

        Cookie cookie = new Cookie("refreshToken", jwtService.generateRefreshToken(user));
        cookie.setMaxAge(maxAgeCookie / 1000);
        cookie.setHttpOnly(true);
//        cookie.setDomain("http://localhost:3000");
        response.addCookie(cookie);


        AuthResponse res = AuthResponse.builder()
                .userID(user.getID())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .token(jwtToken)
                .totalDocument(documentService.countDocument(user.getID()))
                .totalCapacity(user.getTotalCapacity())
                .targetLanguage(user.getTargetLanguage())
                .idDriveFolder(user.getIdDriveFolder())
                .currentDocumentReading(documentService.getLastReadingDocument(user.getID()))
                .collections(documentService.getCollectionCustom(user.getID()))
                .documents(documentService.getDocumentCustom(user.getID(),0, 10))
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    private Set<HashMap<String, Object>> getCollectionCustom(Set<Collection> collections) {
        Set<HashMap<String, Object>> result = new HashSet<>();
        for (Collection col : collections) {
            result.add(new HashMap<String, Object>() {
                {
                    put("ID", col.getID());
                    put("name", col.getName());
                    put("quantity", col.getDocuments().size());
                }
            });
        }
        return result;
    }

    @PutMapping("/logout")
    public ResponseEntity<?> logout(@Nonnull HttpServletResponse response, @RequestBody User req) {

        User user = userRepo.findUserByEmail(req.getEmail()).orElse(null);

        if (user == null) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

        long totalTime = Duration.between(user.getLastAccess().toInstant(), new Date().toInstant()).getSeconds();

        user.setLastAccess(new Date());
        user.setTotalAccessTime(user.getTotalAccessTime() + totalTime);
        userRepo.save(user);
        tokenRepo.deleteTokenByUserID(user.getID());

        return new ResponseEntity<>("Log out successfully", HttpStatus.OK);
    }

    @PostMapping("/forgot-password/step1")
    public ResponseEntity<?> forgotPasswordStep1(@RequestBody User req) {
        User user = userRepo.findUserByEmail(req.getEmail()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email invalid!!!!")
        );

        String resetPasswordToken = jwtService.generateResetPasswordToken(user);

        saveUserToken(user, resetPasswordToken, TokenType.RESET_PASSWORD);

        emailService.sendHTMLEmail(user.getEmail(), resetPasswordToken);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @GetMapping("/forgot-password/step2")
    public ResponseEntity<?> forgotPasswordStep2(@RequestParam("token") String resetPasswordToken) {
        var token = tokenRepo.findTokenByToken(resetPasswordToken)
                .orElse(null);

        if (resetPasswordToken == null || jwtService.isTokenExpried(resetPasswordToken) || token == null)
            return new ResponseEntity<>("Token invalid!!!", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/forgot-password/step3")
    public ResponseEntity<?> forgotPasswordStep2(@RequestParam("token") String resetPasswordToken, @RequestBody User req) {
        var token = tokenRepo.findTokenByToken(resetPasswordToken)
                .orElse(null);

        if (resetPasswordToken == null || req.getPassword() == null || token == null
                || jwtService.isTokenExpried(resetPasswordToken))
            return new ResponseEntity<>("Request invalid!!!", HttpStatus.BAD_REQUEST);

        String email = jwtService.extractUserEmail(resetPasswordToken);

        userRepo.updatePasswordByEmail(email, passwordEncoder.encode(req.getPassword()));

        tokenRepo.delete(token);

        return new ResponseEntity<>(HttpStatus.OK);
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
    private long accessTokenExpiration;
    @Value("${spring.application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;
    @Value("${spring.application.security.jwt.expiration-reset-password}")
    private long resetPasswordExpiration;

    private long getExpiration(TokenType tokenType) {
        return tokenType == TokenType.ACCESS ? accessTokenExpiration
                : tokenType == TokenType.REFRESH ? refreshTokenExpiration
                : resetPasswordExpiration;
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam String token) {
        final String userEmail;
        try {
            userEmail = jwtService.extractUserEmail(token); //todo extract the userEmail form jwt token
        } catch (Exception e) {
            return "400";
        }
        System.out.println("userEmail = "+userEmail);
        if (userEmail != null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            //Kiểm tra token trong Authorization có phải là token hiện tại hay không
            String userID = userRepo.findUserIDByEmail(userEmail);
            String isValidToken = tokenRepo.findAccessTokenByUserID(userID);
            // Lỗi token không tồn tại trong csdl.
            if(isValidToken == null){
                return String.valueOf(HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED); //407
            }
            // Lỗi token của phiên đăng nhập trước đó.
            else if(!isValidToken.equals(token)){
                return String.valueOf(HttpServletResponse.SC_CONFLICT); //409
            }
            else if (jwtService.isTokenValid(token, userDetails)) {
                return userID;
            }
        }
        return "400";
    }

}
