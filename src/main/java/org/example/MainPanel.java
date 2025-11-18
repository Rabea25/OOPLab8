package org.example;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JFrame {
    private JPanel rootPanel;
    private UserService svc;
    private User currentUser;
    private CourseService csvc;

    public MainPanel(){
        this.svc = new UserService();
        this.csvc = new CourseService(svc);
        this.setTitle("Student Management System");
        this.setContentPane(rootPanel);
        switchPanel(new LoginPanel(this));
    }

    public void switchPanel(JPanel panel) {
        rootPanel.removeAll();
        rootPanel.setLayout(new BorderLayout());
        rootPanel.add(panel, BorderLayout.CENTER);
        rootPanel.revalidate();
        rootPanel.repaint();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if(user.getRole().equals("student")){
            //switchPanel(new StudentDashboardPanel(this, csvc, svc));
            System.out.println("Switching to student dashboard for user: " + user.getUsername());
        }
        else{
            //switchPanel(new InstructorDashboardPanel(this, svc, csvc));
            System.out.println("Switching to instructor dashboard for user: " + user.getUsername());
        }
    }

    public void logout(){
        this.currentUser = null;
        switchPanel(new LoginPanel(this));
    }

    public UserService getUserService() {
        return svc;
    }

    public CourseService getCourseService() {
        return csvc;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
