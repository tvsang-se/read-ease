package com.readease.userservice.Controller.Admin.Admin2;

import com.readease.userservice.Service.Admin2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/admin2/user-management")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('admin1')")
public class Admin2Controller {
    private final Admin2Service admin2Service;
    @GetMapping("/hello")
    public ResponseEntity<?> hello(){
        return new ResponseEntity<>("Hello admin2", HttpStatus.OK);
    }

    @GetMapping("/count-user-created/")
    public ResponseEntity<?> countUserCreateCustom(@RequestParam("time") String time){
        return new ResponseEntity<>(admin2Service.getCountCreatedCustom(time), HttpStatus.OK);
    }

    @GetMapping("/count-user-created-by-year/")
    public ResponseEntity<?> countUserCreateCustomByYear(
            @RequestParam("time") String time,
            @RequestParam("year") int year
    ){
        return new ResponseEntity<>(admin2Service.getCountCreatedCustom1(time,year), HttpStatus.OK);
    }


    @GetMapping("/getdatahome")
    public ResponseEntity<?> getDataHome(){
        HashMap<String, Object> res = new HashMap<>();
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
}
