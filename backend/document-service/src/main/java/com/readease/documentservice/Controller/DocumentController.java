package com.readease.documentservice.Controller;


import com.google.api.client.auth.oauth2.TokenResponse;
import com.readease.documentservice.Controller.Request.DocumentReq;
import com.readease.documentservice.Model.*;
import com.readease.documentservice.Repo.*;
import com.readease.documentservice.Service.DriveService;
import com.readease.documentservice.Utils.FileUtils;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/user/document")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentRepo docRepo;
//    private final UserRepo userRepo;
//    private final TokenRepo tokenRepo;
    private final CollectionRepo colRepo;
    private final AnnotationRepo annotationRepo;
    @Autowired
    private final RestClient restClient;
//    private final TokenService tokenService;

    @GetMapping("/{docID}")
    public ResponseEntity<?> getDocument(@RequestHeader String userId, @PathVariable("docID") long docID){
        //String userID = tokenService.getUserID(servletRequest);
//        System.out.println("Document not found");
        if(docRepo.existDocumentByUserIDAndDocID(userId,docID) < 1) {

            return new ResponseEntity<>("Document not found", HttpStatus.NOT_FOUND);
        }

        List<Annotation> annotationList = annotationRepo.findAnnotationByDocID(String.valueOf(docID));

        return new ResponseEntity<>(new HashMap<String,Object>(){
            {
                put("document",  docRepo.findById(docID));
                put("annotations", annotationList);
            }
        },HttpStatus.OK);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getDocumentsByID(@RequestHeader String userId){
        return new ResponseEntity<>(docRepo.getAllDocumentsByUserID(userId),HttpStatus.OK);
    }

    @GetMapping("/required-upload")
    public ResponseEntity<?> requireDriveAccessToken() throws GeneralSecurityException, IOException, ParseException {
        //Token token = tokenRepo.findGGToken(TokenType.GG_DRIVE.toString()).orElseThrow();

        //Kiem tra access token het han
//        if(token.getExpriedAt().before(new Date())){
//            DriveService driveService = new DriveService();
//            TokenResponse tokenResponse = driveService.getToken();
//            token.setToken(tokenResponse.getAccessToken());
//            token.setExpriedAt(new Date((new Date()).getTime() + tokenResponse.getExpiresInSeconds() *1000 - 10000));
//            tokenRepo.save(token);
//        }
        ArrayList<String> token = FileUtils.readFile();
        // Convert string to date
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        Date expiredAt = formatter.parse(token.get(1));
        String newToken = "";
        if(expiredAt.before(new Date())) {
            DriveService driveService = new DriveService();
            TokenResponse tokenResponse = driveService.getToken();
            newToken = tokenResponse.getAccessToken();
            System.out.println("So sanh: "+newToken.equals(token.get(0)));
            FileUtils.writeFile(newToken,new Date((new Date()).getTime() + tokenResponse.getExpiresInSeconds() *1000 - 10000).toString());
            token.set(0,tokenResponse.getAccessToken());
        }

//        String ggToken = "ya29.a0Ad52N398DmDaUjVgwn53WAtQ9JkV1zPh2uO2itmtzfh_-x-Br3yqT-6OJIt72HyU9E3h1MvlDv9UPI3SiOyNf_dY4HPwMz8nyKWe5PNd3D0IgzAgoh2PbMxe_HP-Bupzy6g6MXMp5gngHMjscEoemOcKREUwqOTusr34swaCgYKAToSARISFQHGX2MiVdYG3Z44G6WOD9RNTxSJ7A0173";
        String finalNewToken = newToken;
        return new ResponseEntity<>(new HashMap<String, String>(){
            {
                put("token", decodeToken((finalNewToken =="")?token.get(0): finalNewToken)); //token.getToken())
            }
        },HttpStatus.OK);
    }
    private String decodeToken(String token) {
        char [] array = token.toCharArray();
        char temp = array[6];
        array[6] = array[9];
        array[9] = temp;
        return new String(array);
    }

    @GetMapping("")
    public ResponseEntity<?> getDocumentAtPageNumberWithSize(
//            @Nonnull  HttpServletRequest request,
             @RequestHeader String userId,
             @RequestParam("page") int page,
             @RequestParam("size") int size,
             @RequestParam("sortBy") String sortBy, // name, lastRead, createAt, star
             @RequestParam("sortOrder") String sortOrder, // desc (giảm dần) | asc (tăng dần)
             @RequestParam("name") String name,
             @RequestParam("collectionID") int colID){
        //String userID = //tokenService.getUserID(request);
        List<Document> documentCustom = null;

        if(colID == 0){//Trường hợp không truy vấn document theo Collection
            documentCustom = docRepo.findDocumentByUserIDAndName( userId, name);
        }
        else {
            documentCustom = docRepo.findDocumentByColIDAndUserIDAndName( userId,  colID, name);
        }
        //Sắp xếp dữ liệu theo sortBy, sortOrder
       sortResponseData(sortBy, sortOrder, documentCustom);


        return  new ResponseEntity<>(extractResponseData(documentCustom,page,size),HttpStatus.OK);
    }

    private void sortResponseData(String sortBy, String sortOrder,  List<Document> src) {

        if(sortBy.equals("name"))
            Collections.sort(src,Comparator.comparing(Document::getName));
        else if(sortBy.equals("lastRead"))
            Collections.sort(src,Comparator.comparing(Document::getLastRead));
        else if(sortBy.equals("createAt"))
            Collections.sort(src,Comparator.comparing(Document::getCreateAt));
        else if(sortBy.equals("star"))
            Collections.sort(src,Comparator.comparing(Document::getStar));

        if(sortOrder.equals("desc"))
            Collections.reverse(src);
    }

    private List <HashMap<String, Object>> extractResponseData (List<Document> res, int page, int size){
       if(res == null)
           return null;
        List<HashMap<String, Object>> result = new ArrayList<>();

        HashMap<String, Object> totalBooks = new HashMap<>();
        totalBooks.put("totalBooks",res.size());
        result.add(totalBooks);

        //Phân trang tài liệu
        res = res.stream()
                .skip((page-1)*size)
                .limit(size)
                .collect(Collectors.toList());
        //Duyệt từng phần tử, add vào response
        for (Document doc: res) {
            float percent = 0;
            percent = Math.round((doc.getNumberOfPagesReading() * 1.0f / doc.getTotalPages()) * 100);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");

            HashMap<String, Object> temp = new HashMap<>();
            temp.put("ID",doc.getID());
            temp.put("name", doc.getName());
            temp.put("totalPages",doc.getTotalPages());
            temp.put("numberOfPagesReading", doc.getNumberOfPagesReading());
            temp.put("percentPagesRead", percent);
            temp.put("star",doc.getStar());
            temp.put("createAt",dateFormat.format( doc.getCreateAt()));
            temp.put("lastRead", dateFormat.format(doc.getLastRead()));
            temp.put("url", doc.getUrl());
            temp.put("thumbnailLink",doc.getThumbnailLink());

            result.add(temp);
        }

        return result;
    }


    @PostMapping("/")
    public ResponseEntity<?> createDocument(@RequestHeader String userId, @NotNull @RequestBody DocumentReq req) {
//        User user = userRepo.findById(req.getUserID()).orElse(null);
//        if(user == null)
//            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

        for (String docName: docRepo.findDocumentNameByUserID(userId)) {
            if(docName.equals(req.getName())){
                return new ResponseEntity<>("The document name must not be duplicated.", HttpStatus.BAD_REQUEST);
            }
        }
            Document doc = Document.builder()
                    .name(req.getName())
                    .url(req.getUrl())
                    .thumbnailLink(req.getThumbnailLink())
                    .size(req.getSize())
                    .totalPages(req.getTotalPages())
                    .createAt(new Date())
                    .lastRead(new Date())
                    .star(-1)
                    .numberOfPagesReading(0)
                    .userId(userId)
                    .build();
//            user.setTotalCapacity(user.getTotalCapacity() + doc.getSize());
        restClient.put()
                .uri("http://USER-SERVICE/api/user/account/update-capacity?userId="+userId+"&sizeOfNewDocument="+doc.getSize())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);
//        System.out.println("Create document: "+result);
//            user.getDocuments().add(doc);
            docRepo.save(doc);
//            userRepo.save(user);

        return new ResponseEntity<>(doc, HttpStatus.CREATED);
    }

    @GetMapping("/check-duplicate-name")
    public ResponseEntity<?> checkDuplicateName(@RequestHeader String userId ,@RequestParam("name") String name){ //@Nonnull HttpServletRequest httpServletRequest
        //String userID = tokenService.getUserID(httpServletRequest);
        if(docRepo.countDocumentByName(userId,name) > 0){
            return new ResponseEntity<>("The document name must not be duplicated.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("",HttpStatus.OK);
    }

    @PutMapping("/rename")
    public ResponseEntity<?> renameDocument(
            //@Nonnull HttpServletRequest httpServletRequest,
            @RequestHeader String userId,
            @RequestBody HashMap<String, Object> docReq
    ){
        //String userID = tokenService.getUserID(httpServletRequest);
        Long docID = ((Integer) docReq.get("ID")).longValue();
        //Kiểm tra tài liệu có phải thuộc user
        if(docRepo.existDocumentByUserIDAndDocID(userId,docID) < 1){
            return new ResponseEntity<>("Not found document", HttpStatus.NOT_FOUND);
        }
        //Kiểm tra tên trùng
        if(docRepo.countDocumentByName(userId,(String)docReq.get("name")) > 0){
            return new ResponseEntity<>("The document name must not be duplicated.", HttpStatus.BAD_REQUEST);
        }
        docRepo.updateDocumentNameByID(docID, (String) docReq.get("name"));
        return new ResponseEntity<>("Rename successfully!!",HttpStatus.OK);
    }


    @PutMapping("/update/{docID}")
    public ResponseEntity<?> updateDocument(
            //@Nonnull HttpServletRequest httpServletRequest,
            @RequestHeader String userId,
            @PathVariable("docID") long docID,
            @RequestBody Document docReq
    ){
        //String userID = tokenService.getUserID(httpServletRequest);

        if(docRepo.existDocumentByUserIDAndDocID(userId,docID) < 1){
            return new ResponseEntity<>("Not found document", HttpStatus.NOT_FOUND);
        }

        docRepo.updatePageReadingAndLastRead(docID,new Date(),docReq.getNumberOfPagesReading());
        return new ResponseEntity<>("Update successfully!!",HttpStatus.OK);
    }

    @PutMapping("/rating/{docID}")
    public ResponseEntity<?> updateDocumentStar(
            //@Nonnull HttpServletRequest httpServletRequest,\
            @RequestHeader String userId,
            @PathVariable("docID") long docID,
            @RequestBody Document docReq
    ){
        //String userID = tokenService.getUserID(httpServletRequest);

        if(docRepo.existDocumentByUserIDAndDocID(userId,docID) < 1){
            return new ResponseEntity<>("Not found document", HttpStatus.NOT_FOUND);
        }

        docRepo.updateDocumentStar(docID,docReq.getStar());
        return new ResponseEntity<>("Update star successfully!!",HttpStatus.OK);
    }
    @GetMapping("/{docID}/get-collection")
    public ResponseEntity<?> getCollection(
            @PathVariable("docID") int docID,
            //@Nonnull HttpServletRequest servletRequest
            @RequestHeader String userId
    ){
        //String userID = tokenService.getUserID(servletRequest);

        return new ResponseEntity<>(colRepo.getAllCollectionByDocID(docID), HttpStatus.OK);
    }

    @PutMapping("/{docID}/remove-out-list-collection/")
    public ResponseEntity<?> removeDocumentOutToListCollection(
            @PathVariable("docID") int docID,
            //@Nonnull HttpServletRequest servletRequest,
            @RequestHeader String userId,
            @RequestBody HashMap<String,Object> req)
    {
        //String userID = tokenService.getUserID(servletRequest);

        int cnt = docRepo.existDocumentByUserIDAndDocID(userId,docID);
        if(cnt < 1)
            return new ResponseEntity<>("Not found collection",HttpStatus.NOT_FOUND);

        List<Integer> docIDList = (List<Integer>) req.get("colIDs");

        for(int ID : docIDList){
            if(colRepo.existCollectionByUserIDAndColID(userId,ID) < 1){
                return new ResponseEntity<>("Collection ID: " + ID+ " not exist" ,HttpStatus.BAD_REQUEST);
            }
            colRepo.removeDocumentIntoCollection(ID, docID);
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }
    @PutMapping("/{docID}/add-into-list-collection/")
    public ResponseEntity<?> addDocumentIntoListCollection(
            @PathVariable("docID") int docID,
            //@Nonnull HttpServletRequest servletRequest,
            @RequestHeader String userId,
            @RequestBody HashMap<String,Object> req){
        //String userID = tokenService.getUserID(servletRequest);

        int cnt = docRepo.existDocumentByUserIDAndDocID(userId,docID);
        if(cnt < 1)
            return new ResponseEntity<>("Not found collection",HttpStatus.NOT_FOUND);

        List<Integer> colIDList = (List<Integer>) req.get("colIDs");

        for(int ID : colIDList){
            if(colRepo.existCollectionByUserIDAndColID(userId,ID) < 1){
                return new ResponseEntity<>("Collection ID: " + ID+ " not exist" ,HttpStatus.BAD_REQUEST);
            }
            if(colRepo.checkDuplicateDocumentInCollection(ID,docID) == 1)
                return new ResponseEntity<>("The collection_document must not be duplicated.", HttpStatus.BAD_REQUEST);
            colRepo.addDocumentIntoCollection(ID, docID);
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{docID}")
    public ResponseEntity <?> deleteDocument(@RequestHeader String userId ,@PathVariable("docID") long docID){ //@Nonnull HttpServletRequest request
        //String userID = tokenService.getUserID(request);
//        User user = userRepo.findById(userID).orElse(null);

        //Kiểm tra document có thuộc user không
        int count = docRepo.existDocumentByUserIDAndDocID(userId,docID);
        if(count < 1)
            return new ResponseEntity<>("Request Valid", HttpStatus.BAD_REQUEST);
        Document doc = docRepo.findById(docID).orElse(null);
        //Trigger annotation
        annotationRepo.deleteAnnotationsByDocument(String.valueOf(docID));

//        userRepo.updateUserTotalCapacityBeforeDeleteDoc(userId,doc.getSize());
        String totalCapacity = restClient.put()
                .uri("http://USER-SERVICE/api/user/account/update-capacity?userId="+userId+"&sizeOfNewDocument="+doc.getSize()*(-1))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);
        docRepo.deleteById(docID);

//        User resUser = userRepo.findById(userID).orElse(null);

        return new ResponseEntity<>(new HashMap<String, Object> () {
            {
                put("totalCapacity", totalCapacity); //resUser.getTotalCapacity()
            }
        },HttpStatus.OK);
    }

    @GetMapping("/admin/{userId}")
    public String countDocumentByUserId(@PathVariable String userId) {
        return  String.valueOf(docRepo.countDocumentByUserId(userId));
    }

    @GetMapping("/admin/total-document")
    public long getTotalDocument() {
        return  docRepo.count();
    }

}
