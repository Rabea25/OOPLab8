package org.example;

import java.util.*;

public class UserService {
    private ArrayList<User> users;
    private CourseService courseService;
    public UserService() {
        this.users = new ArrayList<>(JsonDatabaseManager.loadUsers());
    }
    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }


    public User login(String email, String passwordHash) {
        for(User u : users) {
            if(u.getEmail().equals(email) && u.getPasswordHash().equals(passwordHash)) {
                return u;
            }
        }
        return null;
    }
    public User signup(String role, String username, String email, String passwordHash){

        User newUser;
        if(role.equals("student")) newUser = new Student(Utilities.generateStudentId(), "student",  username, email, passwordHash);
        else if(role.equals("instructor")) newUser = new Instructor(Utilities.generateInstructorId(), "instructor", username, email, passwordHash);
        else newUser = new Admin(Utilities.generateAdminId(), "admin", username, email, passwordHash);
        users.add(newUser);
        saveUsers();
        return newUser;
    }


    public User[] getUsers() {
        return users.toArray(new User[0]);
    }
    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }
    public void removeUser(String userId) {
        for(User u : users) if(u.getUserId().equals(userId)) {users.remove(u); break;}
        saveUsers();
    }
    public User getUserByUsername(String username) {
        for(User u : users) if(u.getUsername().equals(username)) return u;
        return null;
    }
    public User getUserById(String userId) {
        for(User u : users) if(u.getUserId().equals(userId)) return u;
        return null;
    }
    public User getUserByEmail(String email) {
        for(User u : users) if(u.getEmail().equals(email)) return u;
        return null;
    }
    public void updateUser(User updatedUser){
        removeUser(updatedUser.getUserId());
        addUser(updatedUser);
        saveUsers();
    }

    public ArrayList<Lesson> getUserLessons(String userId) {
        User user = getUserById(userId);
        if(!(user instanceof Student student)) return null;
        String[] courses = student.getEnrolledCourses();

        ArrayList<Lesson> lessons = new ArrayList<>();
        for(String courseId : courses){
            Course course = courseService.getCourseById(courseId);
            if(course != null){
                for(Lesson lesson : course.getLessons()){
                    lessons.add(lesson);
                }
            }
        }
        return lessons;
    }
    public ArrayList<Lesson> getUserLessons(String userId, String courseId) {
        User user = getUserById(userId);
        if(!(user instanceof Student student)) return null;

        String[] courses = student.getEnrolledCourses();
        if(!Arrays.asList(courses).contains(courseId)) return null;

        Course course = courseService.getCourseById(courseId);

        if(course != null) return new ArrayList<>(Arrays.asList(course.getLessons()));
        return null;
    }

    public void saveUsers() {
        JsonDatabaseManager.writeUsers(users);
    }
}
