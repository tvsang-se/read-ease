package com.readease.userservice.Service;

import com.readease.userservice.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Admin2Service {
    private final UserRepo userRepo;
    @Autowired
    private RestClient restClient;
//    private final DocumentRepo docRepo;
//    private final AnnotationRepo annotationRepo;

    public HashMap<String, Object> getCountCreatedCustom(String time) {
        return switch (time) {
            case "date" -> this.getCreatedCountByDateOrYear("date");
            case "year" -> this.getCreatedCountByDateOrYear("year");
            case "week" -> this.getCreatedCountByWeekOrMonth("week");
            case "month" -> this.getCreatedCountByWeekOrMonth("month");
            default -> null;
        };
    }

    private HashMap<String, Object> getCreatedCountByDateOrYear(String time) {
        List<Object[]> res = time.equals("date") ? userRepo.countUserCreatedByDate()
                : userRepo.countUserCreatedByYear();
       HashMap<String, Object> result = new HashMap<>();

        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();

        for (Object[] obj : res) {
            HashMap<String, Object> temp = new HashMap<>();
            labels.add(obj[0].toString());
            data.add( ((Number) obj[1]).intValue()) ;
        }
        result.put("labels",labels);
        result.put("data",data);
        return result;
    }

    private HashMap<String, Object> getCreatedCountByWeekOrMonth(String time) {
        List<Object[]> res = time.equals("week") ? userRepo.countUserCreatedByWeek()
                : userRepo.countUserCreatedByMonth();
        HashMap<String, Object> result = new HashMap<>();

        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();
        for (Object[] obj : res) {
            labels.add(obj[0].toString() + " " + obj[1].toString());
            data.add( ((Number) obj[2]).intValue()) ;
        }
        result.put("labels",labels);
        result.put("data",data);
        return result;
    }

    public  List<HashMap<String, Object>> getCountTargetLanguage() {
        List<Object[]> res = userRepo.countTargetLanguage();
        List<HashMap<String, Object>> result = new ArrayList<>();

        for (Object[] obj : res) {
            HashMap<String, Object> temp = new HashMap<>();
            temp.put("targetLanguage", obj[0]);
            temp.put("count", obj[1]);
            result.add(temp);
        }
        return result;

    }

    public List<HashMap<String, Object>> getUserInformation() {

        List<Object[]> res = userRepo.listUserInformation();

        List<HashMap<String, Object>> result = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");

        for (Object[] obj : res) {
            HashMap<String, Object> temp = new HashMap<>();
            Formatter formatter = new Formatter();
            temp.put("ID", obj[0]);
            temp.put("email", obj[1]);
            temp.put("totalAccessTime",Math.round(((Number) obj[2]).longValue() / 3600.0f));
            temp.put("totalCapacity", formatter.format("%.2f",((Number) obj[3]).floatValue()).toString());
            temp.put("createAt", obj[4]);
            temp.put("lastAccess", obj[5]);
            String totalDocsString = restClient.get()
                    .uri("http://localhost:8084/api/user/document/admin/"+obj[0].toString())
                    .retrieve()
                    .body(String.class);
            temp.put("totalDocuments", totalDocsString); //docRepo.getTotalDocumentByUser(obj[0].toString())
            result.add(temp);
        }
        return result;
    }
    private String formatSeconds(long totalSeconds) {
        long days = totalSeconds / 86400; // 86400 seconds in a day
        long hours = (totalSeconds % 86400) / 3600; // 3600 seconds in an hour
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
    }

    public long getTotalDocuments() {
        //docRepo.getTotalDocument();
        return restClient.get()
                .uri("http://localhost:8084/api/user/document/admin/total-document")
                .retrieve()
                .body(Long.class);
    }
    public int getTotalUsers(){
        return userRepo.getTotalUsers();
    }
    public String getTotalCapacity(){
        float total =  userRepo.getTotalCapacity();
        Formatter formatter = new Formatter();
        return formatter.format("%.2f",total).toString();
    }
    public long getTotalAnnotation() {
        //docRepo.getTotalDocument();
        return restClient.get()
                .uri("http://localhost:8084/api/user/document/annotation/admin/total-annotation")
                .retrieve()
                .body(Long.class);
    }





    public HashMap<String, Object> getCountCreatedCustom1(String time, int year) {
        return switch (time) {
            case "date" -> this.getCreatedCountByDateOrYear1("date",year);
            case "year" -> this.getCreatedCountByDateOrYear1("year",year);
            case "week" -> this.getCreatedCountByWeekOrMonth1("week",year);
            case "month" -> this.getCreatedCountByWeekOrMonth1("month",year);
            default -> null;
        };
    }

    private HashMap<String, Object> getCreatedCountByDateOrYear1(String time, int year) {
        List<Object[]> res = time.equals("date") ? userRepo.countUserCreatedByDate1(year)
                : userRepo.countUserCreatedByYear1(year);
        HashMap<String, Object> result = new HashMap<>();

        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();

        for (Object[] obj : res) {
            HashMap<String, Object> temp = new HashMap<>();
            labels.add(obj[0].toString());
            data.add( ((Number) obj[1]).intValue()) ;
        }
        result.put("labels",labels);
        result.put("data",data);
        return result;
    }

    private HashMap<String, Object> getCreatedCountByWeekOrMonth1(String time, int year) {
        List<Object[]> res = time.equals("week") ? userRepo.countUserCreatedByWeek1(year)
                : userRepo.countUserCreatedByMonth1(year);
        HashMap<String, Object> result = new HashMap<>();

        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();
        for (Object[] obj : res) {
            labels.add(obj[0].toString() + " " + obj[1].toString());
            data.add( ((Number) obj[2]).intValue()) ;
        }
        result.put("labels",labels);
        result.put("data",data);
        return result;
    }

    public List<Integer> getListYear() {

        return  userRepo.getListYear();

    }
}
