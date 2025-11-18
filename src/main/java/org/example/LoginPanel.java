package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private JPanel root;
    private JTextField emailField;
    private JPanel login;
    private JButton loginButton;
    private JButton signUpButton;
    private JPasswordField passwordField;
    private UserService svc;

    public LoginPanel(MainPanel mainPanel) {
        this.setLayout(new BorderLayout());
        this.add(root, BorderLayout.CENTER);
        this.svc = mainPanel.getUserService();

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainPanel.switchPanel(new SignupPanel(mainPanel));
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                if(email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(root, "Please enter both email and password.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String passwordHash = PasswordManager.hashing(password);
                User user = svc.login(email, passwordHash);
                if(user == null) {
                    JOptionPane.showMessageDialog(root, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(root, "Login successful! Welcome " + user.getUsername(), "Success", JOptionPane.INFORMATION_MESSAGE);
                mainPanel.setCurrentUser(user);
                //mainPanel.switchPanel(new StudentDashboardPanel(mainPanel, svc, (Student) user));
            }
        });
    }
}
