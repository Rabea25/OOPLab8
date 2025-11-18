package org.example;

import java.util.ArrayList;

public class Instructor extends User{

    ArrayList<String> createdCourses = new ArrayList<>();

    public Instructor(String userId, String role, String username, String email, String passwordHash) {
        super(userId, role, username, email, passwordHash);
    }

    public void addCourse(String courseId) {
        createdCourses.add(courseId);
    }
    public String[] getCourses() {
        return createdCourses.toArray(new String[0]);
    }
    public void removeCourse(String courseId) {
        createdCourses.remove(courseId);
    }

}
