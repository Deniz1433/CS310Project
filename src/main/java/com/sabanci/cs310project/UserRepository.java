package com.sabanci.cs310project;

import org.springframework.data.repository.CrudRepository;

import com.sabanci.cs310project.User;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserRepository extends CrudRepository<User, Integer> {

}