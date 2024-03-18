package com.readease.documentservice.Repo;

import com.readease.documentservice.Model.Document;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface DocumentRepo extends JpaRepository<Document, Long> {

    //Kiểm tra Collection với ID đầu vào có thuộc bất kỳ User nào không????
    @Query(value = "SELECT count(*) FROM  document where document.userId = ?1 and document.ID = ?2", nativeQuery = true)
    int existDocumentByUserIDAndDocID(String userID, long docID);

    @Query(value ="SELECT count(*) FROM document where userId = ?1 and name = ?2" ,nativeQuery = true)
    int countDocumentByName(String userID, String name);

    //returns Tìm tên tất cả document từ userId
    @Query(value = "select name from document where userId = ?1", nativeQuery = true)
    Set<String> findDocumentNameByUserID(String userID);
    @Query(value = "select * from document where userId = ?1", nativeQuery = true)
    List<Document> getAllDocumentsByUserID(String userID);
    @Transactional
    @Modifying
    @Query(value = "update document set name = ?2 where ID = ?1", nativeQuery = true)
    void updateDocumentNameByID(long docID, String name);

    @Transactional
    @Modifying
    @Query(value = "update document set star = ?2 where ID = ?1", nativeQuery = true)
    void updateDocumentStar(long docID, float star);

    @Transactional
    @Modifying
    @Query(value = "update document set lastRead = ?2,  numberOfPagesReading  = ?3 where ID = ?1", nativeQuery = true)
    void updatePageReadingAndLastRead(long docID,Date date, int numberOfPagesReading);





    @Query(value = "SELECT * " +
            "FROM  document d " +
            "WHERE d.name LIKE %?1% ", nativeQuery = true)
    List<Document> findDocumentByName( String name, String userID);

    @Query(value = "SELECT count(*) " +
            "FROM  document d " +
            "WHERE d.userId = ?1 ", nativeQuery = true)
    int countDocumentByUserId(String userID);

    //Tìm kiếm document theo nhiều tham số
    @Query(value = "SELECT * " +
            " from document " +
            "where  userId = ?1 AND name LIKE %?2%", nativeQuery = true)
    List<Document> findDocumentByUserIDAndName(String userID, String name);
    //Tìm kiếm document theo
    @Query(value = "SELECT d.* " +
            "from document d, collection c, collection_document cd " +
            "where d.userId = ?1 and d.ID = cd.DOCUMENT_ID and c.ID = COLLECTION_ID and c.ID = ?2 AND d.name LIKE %?3% "
            , nativeQuery = true)
    List<Document> findDocumentByColIDAndUserIDAndName(String userID, int colID, String name);


}
