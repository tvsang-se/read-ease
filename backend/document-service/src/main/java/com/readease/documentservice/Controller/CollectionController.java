package com.readease.documentservice.Controller;

import com.readease.documentservice.Controller.Request.CollectionReq;
import com.readease.documentservice.Model.Collection;
import com.readease.documentservice.Model.Document;
import com.readease.documentservice.Repo.CollectionRepo;
import com.readease.documentservice.Repo.DocumentRepo;
import com.readease.documentservice.Utils.FileUtils;
import jakarta.annotation.Nonnull;
//import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/user/collection/")
@RequiredArgsConstructor
public class CollectionController {
    private final CollectionRepo colRepo;
    private final DocumentRepo docRepo;
//    private final TokenService tokenService;
//
//    private final UserRepo userRepo;
    @GetMapping("/get-all")
    public ResponseEntity<?> getCollectionByID(@RequestHeader String userId){
        return new ResponseEntity<>(colRepo.getAllCollectionByUserID(userId),HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<?> createCollection(@RequestBody CollectionReq req, @RequestHeader String userId){
        //User user = userRepo.findById(req.getUserID()).orElse(null);
        //if(user == null) return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);

        for (Collection col: colRepo.getAllCollectionByUserID(userId)) {
            if(col.getName().equals(req.getCollectionName())){
                return new ResponseEntity<>("The collection name must not be duplicated.", HttpStatus.BAD_REQUEST);
            }
        }

        Collection _col = Collection.builder()
                .name(req.getCollectionName())
                .userId(userId)
                .build();
        //user.addCollection(_col);

        return new ResponseEntity<>(  colRepo.save(_col),HttpStatus.CREATED);
    }
    @PutMapping("/{colId}/add-document/{docId}")
    public ResponseEntity<?> addDocumentIntoCollection(
            @PathVariable("colId") int colID,
            @PathVariable("docId") long docID,
            @RequestHeader String userId
            //@Nonnull HttpServletRequest servletRequest
    ){;//tokenService.getUserID(servletRequest);

        if((docRepo.existDocumentByUserIDAndDocID(userId,docID)) < 1 ||
                colRepo.existCollectionByUserIDAndColID(userId,colID) < 1
        ){
            return new ResponseEntity<>("Document or Collection not found" ,HttpStatus.NOT_FOUND);
        }

        if(colRepo.checkDuplicateDocumentInCollection(colID,docID) == 1)
            return new ResponseEntity<>("Duplicate at ID: " + docID,HttpStatus.NOT_ACCEPTABLE);

        colRepo.addDocumentIntoCollection(colID, docID);

        return new ResponseEntity<>("", HttpStatus.OK);
    }
    @PutMapping("/{colId}/add-document/")
    public ResponseEntity<?> addListDocumentIntoCollection(
            @PathVariable("colId") int colID,
            @RequestHeader String userId,
            //@Nonnull HttpServletRequest servletRequest,
            @RequestBody HashMap<String,Object> req){
        //String userID = tokenService.getUserID(servletRequest);

        Collection col = colRepo.findCollectionNameByIDAndUserID(userId,colID);
        if(col == null)
            return new ResponseEntity<>("Not found collection",HttpStatus.NOT_FOUND);

        List<Integer> docIDList = (List<Integer>) req.get("docIDs");

        for(long ID : docIDList){
           if((docRepo.existDocumentByUserIDAndDocID(userId,ID)) < 1){
               return new ResponseEntity<>("Document ID: " + ID+ " not exist" ,HttpStatus.BAD_REQUEST);
           }
            if(colRepo.checkDuplicateDocumentInCollection(colID,ID) == 1)
                return new ResponseEntity<>("Duplicate at ID: " + ID,HttpStatus.NOT_ACCEPTABLE);
            colRepo.addDocumentIntoCollection(colID, ID);
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @DeleteMapping("/{colId}/remove-document/{docId}")
    public ResponseEntity<?> removeDocumentOutToCollection(@PathVariable("colId") int colID,@PathVariable("docId") long docID){

        Document doc = docRepo.findById(docID).orElse(null);
        Collection collection = colRepo.findById(colID).orElse(null);
        if(doc == null || collection == null)
            return new ResponseEntity<>("Request Invalid", HttpStatus.BAD_REQUEST);

        collection.removeDocument(docID);
        doc.getCollections().remove(collection);
        colRepo.removeDocumentIntoCollection(colID, docID);

        return new ResponseEntity<>(collection, HttpStatus.OK);
    }

    //Xoa list document out to collection
    @PutMapping("/{colId}/remove-document/")
    public ResponseEntity<?> removeListDocumentOutToCollection(
            @PathVariable("colId") int colID,
            @RequestHeader String userId,
            //@Nonnull HttpServletRequest servletRequest,
            @RequestBody HashMap<String,Object> req){
        //String userID = tokenService.getUserID(servletRequest);

        Collection col = colRepo.findCollectionNameByIDAndUserID(userId,colID);
        if(col == null)
            return new ResponseEntity<>("Not found collection",HttpStatus.NOT_FOUND);

        List<Integer> docIDList = (List<Integer>) req.get("docIDs");

        for(long ID : docIDList){
            if((docRepo.existDocumentByUserIDAndDocID(userId,ID)) < 1){
                return new ResponseEntity<>("Document ID: " + ID+ " not exist" ,HttpStatus.BAD_REQUEST);
            }
            colRepo.removeDocumentIntoCollection(colID, ID);
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> renameCollection(@PathVariable("id") int colID,@RequestBody CollectionReq req){
        Collection _col = colRepo.findById(colID).orElse(null);
        if(_col == null)
            return new ResponseEntity<>("Collection not found",HttpStatus.NOT_FOUND);

        Set<String> collectionNames = colRepo.findCollectionNameByUserID(req.getUserId());
        System.out.println("collectionNames: "+collectionNames);
        for (String name: collectionNames) {
            if(name.equals(req.getCollectionName())){
                return new ResponseEntity<>("The document name must not be duplicated.", HttpStatus.BAD_REQUEST);
            }
        }
        _col.setName(req.getCollectionName());
        colRepo.save(_col);

        return new ResponseEntity<>("Update collection successfully!!!",HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCollection(@RequestHeader String userId, @PathVariable("id") int colID){ //HttpServletRequest request,
        //String userID = tokenService.getUserID(request);
        int count = colRepo.existCollectionByUserIDAndColID(userId,colID);
        if(count < 1) return new ResponseEntity<>("Request Valid", HttpStatus.BAD_REQUEST);
        colRepo.deleteById(colID);
        return new ResponseEntity<>("Delete collection successfully!!!",HttpStatus.CREATED);
    }


}
