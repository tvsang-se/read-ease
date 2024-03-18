package com.readease.userservice.Repo;

import com.readease.userservice.Model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, String> {
    Optional<User> findUserByEmail(String email);


    @Transactional
    @Modifying
    @Query(value = "update user set totalCapacity = totalCapacity - ?2 where ID = ?1", nativeQuery = true)
    void  updateUserTotalCapacityBeforeDeleteDoc(String userID, float totalCapacity);

    @Transactional
    @Modifying
    @Query(value = "update user set totalCapacity = totalCapacity + ?2 where ID = ?1", nativeQuery = true)
    void  updateUserTotalCapacityBeforeAddDoc(String userID, float totalCapacity);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user u SET u.password = :pwd WHERE u.email = :email ", nativeQuery = true)
    void updatePasswordByEmail(@Param("email") String email, @Param("pwd") String pwd);

    @Query(value = "SELECT count(*) FROM user u where u.email = ?1", nativeQuery = true)
    int countUserByEmail(String email);

    @Query(value = "SELECT ID FROM user u where u.email = ?1", nativeQuery = true)
    String findUserIDByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "SELECT * FROM user t WHERE t.ROLE_ID= :roleID", nativeQuery = true)
    List<User> findUserByRole(@Param("roleID") int roleID);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user t " +
            " SET t.lastAccess = :date , t.totalAccessTime = :ttTime " +
            "WHERE t.email = :email", nativeQuery = true)
    int updateLastAccessByEmail(@Param("email") String email, @Param("date") Date date, @Param("ttTime") long ttTime);


    //Cập nhật mật khẩu
//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE user u SET u.password = :pwd WHERE u.email = :email ", nativeQuery = true)
//    void updatePasswordByEmail(@Param("email") String email, @Param("pwd") String pwd);
//    @Query(value = "SELECT count(*) FROM user u where u.email = ?1", nativeQuery = true)
//    int countUserByEmail(String email);

    @Query(value = "select DATE(createAt) as date, count(*) as count " +
            "from user " +
            "where ROLE_ID = 1 " +
            "group by date " +
            "order by DATE ", nativeQuery = true)
    List< Object[]> countUserCreatedByDate();


    @Query(value = "SELECT EXTRACT(YEAR FROM createAt) AS createYear," +
            "    EXTRACT(WEEK FROM createAt) AS createWeek," +
            "    COUNT(*) AS count" +
            "    FROM user" +
            "    WHERE ROLE_ID = 1" +
            "    GROUP BY createYear, createWeek" +
            "    ORDER BY createYear, createWeek", nativeQuery = true)
    List< Object[]> countUserCreatedByWeek();

    @Query(value = "SELECT EXTRACT(YEAR FROM createAt) AS createYear," +
            "    EXTRACT(MONTH FROM createAt) AS createMonth," +
            "    COUNT(*) AS count" +
            "    FROM user" +
            "    WHERE ROLE_ID = 1" +
            "    GROUP BY createYear, createMonth" +
            "    ORDER BY createYear, createMonth", nativeQuery = true)
    List< Object[]> countUserCreatedByMonth();
    @Query(value = "SELECT EXTRACT(YEAR FROM createAt) AS createYear," +
            "    COUNT(*) AS count" +
            "    FROM user" +
            "    WHERE ROLE_ID = 1" +
            "    GROUP BY createYear" +
            "    ORDER BY createYear", nativeQuery = true)
    List< Object[]> countUserCreatedByYear();

    @Query(value = "select ID, email, totalAccessTime, totalCapacity, createAt, lastAccess " +
            "    from user" +
            "    where ROLE_ID = 1", nativeQuery = true)
    List<Object[]> listUserInformation();

    @Query(value = "SELECT targetLanguage, COUNT(*) AS languageCount " +
            "FROM user  WHERE ROLE_ID = 1 " +
            "GROUP BY targetLanguage ", nativeQuery = true)
    List<Object[]> countTargetLanguage();


    @Query(value = "select count(*) from user where ROLE_ID = 1", nativeQuery = true)
    int getTotalUsers();

    @Query(value = "select sum(totalCapacity) from user where ROLE_ID = 1", nativeQuery = true)
    float getTotalCapacity();

//    @Query(value = "SELECT ID FROM user u where u.email = ?1", nativeQuery = true)
//    String findUserIDByEmail(String email);


    @Query(value = "SELECT * from user where ROLE_ID = 3", nativeQuery = true)
    List<User> getAllAdmin2();



    @Query(value = "select DATE(createAt) as date, count(*) as count " +
            "from user " +
            "where ROLE_ID = 1 and YEAR(createAt) = ?1 " +
            "group by date " +
            "order by DATE ", nativeQuery = true)
    List< Object[]> countUserCreatedByDate1(int year);
    @Query(value = "SELECT EXTRACT(YEAR FROM createAt) AS createYear," +
            "    EXTRACT(WEEK FROM createAt) AS createWeek," +
            "    COUNT(*) AS count" +
            "    FROM user" +
            "    WHERE ROLE_ID = 1 and YEAR(createAt) = ?1 " +
            "    GROUP BY createYear, createWeek" +
            "    ORDER BY createYear, createWeek", nativeQuery = true)
    List< Object[]> countUserCreatedByWeek1(int year);

    @Query(value = "SELECT EXTRACT(YEAR FROM createAt) AS createYear," +
            "    EXTRACT(MONTH FROM createAt) AS createMonth," +
            "    COUNT(*) AS count" +
            "    FROM user" +
            "    WHERE ROLE_ID = 1 and YEAR(createAt) = ?1 " +
            "    GROUP BY createYear, createMonth" +
            "    ORDER BY createYear, createMonth", nativeQuery = true)
    List< Object[]> countUserCreatedByMonth1(int year);

    @Query(value = "SELECT EXTRACT(YEAR FROM createAt) AS createYear," +
            "    COUNT(*) AS count" +
            "    FROM user" +
            "    WHERE ROLE_ID = 1 and YEAR(createAt) = ?1 " +
            "    GROUP BY createYear" +
            "    ORDER BY createYear", nativeQuery = true)
    List< Object[]> countUserCreatedByYear1(int year);


    @Query(value = "SELECT DISTINCT YEAR(createAt) AS year FROM user ORDER BY year desc" , nativeQuery = true)
    List<Integer> getListYear();

}
