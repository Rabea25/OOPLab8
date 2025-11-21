package org.example;

import javax.swing.*;

public class AddCourseDialog extends JDialog{
    private boolean saved = false;
    private JPanel root;
    private JTextField titleField;
    private JTextField descField;
    private JLabel instructorLabel;
    private JButton saveButton;
    private JButton cancelButton;

    public AddCourseDialog(Instructor instructor, CourseService courseService){

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String description = descField.getText().trim();

            Course b = courseService.getCourseByTitle(title);

            if (!Validations.isNonEmpty(title) || !Validations.isValidTitle(title) || (b != null)) {
                JOptionPane.showMessageDialog(root,"Title cannot be empty and must be at least 4 characters, and must be unique","Invalid Title",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Validations.isNonEmpty(description) || !Validations.isValidDescription(description)) {
                JOptionPane.showMessageDialog(root,"Description cannot be empty and must be at least 10 characters.","Invalid Description",JOptionPane.ERROR_MESSAGE);
                return;
            }

            if(courseService.editCourse(Utilities.generateCourseId(), title, description)) {
                JOptionPane.showMessageDialog(root, "Course saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            }
            else {
                JOptionPane.showMessageDialog(root, "Failed to add course.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        this.setContentPane(root);
        this.setModal(true);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle("Add Course");
        this.setVisible(true);
    }

    public boolean isSaved(){
        return saved;
    }

}
