package com.readease.documentservice.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("annotation")
public class Annotation {
    @Id
    private String id;
    @Field("@context")
    private Object context;
    private String type;
    private  String bodyValue;
    private  String  motivation;
    @Indexed
    private Object target;
    private Object creator;
    private String created;
    private String modified;

    public Annotation(String id, List<String> context, String type, String bodyValue, String motivation, HashMap<String, Object> target, HashMap<String, Object> creator, String created, String modified) {
        this.id = id;
        this.context = context;
        this.type = type;
        this.bodyValue = bodyValue;
        this.motivation = motivation;
        this.target = target;
        this.creator = creator;
        this.created = created;
        this.modified = modified;
    }
}