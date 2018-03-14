package com.czsm.Demand_Driver.Firebasemodel;

/**
 * Created by macbook on 10/08/16.
 */
public class UserList {

 public String name;
 public String email;
 public String mobileno;
 public String address;
 public String password;
 public String countrycode;
 public String profilepic;

 public UserList(){


 }

 public String getName() {
  return name;
 }

 public String getCountrycode() {
  return countrycode;
 }

 public void setCountrycode(String countrycode) {
  this.countrycode = countrycode;
 }

 public String getProfilepic() {
  return profilepic;
 }

 public void setProfilepic(String profilepic) {
  this.profilepic = profilepic;
 }

 public void setName(String name) {
  this.name = name;
 }

 public String getMobileno() {
  return mobileno;
 }

 public void setMobileno(String mobileno) {
  this.mobileno = mobileno;
 }

 public String getEmail() {
  return email;
 }

 public void setEmail(String email) {
  this.email = email;
 }

 public String getAddress() {
  return address;
 }

 public void setAddress(String address) {
  this.address = address;
 }

 public String getPassword() {
  return password;
 }

 public void setPassword(String password) {
  this.password = password;
 }
}
