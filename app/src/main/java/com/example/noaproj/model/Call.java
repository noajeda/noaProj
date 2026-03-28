package com.example.noaproj.model;

public class Call {
    String id;
    Job job;
    long time;
    User user;

    public Call(String id, Job job, long time, User user) {
        this.id = id;
        this.job = job;
        this.time = time;
        this.user = user;
    }


    public Call(String id,  long time, User user) {
        this.id = id;

        this.time = time;
        this.user = user;
    }

    public Call(String id, Job job, long time) {
        this.id = id;

        this.time = time;
        this.job=job;

    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Call() {
    }





    @Override
    public String toString() {
        return "Call{" +
                "id='" + id + '\'' +
                ", job=" + job +
                ", time=" + time +
                ", user=" + user +
                '}';
    }
}
