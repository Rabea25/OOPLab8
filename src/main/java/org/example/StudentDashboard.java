package org.example;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class StudentDashboard extends JPanel{
    private JPanel root;
    private JComboBox coursesComboBox;
    private JButton logoutButton;
    private JLabel studentLabel;
    private JButton unenrollButton;
    private JSplitPane splitPane;
    private JTable lessonsTable;
    private JButton completeLessonButton;
    private JButton exploreCoursesButton;
    private JLabel descLabel;
    private JLabel progressLabel;
    private JPanel lessonDetailsPanel;
    private JTextArea contentArea;
    private JTextArea resoucresArea;
    private JLabel titleLabel2;
    private JButton viewَQuizAttemptsButton;
    private Student student;
    private ArrayList<Course> courses;
    private CourseService courseService;
    private UserService userService;
    public StudentDashboard(MainPanel mainPanel){
        this.setLayout(new BorderLayout());
        this.add(root, BorderLayout.CENTER);

        this.userService = mainPanel.getUserService();
        this.courseService = mainPanel.getCourseService();
        this.student = (Student) mainPanel.getCurrentUser();

        splitPane.setDividerLocation(0.5);
        studentLabel.setText(student.getUsername());

        logoutButton.addActionListener(e -> mainPanel.logout());
        unenrollButton.addActionListener(e -> unenroll());
        exploreCoursesButton.addActionListener(e -> enroll());
        coursesComboBox.addActionListener(e ->{
            updateLessonsTable();
        });
        completeLessonButton.addActionListener(e -> completeLesson());
        lessonsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    System.out.println("here");
                    updateLessonDetails();
                }
            }
        });
        refresh();
    }

    private void refresh(){

        coursesComboBox.removeAllItems();
        courses = courseService.getCoursesByStudentId(student.getUserId());
        if(courses.isEmpty()){
            coursesComboBox.addItem("No courses available");
            coursesComboBox.setSelectedIndex(0);
            coursesComboBox.setEnabled(false);
            updateLessonsTable();
            return;
        }
        coursesComboBox.setEnabled(true);
        for(Course course : courses) coursesComboBox.addItem(course.getCourseId() + " : " + course.getTitle());
        coursesComboBox.setSelectedIndex(0);
    }

    private void updateLessonsTable(){
        if(!coursesComboBox.isEnabled() || coursesComboBox.getSelectedIndex() < 0 || coursesComboBox.getSelectedIndex() >= courses.size()){
            lessonsTable.setModel(new DefaultTableModel());
            return;
        }
        Course selectedCourse = courses.get(coursesComboBox.getSelectedIndex());
        Lesson[] lessons = selectedCourse.getLessons();
        Arrays.sort(lessons, Comparator.comparing(Lesson::getLessonId));
        //ArrayList<Lesson> completedLessons = courseService.getCompletedLessons(student.getUserId(), selectedCourse.getCourseId());
        List<String> completedLessonIds = List.of(student.getProgress(selectedCourse.getCourseId()));
        String[] columns = {"Lesson ID", "Title", "Content", "Completion"};

        Object[][] data = new Object[lessons.length][columns.length];
        for(int i=0; i < lessons.length; i++) {
            data[i][0] = lessons[i].getLessonId();
            data[i][1] = lessons[i].getTitle();
            data[i][2] = lessons[i].getContent();
            data[i][3] = completedLessonIds.contains(lessons[i].getLessonId()) ? "Yes" : "No";
        }
        lessonsTable.setModel(new DefaultTableModel(data, columns){
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        });


        descLabel.setText(selectedCourse.getDescription());
        progressLabel.setText("Progress: " + completedLessonIds.size() + " / " + lessons.length + " lessons completed.");
        if(completedLessonIds.size() == lessons.length) progressLabel.setText(progressLabel.getText() + " Course Completed!");
        if(lessonsTable.getRowCount() > 0) lessonsTable.setRowSelectionInterval(0, 0);
        updateLessonDetails();
    }

    private void unenroll(){
        if(!coursesComboBox.isEnabled() || coursesComboBox.getSelectedIndex() < 0 || coursesComboBox.getSelectedIndex() >= courses.size()){
            JOptionPane.showMessageDialog(this, "No course selected to unenroll from.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Course selectedCourse = courses.get(coursesComboBox.getSelectedIndex());
        boolean success = courseService.unenrollStudent(selectedCourse.getCourseId(), student.getUserId());
        if(success){
            JOptionPane.showMessageDialog(this, "Unenrolled from course: " + selectedCourse.getTitle(), "Success", JOptionPane.INFORMATION_MESSAGE);
            refresh();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to unenroll", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void enroll(){
        ExploreCoursesDialog dialog = new ExploreCoursesDialog(student, courseService);
        if(dialog.isEnrolled()){
            refresh();
        }
    }

    private void completeLesson(){
        int selectedRow = lessonsTable.getSelectedRow();
        if(selectedRow < 0){
            JOptionPane.showMessageDialog(this, "No lesson selected to complete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(!coursesComboBox.isEnabled() || coursesComboBox.getSelectedIndex() < 0 || coursesComboBox.getSelectedIndex() >= courses.size()){
            JOptionPane.showMessageDialog(this, "No course selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(lessonsTable.getValueAt(selectedRow, 3).equals("Yes")){
            JOptionPane.showMessageDialog(this, "Lesson already completed.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }


        boolean pass = false, success = false;

        if(pass) success = courseService.completeLesson(courses.get(coursesComboBox.getSelectedIndex()).getCourseId(), student.getUserId(), (String) lessonsTable.getValueAt(selectedRow, 0));
        if(success){
            JOptionPane.showMessageDialog(this, "Quiz passed and lesson complete.", "Success", JOptionPane.INFORMATION_MESSAGE);
            updateLessonsTable();
        } else {
            JOptionPane.showMessageDialog(this, "Quiz failed, lesson is incomplete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void updateLessonDetails(){
        int selectedRow = lessonsTable.getSelectedRow();
        if(selectedRow < 0){
            lessonDetailsPanel.setVisible(false);
            return;
        }
        lessonDetailsPanel.setVisible(true);
        Lesson lesson = courseService.getLessonById(courses.get(coursesComboBox.getSelectedIndex()).getCourseId(), (String) lessonsTable.getValueAt(selectedRow, 0));
        titleLabel2.setText(lesson.getTitle());
        contentArea.setText(lesson.getContent());
        StringBuilder resourcesText = new StringBuilder();
        int cnt = 1;
        for(String resource : lesson.getResources()) resourcesText.append(cnt++).append(") ").append(resource).append("\n");
        resoucresArea.setText(resourcesText.toString());
        contentArea.setEditable(false);
        resoucresArea.setEditable(false);
        splitPane.setDividerLocation(0.6);

    }
}
