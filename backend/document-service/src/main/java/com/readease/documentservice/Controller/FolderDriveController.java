package com.readease.documentservice.Controller;

import com.readease.documentservice.Service.DriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/user/create-folder")
@RequiredArgsConstructor
public class FolderDriveController {
    private final DriveService driveService;

    @PostMapping("/{userId}")
    public String createFolder(@PathVariable String userId) throws GeneralSecurityException, IOException {
        String folderId = driveService.createFolder(userId);
        return  folderId;
    }
}
