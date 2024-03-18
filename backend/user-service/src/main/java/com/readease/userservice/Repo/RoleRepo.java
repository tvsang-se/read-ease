package com.readease.userservice.Repo;


import com.readease.userservice.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepo extends JpaRepository<Role, Integer> {
    @Override
    Optional<Role> findById(Integer id);
}
