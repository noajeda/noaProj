package com.example.noaproj.model;

public class Job {
    String id;
    String title;
    String type;
    String city;
    String address;
    String phone;
    String age;
    String details;
    User user;



    String company;
    String status;


    public Job(String address, String age, String city, String company, String details, String id, String phone, String title, String type, User user) {
        this.address = address;
        this.age = age;
        this.city = city;
        this.company = company;
        this.details = details;
        this.id = id;
        this.phone = phone;
        this.title = title;
        this.type = type;
        this.user = user;
        this.status = "new";
    }

    public Job(String address, String age, String city, String company, String details, String id, String phone, String status, String title, String type, User user) {
        this.address = address;
        this.age = age;
        this.city = city;
        this.company = company;
        this.details = details;
        this.id = id;
        this.phone = phone;
        this.status = status;
        this.title = title;
        this.type = type;
        this.user = user;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Job() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Job{" +
                "address='" + address + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", city='" + city + '\'' +
                ", phone='" + phone + '\'' +
                ", age='" + age + '\'' +
                ", details='" + details + '\'' +
                ", user=" + user +
                ", company='" + company + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}