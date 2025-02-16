package com.example.cafeticker;

public class Student {
    private String name, username, year, department, image;

    public Student() {} // Fire store requires an empty constructor

    public Student(String name, String username, String year, String department, String image) {
        this.name = name;
        this.username = username;
        this.year = year;
        this.department = department;
        this.image = image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getYear() { return year; }
    public String getDepartment() { return department; }
    public String getImage() { return image; }
}
