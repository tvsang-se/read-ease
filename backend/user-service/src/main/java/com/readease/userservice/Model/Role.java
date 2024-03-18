package com.readease.userservice.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name="role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {
    @Id
    private int ID;
    @Column(nullable = false)
    private String role;

}
