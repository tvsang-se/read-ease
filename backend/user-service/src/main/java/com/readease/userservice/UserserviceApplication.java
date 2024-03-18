package com.readease.userservice;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.readease.userservice.Model.Role;
import com.readease.userservice.Model.User;
import com.readease.userservice.Repo.RoleRepo;
import com.readease.userservice.Repo.TokenRepo;
import com.readease.userservice.Repo.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableDiscoveryClient
public class UserserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserserviceApplication.class, args);
	}

//		@Bean
//		CommandLineRunner initDatabase (TokenRepo tokenRepo, UserRepo userRepo, RoleRepo roleRepo, PasswordEncoder passwordEncoder){
//		return new CommandLineRunner() {
//			@Override
//			public void run(String... args) throws Exception {
//
//				Role role = new Role(2, "admin1");
//				User admin1 = new User("readease.webapp@gmail.com", passwordEncoder.encode("Aa!12345"),role,"https://res.cloudinary.com/dwnunieno/image/upload/v1698063552/oj57znd0lo3nlvtbgr7s.svg","en");
//				userRepo.save(admin1);
////				User user = userRepo.findUserByEmail("readease.webapp@gmail.com").orElseThrow();
////				tokenRepo.deleteTokenByUserID(user.getID());
////				TokenResponse token = driveService.getToken();
////
////				tokenRepo.save(Token.builder()
////								.token(token.getAccessToken())
////								.expriedAt(new Date((new Date()).getTime() + token.getExpiresInSeconds() *1000))
////								.type(TokenType.GG_DRIVE)
////								.user(user)
////						.build());
//
//			}
//		};
//	}

}
