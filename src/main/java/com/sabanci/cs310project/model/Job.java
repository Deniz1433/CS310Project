package com.sabanci.cs310project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
public class Job {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private String id;
    private String username;
    private int job_type;
    private String job_detail;
    private String fee;
    private String contact;
    private String city;

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public int getJob_type() {return job_type;}
    public void setJob_type(int job_type) {this.job_type = job_type;}
    public String getJob_detail() {return job_detail;}
    public void setJob_detail(String job_detail) {this.job_detail = job_detail;}
    public String getFee() {return fee;}
    public void setFee(String fee) {this.fee = fee;}
    public String getContact() {return contact;}
    public void setContact(String contact) {this.contact = contact;}

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}