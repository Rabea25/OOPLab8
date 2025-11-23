package org.example;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JFrame {
    private JPanel rootPanel;
    private UserService userService;
    private User currentUser;
    private CourseService courseService;
    private Analytics analytics;

    public MainPanel(){
        userService = new UserService();
        courseService = new CourseService(userService);
        userService.setCourseService(courseService);
        analytics = new Analytics(userService, courseService);

        this.setTitle("Student Management System");
        this.setContentPane(rootPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 400);
        this.setLocationRelativeTo(null);
        switchPanel(new LoginPanel(this));
        this.setVisible(true);

    }

    public void switchPanel(JPanel panel) {
        rootPanel.removeAll();
        rootPanel.setLayout(new BorderLayout());
        rootPanel.add(panel, BorderLayout.CENTER);
        rootPanel.revalidate();
        rootPanel.repaint();
    }

    public void setCurrentUser(User user) {
        this.setSize(900, 600);
        this.setLocationRelativeTo(null);
        this.currentUser = user;
        if(user.getRole().equals("student")){
            switchPanel(new StudentDashboard(this));
            System.out.println("Switching to student dashboard for user: " + user.getUsername());
        }
        else if(user.getRole().equals("instructor")){
            switchPanel(new InstructorDashboard(this));
            System.out.println("Switching to instructor dashboard for user: " + user.getUsername());
        }
        else{

        }
    }

    public void logout(){
        this.setSize(400, 400);
        this.currentUser = null;
        switchPanel(new LoginPanel(this));
    }

    public UserService getUserService() {
        return userService;
    }

    public CourseService getCourseService() {
        return courseService;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Analytics getAnalytics() {
        return analytics;
    }

}
