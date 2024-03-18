package com.readease.documentservice.Controller;

import com.readease.documentservice.Model.Annotation;
import com.readease.documentservice.Model.Document;
import com.readease.documentservice.Repo.AnnotationRepo;
import com.readease.documentservice.Repo.DocumentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


@RestController
@RequestMapping("/api/user/document/annotation")
@RequiredArgsConstructor
public class AnnotationController {
    private final DocumentRepo docRepo;
    private final AnnotationRepo annotationRepo;
    @GetMapping("/{id}")
    public ResponseEntity<?>  getAnnotation(@PathVariable("id") long docID){
        List<Annotation> res = annotationRepo.findAnnotationByDocID(String.valueOf(docID));
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?>  createAnnotation(@RequestBody HashMap<String, Object> req){
        HashMap<String,Object> target = (LinkedHashMap<String,Object>)req.get("target");
        String sourceValue = (String) target.get("source");
        System.out.println(sourceValue + " " + sourceValue.getClass());
        long docID = Long.parseLong(sourceValue);
        Document doc = docRepo.findById(docID).orElse(null);
        if(doc == null)
            return new ResponseEntity<>("Not found document", HttpStatus.NOT_FOUND);

        Annotation annotation = new Annotation(
                (String)req.get("id"),
                req.get("@context"),
                (String) req.get("type"),
                (String)req.get("bodyValue"),
                (String)req.get("motivation"),
                target,
                req.get("creator"),
                (String)req.get("created"),
                (String)req.get("modified")
        );
        annotationRepo.save(annotation);
        return new ResponseEntity<>("Created successfully!!", HttpStatus.CREATED);
    }
    @PutMapping("")
    public ResponseEntity<?>  updateAnnotation( @RequestBody HashMap<String, Object> req){
        HashMap<String,Object> target = (LinkedHashMap<String,Object>)req.get("target");

        String sourceValue = (String) target.get("source");
        System.out.println(sourceValue + " " + sourceValue.getClass());
        long docID = Long.parseLong(sourceValue);

        Document doc = docRepo.findById(docID).orElse(null);
        if(doc == null) return new ResponseEntity<>("Not found document", HttpStatus.NOT_FOUND);
        Annotation annotation = new Annotation(
                (String)req.get("id"),
                req.get("@context"),
                (String) req.get("type"),
                (String)req.get("bodyValue"),
                (String)req.get("motivation"),
                target,
                req.get("creator"),
                (String)req.get("created"),
                (String)req.get("modified")
        );
        annotationRepo.save(annotation);
        return new ResponseEntity<>("Update successfully!!", HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?>  deleteAnnotation(@PathVariable("id") String annotationID){
       annotationRepo.deleteById(annotationID);
//        annotationRepo.save(annotation);
        return new ResponseEntity<>("Delete successfully!!", HttpStatus.OK);
    }
    @GetMapping("/admin/total-annotation")
    public long getTotalAnnotation() {
        return  annotationRepo.count();
    }
}
