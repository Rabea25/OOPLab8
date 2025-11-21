package org.example;

import java.util.List;

public class Admin extends User {
    String id;
    String role;
    String username;
    String email;
    String Password;

    public Admin(String id, String role, String username, String email, String Password) {
        super(id, role, username, email, "ADMIN:");
    }

    public List<Course> getPendingCourses(CourseService courseService) {
        return courseService.getPendingCourses();
    }

    public void approveCourse(String courseId, CourseService courseService) {
        courseService.approveCourse(courseId);
    }

    public void rejectCourse(String courseId, CourseService courseService) {
        courseService.rejectCourse(courseId);
    }
}