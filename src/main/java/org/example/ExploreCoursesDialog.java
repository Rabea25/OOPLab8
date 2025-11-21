package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ExploreCoursesDialog extends JDialog {


    private JPanel root;
    private JTable table1;
    private JButton enrollButton;
    private JButton backButton;
    private boolean enrolled = false;
    public ExploreCoursesDialog(Student student, CourseService courseService) {

        List<String> enrolledCourses = List.of(student.getEnrolledCourses());
        Course[] allCourses = courseService.getAllCourses();
        ArrayList<Course> availableCourses = new ArrayList<>();
        for(Course course : allCourses){
            if(!enrolledCourses.contains(course.getCourseId())) availableCourses.add(course);
        }

        String[] columnNames = {"Course ID", "Title", "Lessons Count"};
        Object[][] data = new Object[availableCourses.size()][3];

        for(int i=0; i<availableCourses.size(); i++){
            Course course = availableCourses.get(i);
            data[i][0] = course.getCourseId();
            data[i][1] = course.getTitle();
            data[i][2] = course.getLessons().length;
        }

        table1.setModel(new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        });

        enrollButton.addActionListener(e -> {
            int selectedRow = table1.getSelectedRow();
            if(selectedRow < 0 || selectedRow >= availableCourses.size()) return;
            Course selectedCourse = availableCourses.get(selectedRow);
            courseService.enrollStudent(selectedCourse.getCourseId(), student.getUserId());
            JOptionPane.showMessageDialog(root, "Sucessfully enrolled.", "Success", JOptionPane.INFORMATION_MESSAGE);
            enrolled = true;
            this.dispose();
        });
        backButton.addActionListener(e -> this.dispose());

        this.setContentPane(root);
        this.setModal(true);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle("Explore courses");
        this.setVisible(true);
    }

    public boolean isEnrolled(){
        return enrolled;
    }
}
