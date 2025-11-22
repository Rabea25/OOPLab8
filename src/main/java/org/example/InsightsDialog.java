package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InsightsDialog extends JDialog {
    private JPanel rootPanel;
    private JTabbedPane tabbedPane;
    private JPanel overviewPanel;
    private JPanel studentPerformancePanel;
    private JPanel lessonStatisticsPanel;
    private JLabel overviewTitleLabel;
    private JLabel studentsCountLabel;
    private JLabel lessonsCountLabel;
    private JLabel completionLabel;
    private JTable studentTable;
    private JButton showStudentChartButton;
    private JTable lessonTable;
    private JButton showLessonChartButton;
    private JButton closeButton;
    private Analytics analytics;
    private CourseService courseService;
    private UserService userService;
    private Course course;

    public InsightsDialog(JFrame parent, Course course, Analytics analytics,
                          CourseService courseService, UserService userService) {
        super(parent, "Insights - " + course.getTitle(), true);

        this.course = course;
        this.analytics = analytics;
        this.courseService = courseService;
        this.userService = userService;
        this.setContentPane(rootPanel);
        this.setSize(800, 600);
        this.setLocationRelativeTo(parent);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initializeData();
        setupListeners();
        this.setVisible(true);
    }

    private void initializeData() {
        loadOverviewData();
        loadStudentPerformanceData();
        loadLessonStatisticsData();
    }

    private void loadOverviewData() {
        double completionPercentage = analytics.getCourseCompletionPercentage(course.getCourseId());
        List<String> enrolledStudents = courseService.getEnrolledStudents(course.getCourseId());
        int studentCount = enrolledStudents != null ? enrolledStudents.size() : 0; //haga mn ayam elsha2aqa bta3t el c
        int lessonCount = course.getLessons().length;
        studentsCountLabel.setText("Enrolled Students: " + studentCount);
        lessonsCountLabel.setText("Total Lessons: " + lessonCount);
        completionLabel.setText(String.format("Overall Completion: %.2f", completionPercentage));
    }

    private void loadStudentPerformanceData() {
        List<String> enrolledStudentIds = courseService.getEnrolledStudents(course.getCourseId());
        if (enrolledStudentIds == null || enrolledStudentIds.isEmpty()) {
            String[] columns = {"Student ID", "Username", "Completed Lessons", "Total Lessons", "Completion %"};
            Object[][] data = new Object[0][5];
            studentTable.setModel(new DefaultTableModel(data, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            return;
        }

        int totalLessons = course.getLessons().length;
        String[] columns = {"Student ID", "Username", "Completed Lessons", "Total Lessons", "Completion %"};
        Object[][] data = new Object[enrolledStudentIds.size()][5];
        int row = 0;
        for (String studentId : enrolledStudentIds) {
            Student student = (Student) userService.getUserById(studentId);
            if (student != null) {
                int completedLessons = student.getProgress(course.getCourseId()).length;
                double percentage = analytics.getStudentCompletionPercentage(studentId, course.getCourseId());
                data[row][0] = studentId;
                data[row][1] = student.getUsername();
                data[row][2] = completedLessons;
                data[row][3] = totalLessons;
                data[row][4] =String.format("%.2f", percentage);
                row++;
            }
        }

        studentTable.setModel(new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    private void loadLessonStatisticsData() {
        Lesson[] lessons = course.getLessons();
        if (lessons.length == 0) {
            String[] columns = {"Lesson ID", "Title", "Average Quiz Score", "Students Completed"};
            Object[][] data = new Object[0][4];
            lessonTable.setModel(new DefaultTableModel(data, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            return;
        }

        String[] columns = {"Lesson ID", "Title", "Average Quiz Score", "Students Completed"};
        Object[][] data = new Object[lessons.length][4];
        for (int i = 0; i < lessons.length; i++) {
            Lesson lesson = lessons[i];
            double avgScore = analytics.getAverageQuizScore(course.getCourseId(), lesson.getLessonId());
            int completionCount = analytics.getCompletionCount(course.getCourseId(), lesson.getLessonId());
            data[i][0] = lesson.getLessonId();
            data[i][1] = lesson.getTitle();
            data[i][2] = String.format("%.2f", avgScore);
            data[i][3] = completionCount;
        }

        lessonTable.setModel(new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    private void setupListeners() {
        closeButton.addActionListener(e -> dispose());
        showStudentChartButton.addActionListener(e -> showStudentCompletionChart());
        showLessonChartButton.addActionListener(e -> showLessonScoresChart());
    }

    private void showStudentCompletionChart() {
        List<String> enrolledStudentIds = courseService.getEnrolledStudents(course.getCourseId());
        if (enrolledStudentIds == null || enrolledStudentIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students to show", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<Double> values = new ArrayList<>();
        for (String studentId : enrolledStudentIds) {
            Student student = (Student) userService.getUserById(studentId);
            if (student != null) {
                labels.add(student.getUsername());
                double percentage = analytics.getStudentCompletionPercentage(studentId, course.getCourseId());
                values.add(percentage);
            }
        }
        new ChartFrame(labels, values, "Student Completion Percentage", "Students", "Completion %");
    }

    private void showLessonScoresChart() {
        Lesson[] lessons = course.getLessons();
        if (lessons.length == 0) {
            JOptionPane.showMessageDialog(this, "No lessons to show", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<Double> values = new ArrayList<>();
        for (Lesson lesson : lessons) {
            labels.add(lesson.getTitle());
            double avgScore = analytics.getAverageQuizScore(course.getCourseId(), lesson.getLessonId());
            values.add(avgScore);
        }
        new ChartFrame(labels, values, "Average Quiz Scores by Lesson", "Lessons", "Average Score");
    }
}