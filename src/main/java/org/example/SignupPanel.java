package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignupPanel extends JPanel{
    private JTextField usernameField;
    private JTextField emailField;
    private JPanel signup;
    private JPanel root;
    private JButton loginButton;
    private JButton signupButton;
    private JPasswordField passwordField;
    private JRadioButton instructorRadioButton;
    private JRadioButton studentRadioButton;
    private JRadioButton adminRadioButton;
    private UserService svc;
    private ButtonGroup role;

    public SignupPanel(MainPanel mainPanel) {
        this.svc = mainPanel.getUserService();
        this.setLayout(new BorderLayout());
        this.add(root, BorderLayout.CENTER);

        role = new ButtonGroup();
        role.add(instructorRadioButton);
        role.add(studentRadioButton);
        role.add(adminRadioButton);
        instructorRadioButton.setActionCommand("instructor");
        studentRadioButton.setActionCommand("student");
        adminRadioButton.setActionCommand("admin");
        studentRadioButton.setSelected(true);


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainPanel.switchPanel(new LoginPanel(mainPanel));
            }
        });
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                if(username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(root, "Please fill all the fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(svc.getUserByEmail(email) != null) {
                    JOptionPane.showMessageDialog(root, "Email is already registered.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(svc.getUserByUsername(username) != null) {
                    JOptionPane.showMessageDialog(root, "Username is already taken.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(!Validations.validatePassword(password)){
                    JOptionPane.showMessageDialog(root, "Password must contain at least 4 characters, one uppercase letter, one lowercase letter and a number", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(!Validations.validateEmail(email)){
                    JOptionPane.showMessageDialog(root, "Please enter a valid e-mail.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String selectedRole = role.getSelection().getActionCommand();
                String passwordHash = Utilities.hashPassword(password);
                User newUser = svc.signup(selectedRole, username, email, passwordHash);

                JOptionPane.showMessageDialog(root, "Signup successful! You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                mainPanel.switchPanel(new LoginPanel(mainPanel));

            }
        });
    }
}
