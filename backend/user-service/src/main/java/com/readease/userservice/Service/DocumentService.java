package com.readease.userservice.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readease.userservice.Controller.User.Response.Collection;
import com.readease.userservice.Controller.User.Response.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    @Autowired
    private RestClient restClient;
    @Autowired
    private ObjectMapper objectMapper;

    public Document getLastReadingDocument(String userId) {
        Set<Document> documents = restClient.get()
                .uri("http://localhost:8084/api/user/document/get-all")
                .header("userId",userId)
                .exchange((request, response) -> {
                    return objectMapper.readValue(response.getBody(), new TypeReference<>() {});
                });
        Optional<Document> latestDocument = documents.stream()
                .filter(doc -> doc.getLastRead() != null)
                .max(Comparator.comparing(Document::getLastRead));

        return latestDocument.orElse(null);
    }

    public Set<Document> getDocumentCustom(String userId, int skip, int limit) {
        Set<Document> documents = restClient.get()
                .uri("http://localhost:8084/api/user/document/get-all")
                .header("userId",userId)
                .exchange((request, response) -> {
                    return objectMapper.readValue(response.getBody(), new TypeReference<>() {});
                });
        Comparator<Document> descendingComparator =
                (doc1, doc2) -> doc2.getCreateAt().compareTo(doc1.getLastRead());

        TreeSet<Document> sortedSet = new TreeSet<>(descendingComparator);

        sortedSet.addAll(documents);

        return  sortedSet.stream()
                .skip(skip)
                .limit(limit)
                .collect(Collectors.toSet());
    }

    public Set<HashMap<String, Object>> getCollectionCustom(String userId) {
        Set<Collection> collections = restClient.get()
                .uri("http://localhost:8084/api/user/collection/get-all")
                .header("userId",userId)
                .exchange((request, response) -> {
                    return objectMapper.readValue(response.getBody(), new TypeReference<>() {});
                });
        Set<HashMap<String, Object>> result = new HashSet<>();
        for (Collection col : collections) {
            result.add(new HashMap<String, Object>() {
                {
                    put("ID", col.getID());
                    put("name", col.getName());
                    put("quantity", col.getDocuments().size());
                }
            });
        }
        return result;
    }

    public int countDocument(String userId) {
        Set<Document> documents = restClient.get()
                .uri("http://localhost:8084/api/user/document/get-all")
                .header("userId",userId)
                .exchange((request, response) -> {
                    return objectMapper.readValue(response.getBody(), new TypeReference<>() {});
                });
        return documents.size();
    }

}
