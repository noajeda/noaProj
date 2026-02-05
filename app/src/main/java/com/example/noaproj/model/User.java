package com.example.noaproj.model;

public class User {
    String id;
    String fName;
    String lName;
    String phone;
    String email;
    String password;
    String age;
    String gender;
    String city;
    Boolean isAdmin;

    public User(String age, String city, String email, String fName, String gender, String id, String lName, String password, String phone) {
        this.age = age;
        this.city = city;
        this.email = email;
        this.fName = fName;
        this.gender = gender;
        this.id = id;
        this.lName = lName;
        this.password = password;
        this.phone = phone;
        this.isAdmin = false;
    }

    public  User(User  user){

        this.id = user.id;
        this.email = user.getEmail();
        this.fName = user.getfName();


        this.lName = user.getlName();

        this.phone = user.getPhone();

    }


    public User() {
    }
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getId() {
        return  this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "User{" +
                "age='" + age + '\'' +
                ", id='" + id + '\'' +
                ", fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", gender='" + gender + '\'' +
                ", city='" + city + '\'' +
                ", isAdmin='" + isAdmin + '\'' +
                '}';
    }
}