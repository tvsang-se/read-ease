package com.readease.documentservice.Repo;

import com.readease.documentservice.Model.Collection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface CollectionRepo extends JpaRepository<Collection, Integer> {

    //Kiểm tra Collection với ID đầu vào có thuộc bất kỳ User nào không????
    @Query(value = "SELECT count(*) FROM collection where collection.userId = ?1 and collection.ID = ?2", nativeQuery = true)
    int existCollectionByUserIDAndColID(String userID, int colId);

    @Query(value = "SELECT * FROM collection where collection.userId = ?1", nativeQuery = true)
    List<Collection> getAllCollectionByUserID(String userID);
    @Query(value = "SELECT * FROM collection c inner join collection_document cd " +
            " where c.ID = cd.COLLECTION_ID and cd.DOCUMENT_ID = ?1", nativeQuery = true)
    List<Collection> getAllCollectionByDocID(long docID);
    //Cập nhật table Collection_Document
    @Transactional
    @Modifying
    @Query(value = "insert into collection_document (COLLECTION_ID,DOCUMENT_ID) values (?1,?2)", nativeQuery = true)
    void addDocumentIntoCollection( int colId,long docId);

    @Transactional
    @Modifying
//    @Query(value = "insert into collection_document (COLLECTION_ID,DOCUMENT_ID) values (?1,?2)", nativeQuery = true)
    @Query(value = "delete from  collection_document where COLLECTION_ID = ?1 and DOCUMENT_ID = ?2 ", nativeQuery = true)
    void removeDocumentIntoCollection( int colId,long docId);

    //returns Tìm tên tất cả collection từ User.ID
    @Query(value = "select name from collection where userId = ?1", nativeQuery = true)
    Set<String> findCollectionNameByUserID(String userID);

    @Query(value = "select * from collection  where userID = ?1 and ID = ?2", nativeQuery = true)
    Collection findCollectionNameByIDAndUserID(String userID, int ID);

    @Query(value = "SELECT count(*) FROM collection_document where COLLECTION_ID = ?1 and DOCUMENT_ID = ?2", nativeQuery = true)
    int checkDuplicateDocumentInCollection(int colID, long docID);

}
