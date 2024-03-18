package com.readease.documentservice.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="collection")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Collection {
    @Id
    @GeneratedValue
    private int ID;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private  String userId;
    @ManyToMany(fetch = FetchType.LAZY,
                cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
                })
    @JoinTable(name="collection_document",
            joinColumns = @JoinColumn(name= "COLLECTION_ID"),
            inverseJoinColumns = @JoinColumn(name = "DOCUMENT_ID")
    )
    private Set<Document> documents = new HashSet<>();

    public void addDocument(Document document){
        this.documents.add(document);
        document.getCollections().add(this);
    }

    public void removeDocument(long documentID) {
        Document doc = this.documents.stream()
                .filter(d -> d.getID() == documentID).findFirst()
                .orElse(null);
        if(doc != null){
            this.documents.remove(doc);
            doc.getCollections().remove(this);
        }
    }
}
