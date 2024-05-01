package com.sabanci.cs310project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
public class User {

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private String id;
  private String username;
  private String email;
  private String password;
  private String birthday;
  private String phone;
  private String bar_city;
  private int bar_id;

  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getUsername() {return username;}
  public void setUsername(String username) {this.username = username;}
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getPassword() {return this.password; }
  public void setPassword(String password) {this.password = password;}
  public String getBirthday() {return this.birthday; }
  public void setBirthday(String birthday) {this.birthday = birthday;}
  public String getPhone() {return this.phone; }
  public void setPhone(String phone) {this.phone = phone;}
  public String getBar_city() {return this.bar_city; }
  public void setBar_city(String bar_city) {this.bar_city = bar_city;}
  public int getBar_id() {return this.bar_id; }
  public void setBar_id(int bar_id) {this.bar_id = bar_id;}
}