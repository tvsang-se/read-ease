package com.readease.documentservice.Repo;

import com.readease.documentservice.Model.Annotation;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AnnotationRepo extends MongoRepository<Annotation,String> {
    @Query("{'target.source':?0}")
    List<Annotation> findAnnotationByDocID(String docID);
    @DeleteQuery("{ 'target.source' : ?0 }")
    void deleteAnnotationsByDocument(String documentId);

}
