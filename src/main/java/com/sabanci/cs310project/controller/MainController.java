package com.sabanci.cs310project.controller;

import com.sabanci.cs310project.model.Job;
import com.sabanci.cs310project.model.JobRepository;
import com.sabanci.cs310project.model.User;
import com.sabanci.cs310project.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController // This means that this class is a Controller
@RequestMapping(path="/api") // This means URL's start with /demo (after Application path)
public class MainController {

  static class ForgotPasswordRequest {
    private String email;

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }
  }

  static class Message {
    private String message;
    Message(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }
  }

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JobRepository jobRepository;

  @PostMapping(path="/createUser")
  public @ResponseBody ResponseEntity<Message> addNewUser
          (@RequestBody User user
           ) {

    try {
      userRepository.save(user);
      Message message = new Message("User created successfully.");
      return new ResponseEntity<>(message, HttpStatus.OK);
    }

    catch (Exception e) {
      Message message = new Message(e.getMessage());
      return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/createJob")
  public ResponseEntity<Message> addNewJob(
          @RequestBody Job job) {
    try {
      jobRepository.save(job);
      Message message = new Message("Job created successfully.");
      return new ResponseEntity<>(message, HttpStatus.OK);
    }

    catch (Exception e) {
      Message message = new Message(e.getMessage());
      return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping(path="/getJobs")
  public @ResponseBody Iterable<Job> getJobs() {
    return jobRepository.findAll();
  }

  @GetMapping(path="/getUsers")
  public @ResponseBody Iterable<User> getUsers() {
    return userRepository.findAll();
  }
}