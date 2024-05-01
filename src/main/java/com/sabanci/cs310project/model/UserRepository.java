package com.sabanci.cs310project.model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    @Query("{ 'email' : ?0 }")
    User findByMail(String email);

    @Query(value="{ 'email' : ?0 }", fields="{ 'password' : 1, '_id' : 0 }")
    String findPasswordByEmail(String email);

    @Query(value="{ 'email' : ?0 }", fields="{ 'username' : 1, '_id': 0 }")
    String findNameByEmail(String email);
}