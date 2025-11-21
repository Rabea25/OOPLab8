package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditCourseDialog extends JDialog{
    private JTextField titleField;
    private JTextField descField;
    private JButton saveButton;
    private JButton cancelButton;
    private JLabel instructorLabel;
    private JLabel courseLabel;
    private JPanel root;
    private boolean saved = false;

    public EditCourseDialog(Course course, Instructor instructor, CourseService courseService) {
        courseLabel.setText(course.getCourseId());
        instructorLabel.setText(instructor.getUsername());
        titleField.setText(course.getTitle());
        descField.setText(course.getDescription());



        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("saving");
                String title = titleField.getText().trim();
                String description = descField.getText().trim();

                Course b = courseService.getCourseByTitle(title);

                if (!Validations.isNonEmpty(title) || !Validations.isValidTitle(title) || (b != null && !b.getCourseId().equals(course.getCourseId()))) {
                    JOptionPane.showMessageDialog(root,"Title cannot be empty and must be at least 4 characters, and must be unique","Invalid Title",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!Validations.isNonEmpty(description) || !Validations.isValidDescription(description)) {
                    JOptionPane.showMessageDialog(root,"Description cannot be empty and must be at least 10 characters.","Invalid Description",JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if(courseService.editCourse(course.getCourseId(), title, description)) {
                    JOptionPane.showMessageDialog(root, "Course saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    dispose();
                }
                else {
                    JOptionPane.showMessageDialog(root, "Failed to edit course.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        this.setContentPane(root);
        this.setModal(true);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle("Edit Course - " + course.getTitle());
        this.setVisible(true);

    }
    public boolean isSaved() {
        return saved;
    }
}
