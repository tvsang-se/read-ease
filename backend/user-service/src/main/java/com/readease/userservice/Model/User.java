package com.readease.userservice.Model;


import com.readease.userservice.Config.StringPrefixedSequenceIdGenerator;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;


@Entity
@Data
@Table (name="user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @GenericGenerator(
            name = "user_seq",
            strategy = "com.readease.userservice.Config.StringPrefixedSequenceIdGenerator",
            parameters = {
                @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "1"),
                @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "RE"),
                @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%08d") })

    @Column(length = 10)
    private String ID;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String avatar;
    @Column(nullable = false)
    private Date lastAccess;
    @Column(nullable = false)
    private Date createAt;
    @Column(nullable = false)
    private long totalAccessTime;
    @Column(nullable = false)
    private float totalCapacity;

    @Column(nullable = false)
    private String targetLanguage;

    @Column(nullable = false)
    private String idDriveFolder;

    @ManyToOne
    @JoinColumn(name = "ROLE_ID",nullable = false)
    private Role role;

    public User(String email, String password, Role role,String avatar, String targetLanguage) {
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.lastAccess = new Date();
        this.createAt = new Date();
        this.totalAccessTime = 0;
        this.role = role;
        this.idDriveFolder = "";
        this.targetLanguage = targetLanguage;
    }
    public User(String ID){
        this.ID = ID;
    }
    public User(String ID, String email, String password, Role role){
        this.ID = ID;
        this.email = email;
        this.password = password;
        this.avatar = "";
        this.lastAccess = new Date();
        this.createAt = new Date();
        this.totalAccessTime = 0;
        this.totalCapacity  = 0;
        this.role = role;
    }



//    public Set<Document> getDocumentsSortedByLastReadDesc() {
//        Comparator<Document> descendingComparator =
//                (doc1, doc2) -> doc2.getCreateAt().compareTo(doc1.getLastRead());
//
//        TreeSet<Document> sortedSet = new TreeSet<>(descendingComparator);
//
//        sortedSet.addAll(documents);
//        return sortedSet;
//    }
//
//    public Set<Document> getDocumentCustom(int skip, int limit){
//        return  getDocumentsSortedByLastReadDesc().stream()
//                .skip(skip)
//                .limit(limit)
//                .collect(Collectors.toSet());
//    }
//
//    public Document getLastReadingDocument(){
//
//        Optional<Document> latestDocument = documents.stream()
//                .filter(doc -> doc.getLastRead() != null)
//                .max(Comparator.comparing(Document::getLastRead));
//
//        return latestDocument.orElse(null);
//    }
//
//
//    public void addCollection(Collection collection){
//        this.collections.add(collection);
//    }
//    public void removeCollection(int  collectionID){
//        this.collections.stream()
//                .filter(c -> c.getID() == collectionID).findFirst()
//                .ifPresent(col -> this.collections.remove(col));
//    }



    //region User Detail

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.getRole()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    //endregion


}

