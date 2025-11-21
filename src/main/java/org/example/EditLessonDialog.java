package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditLessonDialog extends JDialog{
    private JPanel root;
    private JTextField titleField;
    private JTextField contentField;
    private JLabel courseLabel;
    private JLabel lessonLabel;
    private JButton saveButton;
    private JButton cancelButton;
    private JTextField resourceField;
    private boolean saved = false;

    public EditLessonDialog(Lesson lesson, CourseService courseService, Course course){

        titleField.setText(lesson.getTitle());
        contentField.setText(lesson.getContent());
        String resources = String.join(",", lesson.getResources());
        resourceField.setText(resources);
        lessonLabel.setText(lesson.getLessonId());
        courseLabel.setText(course.getCourseId());


        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title  = titleField.getText().trim();
                String content = contentField.getText().trim();
                String[] res = resourceField.getText().trim().split(",");

                Lesson b = courseService.getLessonByTitle(course.getCourseId(), title);

                if (!Validations.isNonEmpty(title) || !Validations.isValidTitle(title) || (b != null && !b.getLessonId().equals(lesson.getLessonId()))) {
                    JOptionPane.showMessageDialog(root,"Title cannot be empty and must be at least 4 characters, and must be unique","Invalid Title", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!Validations.isNonEmpty(content) || !Validations.isValidDescription(content)) {
                    JOptionPane.showMessageDialog(root,"content cannot be empty and must be at least 10 characters.","Invalid Description", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if(courseService.editLesson(course.getCourseId(), lesson.getLessonId(), title, content, res)) {
                    JOptionPane.showMessageDialog(root, "Lesson saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    dispose();
                }
                else {
                    JOptionPane.showMessageDialog(root, "Failed to edit lesson.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        this.setContentPane(root);
        this.setModal(true);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle("Edit Lesson - " + lesson.getTitle());
        this.setVisible(true);

    }
    public boolean isSaved() {
        return saved;
    }
}
