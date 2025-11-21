package org.example;

import javax.swing.*;
import java.util.Arrays;

public class AddLessonDialog extends JDialog{
    private JPanel root;
    private JButton saveButton;
    private JButton cancelButton;
    private JTextField titleField;
    private JTextField contentField;
    private JLabel courseLabel;
    private JTextField resourceField;
    private boolean saved = false;

    public AddLessonDialog(Course course, CourseService courseService) {


        courseLabel.setText(course.getCourseId());

        cancelButton.addActionListener(e -> dispose());

        saveButton.addActionListener(e -> {
            String title  = titleField.getText().trim();
            String content = contentField.getText().trim();
            String[] res = resourceField.getText().trim().split(",");

            Lesson b = courseService.getLessonByTitle(course.getCourseId(), title);

            if (!Validations.isNonEmpty(title) || !Validations.isValidTitle(title) || b != null) {
                JOptionPane.showMessageDialog(root,"Title cannot be empty, must be at least 4 characters, and must be unique","Invalid Title", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Validations.isNonEmpty(content) || !Validations.isValidDescription(content)) {
                JOptionPane.showMessageDialog(root,"Content cannot be empty and must be at least 10 characters.","Invalid Description", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean newLesson = courseService.addLesson(course.getCourseId(), title, content, Arrays.asList(res));
            if(newLesson) {
                JOptionPane.showMessageDialog(root, "Lesson added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(root, "Failed to add lesson.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        this.setContentPane(root);
        this.setModal(true);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle("Add Lesson to - " + course.getTitle());
        this.setVisible(true);
    }

    public boolean isSaved() {
        return saved;
    }
}
