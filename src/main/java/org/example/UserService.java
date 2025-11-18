package org.example;

import java.util.ArrayList;

public class UserService {
    private ArrayList<User> users;

    public UserService() {
        this.users = new ArrayList<>(JsonDatabaseManager.loadUsers());
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
        if(role.equals("student")) newUser = new Student(GenerateID.generateStudentId(), "student",  username, email, passwordHash);
        else newUser = new Instructor(GenerateID.generateInstructorId(), "instructor", username, email, passwordHash);

        users.add(newUser);
        JsonDatabaseManager.writeUsers(users);
        return newUser;
    }


    public User[] getUsers() {
        return users.toArray(new User[0]);
    }
    public void addUser(User user) {
        users.add(user);
        JsonDatabaseManager.writeUsers(users);
    }
    public void removeUser(String userId) {
        for(User u : users) if(u.getUserId().equals(userId)) {users.remove(u); break;}
        JsonDatabaseManager.writeUsers(users);
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
        JsonDatabaseManager.writeUsers(users);
    }

    public void saveUsers() {
        JsonDatabaseManager.writeUsers(users);
    }
}
