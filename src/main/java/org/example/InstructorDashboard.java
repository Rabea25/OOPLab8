package org.example;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;


public class InstructorDashboard extends JPanel{
    private JPanel root;
    private JLabel instructorLabel;
    private JComboBox coursesComboBox;
    private JTable lessonsTable;
    private JTable studentsTable;
    private JSplitPane splitPane;
    private JButton logoutButton;
    private JButton editCourseButton;
    private JButton deleteCourseButton;
    private JButton addCourseButton;
    private JButton addLessonButton;
    private JButton editLessonButton;
    private JButton deleteLessonButton;
    private JPanel quizPanel;
    private JLabel questionsNo;
    private JLabel quizTitle;
    private UserService userService;
    private CourseService courseService;
    private Instructor instructor;
    private ArrayList<Course> courses;
    private JButton viewInsightsButton;
    private Analytics analytics;

    public InstructorDashboard(MainPanel mainPanel) {
        this.setLayout(new BorderLayout());
        this.add(root, BorderLayout.CENTER);
        this.userService = mainPanel.getUserService();
        this.courseService = mainPanel.getCourseService();
        this.instructor = (Instructor) mainPanel.getCurrentUser();
        this.analytics = new Analytics(userService, courseService);

        splitPane.setDividerLocation(0.5);
        instructorLabel.setText(instructor.getUsername());

        logoutButton.addActionListener(e -> {
            mainPanel.logout();
        });

        coursesComboBox.addActionListener(e -> {
            updateLessonsTable();
            updateStudentsTable();
        });

        deleteCourseButton.addActionListener(e -> deleteCourse());
        deleteLessonButton.addActionListener(e -> deleteLesson());
        addLessonButton.addActionListener(e -> addLesson());
        editLessonButton.addActionListener(e -> editLesson());
        editCourseButton.addActionListener(e -> editCourse());
        addCourseButton.addActionListener(e -> addCourse());
        viewInsightsButton.addActionListener(e -> viewInsights());

        lessonsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateQuizDetails();
                }
            }
        });

        refresh();
    }

    private void refresh(){
        coursesComboBox.removeAllItems();
        courses = courseService.getCoursesByInstructorId(instructor.getUserId());
        if(courses.isEmpty()){
            coursesComboBox.addItem("No courses available");
            coursesComboBox.setSelectedIndex(0);
            coursesComboBox.setEnabled(false);
            updateLessonsTable();
            updateStudentsTable();
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
        String[] columns = {"Lesson ID", "Title", "Content"};
        Object[][] data = new Object[lessons.length][columns.length];
        for(int i=0; i < lessons.length; i++) {
            data[i][0] = lessons[i].getLessonId();
            data[i][1] = lessons[i].getTitle();
            data[i][2] = lessons[i].getContent();
        }
        lessonsTable.setModel(new DefaultTableModel(data, columns){
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        });
        if(lessons.length > 0) lessonsTable.setRowSelectionInterval(0,0);
    }

    private void updateStudentsTable(){
        if(!coursesComboBox.isEnabled() || coursesComboBox.getSelectedIndex() < 0 || coursesComboBox.getSelectedIndex() >= courses.size()){
            studentsTable.setModel(new DefaultTableModel());
            return;
        }
        Course selectedCourse = courses.get(coursesComboBox.getSelectedIndex());
        List<String> enrolledStudents = selectedCourse.getEnrolledStudents();
        String[] columns = {"Student ID", "Username", "Email"};
        Object[][] data = new Object[enrolledStudents.size()][columns.length];
        for(int i=0; i < enrolledStudents.size(); i++) {
            User user = userService.getUserById(enrolledStudents.get(i));
            data[i][0] = user.getUserId();
            data[i][1] = user.getUsername();
            data[i][2] = user.getEmail();
        }
        studentsTable.setModel(new DefaultTableModel(data, columns){
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        });
    }

    private void deleteCourse(){
        int selectedIndex = coursesComboBox.getSelectedIndex();
        if(selectedIndex < 0 || selectedIndex >= courses.size()){
            JOptionPane.showMessageDialog(root, "No course selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Course selectedCourse = courses.get(selectedIndex);
        int confirm = JOptionPane.showConfirmDialog(root, "Are you sure you want to delete the course: " + selectedCourse.getTitle() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.NO_OPTION) return;

        boolean success = courseService.removeCourse(selectedCourse.getCourseId());
        if(success){
            JOptionPane.showMessageDialog(root, "Course deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refresh();
        } else {
            JOptionPane.showMessageDialog(root, "Failed to delete course.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void deleteLesson(){
        if(lessonsTable.getSelectedRow() < 0){
            JOptionPane.showMessageDialog(root, "No lesson selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String lessonId = lessonsTable.getValueAt(lessonsTable.getSelectedRow(), 0).toString();
        String courseId = courses.get(coursesComboBox.getSelectedIndex()).getCourseId();
        Lesson lesson = courseService.getLessonById(courseId, lessonId);
        int confirm = JOptionPane.showConfirmDialog(root, "Delete lesson " + lessonId + ":" + lesson.getTitle() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.NO_OPTION) return;
        boolean success = courseService.removeLesson(courseId, lessonId);
        if(success) {
            JOptionPane.showMessageDialog(root, "Lesson deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            updateLessonsTable();
        } else {
            JOptionPane.showMessageDialog(root, "Lesson delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void editLesson(){
        if(lessonsTable.getSelectedRow() < 0){
            JOptionPane.showMessageDialog(root, "No lesson selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String lessonId = lessonsTable.getValueAt(lessonsTable.getSelectedRow(), 0).toString();
        Course course = courses.get(coursesComboBox.getSelectedIndex());
        Lesson lesson = courseService.getLessonById(course.getCourseId(), lessonId);
        EditLessonDialog editPanel = new EditLessonDialog(lesson, courseService, course);
        if(editPanel.isSaved()) {
            updateLessonsTable();
        }
    }
    public void editCourse(){
        if(coursesComboBox.getSelectedIndex() < 0 || coursesComboBox.getSelectedIndex() >= courses.size() || !coursesComboBox.isEnabled()){
            JOptionPane.showMessageDialog(root, "No course selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Course selectedCourse = courses.get(coursesComboBox.getSelectedIndex());
        EditCourseDialog editPanel = new EditCourseDialog(selectedCourse,(Instructor)(userService.getUserById(selectedCourse.getInstructorId())),courseService);
        if(editPanel.isSaved()){
            refresh();
        }

    }
    public void addLesson(){
        if(coursesComboBox.getSelectedIndex() < 0 || coursesComboBox.getSelectedIndex() >= courses.size() || !coursesComboBox.isEnabled()){
            JOptionPane.showMessageDialog(root, "No course selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Course selectedCourse = courses.get(coursesComboBox.getSelectedIndex());
        AddLessonDialog addLessonDialog = new AddLessonDialog(selectedCourse, courseService);
        if(addLessonDialog.isSaved()){
            refresh();
        }
    }
    public void addCourse(){
        AddCourseDialog addCourseDialog = new AddCourseDialog(instructor, courseService);
        if(addCourseDialog.isSaved()){
            refresh();
        }
    }

    public void updateQuizDetails(){
        if(lessonsTable.getSelectedRow() < 0){
            quizTitle.setText("Title: N/A");
            questionsNo.setText("Questions: N/A");
            return;
        }
        String lessonId = lessonsTable.getValueAt(lessonsTable.getSelectedRow(), 0).toString();
        String courseId = courses.get(coursesComboBox.getSelectedIndex()).getCourseId();
        Lesson lesson = courseService.getLessonById(courseId, lessonId);
        Quiz quiz = lesson.getQuiz();
        if(quiz == null){
            quizTitle.setText("Title: N/A");
            questionsNo.setText("Questions: N/A");
            return;
        }
        quizTitle.setText("Title: "+quiz.getTitle());
        questionsNo.setText("Questions: "+String.valueOf(quiz.getNumberOfQuestions()));
    }

    private void viewInsights() {
        if(!coursesComboBox.isEnabled() || coursesComboBox.getSelectedIndex() < 0 || coursesComboBox.getSelectedIndex() >= courses.size()){
            JOptionPane.showMessageDialog(root, "Please select a course to view insights.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Course selectedCourse = courses.get(coursesComboBox.getSelectedIndex());
        new InsightsDialog(null, selectedCourse, analytics, courseService, userService);
    }

}
