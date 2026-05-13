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
    boolean isAdmin;

    public User() {
    }

    public User(String id, String fName, String lName, String phone, String email, String password, String age, String gender, String city, boolean isAdmin) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.age = age;
        this.gender = gender;
        this.city = city;
        this.isAdmin = isAdmin;
    }

    public User(String id, String fName, String lName, String phone, String email, String password, String age, String gender, String city) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.age = age;
        this.gender = gender;
        this.city = city;
        this.isAdmin = false;
    }

    public User(User  user){
        if (user == null) return;
        this.id = user.getId();
        this.email = user.getEmail();
        this.fName = user.getfName();
        this.lName = user.getlName();
        this.phone = user.getPhone();
        this.isAdmin= user.getIsAdmin();
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
    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
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