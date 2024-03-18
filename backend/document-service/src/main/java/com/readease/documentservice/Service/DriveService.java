package com.readease.documentservice.Service;

import com.readease.documentservice.Config.DriveConfig;
import com.readease.documentservice.Model.Document;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;


@Service

public class DriveService {
    private String clientID = ""
    private String clientSecret = "";

    public TokenResponse getToken() throws GeneralSecurityException, IOException {
        DriveConfig drive = new DriveConfig();
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = drive.getCredentials(HTTP_TRANSPORT);

        TokenResponse accessToken = new TokenResponse();
        accessToken.setAccessToken(credential.getAccessToken());
        accessToken.setExpiresInSeconds(credential.getExpiresInSeconds());

        if (credential.getExpiresInSeconds() < 0L)
            accessToken = refreshAccessToken(credential, HTTP_TRANSPORT);

        return accessToken;
    }

    private TokenResponse refreshAccessToken(Credential credential, HttpTransport transport) throws IOException {
        try {
            System.out.println(clientID + "\n" + clientSecret);
            TokenResponse response = new GoogleRefreshTokenRequest(
                    transport, new GsonFactory(),
                    credential.getRefreshToken(), clientID,
                    clientSecret).execute();
            return response;
        } catch (TokenResponseException e) {
            if (e.getDetails() != null) {
                System.err.println("Error: " + e.getDetails().getError());
                if (e.getDetails().getErrorDescription() != null) {
                    System.err.println(e.getDetails().getErrorDescription());
                }
                if (e.getDetails().getErrorUri() != null) {
                    System.err.println(e.getDetails().getErrorUri());
                }
            } else {
                System.err.println(e.getMessage());
            }
        }
        return null;

    }

    public String createFolder(String userID) throws GeneralSecurityException, IOException {
        File fileMetadata = new File();
//        fileMetadata.setParents(Arrays.asList(parent));
        System.out.println(userID);
        fileMetadata.setName(userID);
//        fileMetadata.setDriveId(DRIVE_ID);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");


        DriveConfig driveConfig = new DriveConfig();
        Drive drive = driveConfig.getInstance();
        File file = drive.files().create(fileMetadata)
                .setSupportsAllDrives(true)
                .setFields("id")
                .execute();
        return file.getId();
    }

    public Document updateFile(String userID, String folderID, String fileName, String fileResource) throws GeneralSecurityException, IOException {
        DriveConfig driveConfig = new DriveConfig();
        Drive drive = driveConfig.getInstance();
        File fileMetadata = new File();
        String fileNameDrive = new String(fileName);
        fileNameDrive = fileNameDrive.substring(0,fileNameDrive.lastIndexOf('.'));
        fileNameDrive += ".pdf";

        fileMetadata.setName(fileNameDrive);
        fileMetadata.setParents(Arrays.asList(folderID));
        java.io.File filePath = new java.io.File(fileResource);

        FileContent mediaContent = new FileContent("application/pdf", filePath);
        File file;


        try {
            file = drive.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            System.out.println("link: " + file.getWebViewLink());
            String url = "https://www.googleapis.com/drive/v3/files/" + file.getId() + "?alt=media";
            String thumbnailLink = "";
            return Document.builder()
                    .name(fileName)
                    .star(-1)
                    .createAt(new Date())
                    .lastRead(new Date())
                    .url(url)
                    .thumbnailLink(thumbnailLink)
                    .numberOfPagesReading(0)
                    .build();
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to upload file: " + e.getDetails());
            throw e;
        }


    }
}
