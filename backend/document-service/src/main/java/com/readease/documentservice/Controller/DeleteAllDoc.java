package com.readease.documentservice.Controller;

import com.readease.documentservice.Repo.DocumentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/user/file")
@RequiredArgsConstructor
public class DeleteAllDoc {
    private final DocumentRepo docRepo;

    @DeleteMapping("")
    public ResponseEntity<?> deleteAll(){
        docRepo.deleteAll();
        return new ResponseEntity<>("Ok", HttpStatus.OK);
    }

}
