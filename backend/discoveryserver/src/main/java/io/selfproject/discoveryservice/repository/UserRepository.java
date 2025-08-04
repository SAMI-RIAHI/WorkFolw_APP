package io.selfproject.discoveryservice.repository;


import io.selfproject.discoveryservice.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    User getUserByUsername(String username);
}
