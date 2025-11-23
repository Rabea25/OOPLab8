package org.example;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JTable attemptsTable;
    private JButton certificatesButton;
    private Student student;
    private ArrayList<Course> courses;
    private CourseService courseService;
    private UserService userService;
    private Course currentCourse = null;
    private Lesson currentLesson = null;
    private ArrayList<QuizAttempt> quizAttempts;

    public StudentDashboard(MainPanel mainPanel){
        this.setLayout(new BorderLayout());
        this.add(root, BorderLayout.CENTER);

        this.userService = mainPanel.getUserService();
        this.courseService = mainPanel.getCourseService();
        this.student = (Student) mainPanel.getCurrentUser();

        splitPane.setDividerLocation(0.4);
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
                    updateLessonDetails();
                }
            }
        });
        attemptsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = attemptsTable.getSelectedRow();
                if(e.getClickCount()==2 && row!=-1){
                    checkQuiz();
                }
            }
        });
        refresh();
        certificatesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CertificatePanel certificatesPanel = new CertificatePanel(student);
            }

        });
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
            descLabel.setText("");
            progressLabel.setText("");
            lessonDetailsPanel.setVisible(false);
            return;
        }
        currentCourse = courses.get(coursesComboBox.getSelectedIndex());
        Lesson[] lessons = currentCourse.getLessons();
        Arrays.sort(lessons, Comparator.comparing(Lesson::getLessonId));

        List<String> completedLessonIds = List.of(student.getProgress(currentCourse.getCourseId()));
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


        descLabel.setText(currentCourse.getDescription());
        progressLabel.setText("Progress: " + completedLessonIds.size() + " / " + lessons.length + " lessons completed.");
        if(completedLessonIds.size() == lessons.length) {
            progressLabel.setText(progressLabel.getText() + " Course Completed!");
            if(CertificatesService.checkAndIssueCertificate(student, currentCourse, userService) != null) JOptionPane.showMessageDialog(root, "Course complete! Check for your new certificate", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
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
        if(currentCourse == null){
            JOptionPane.showMessageDialog(this, "No course selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(currentLesson == null){
            JOptionPane.showMessageDialog(this, "No lesson selected to complete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
//      allow mutliple attempts a Quiz, possible for improving score
//        if(lessonsTable.getValueAt(selectedRow, 3).equals("Yes")){
//            JOptionPane.showMessageDialog(this, "Lesson already completed.", "Info", JOptionPane.INFORMATION_MESSAGE);
//            return;
//        }
        if(selectedRow > 0 && lessonsTable.getValueAt(selectedRow-1, 3).equals("No")){
            JOptionPane.showMessageDialog(this, "You must complete previous lesson first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        attemptQuiz();
        updateLessonsTable();
    }
    public void updateLessonDetails(){
        int selectedRow = lessonsTable.getSelectedRow();
        if(selectedRow < 0){
            lessonDetailsPanel.setVisible(false);
            return;
        }
        currentLesson = courseService.getLessonById(courses.get(coursesComboBox.getSelectedIndex()).getCourseId(), (String) lessonsTable.getValueAt(selectedRow, 0));
        lessonDetailsPanel.setVisible(true);

        titleLabel2.setText(currentLesson.getTitle());
        contentArea.setText(currentLesson.getContent());
        StringBuilder resourcesText = new StringBuilder();
        int cnt = 1;
        for(String resource : currentLesson.getResources()) resourcesText.append(cnt++).append(") ").append(resource).append("\n");
        resoucresArea.setText(resourcesText.toString());
        contentArea.setEditable(false);
        resoucresArea.setEditable(false);
        splitPane.setDividerLocation(0.4);
        updateQuizAttemptsTable();
    }

    public void updateQuizAttemptsTable(){
        quizAttempts = student.getQuizAttemptsbyLessonId(currentLesson.getLessonId());
        String[] columns = {"Attempt", "Score", "passed"};
        Object[][] data = new Object[quizAttempts.size()][columns.length];

        for(int i=0; i<quizAttempts.size(); i++){
            data[i][0] = i+1;
            data[i][1] = quizAttempts.get(i).getScore();
            data[i][2] = quizAttempts.get(i).isPassed() ? "Yes" : "No";
            //System.out.println("Quiz Attempt "+i+": Score="+data[i][0]+", Passed="+data[i][1]);
        }

        attemptsTable.setModel(new DefaultTableModel(data, columns){
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        });

    }
    public void checkQuiz(){
        Quiz quiz = currentLesson.getQuiz();
        QuizAttempt quizAttempt = quizAttempts.get(attemptsTable.getSelectedRow());
        Boolean completion = lessonsTable.getValueAt(lessonsTable.getSelectedRow(), 3).equals("Yes");
        if(!completion){
            JOptionPane.showMessageDialog(root, "Complete the lesson again to review past attempts.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        AttemptDialog attemptDialog = new AttemptDialog(quiz, quizAttempt);
    }

    public void attemptQuiz(){
        Quiz quiz = currentLesson.getQuiz();
        int selectedLessonRow = lessonsTable.getSelectedRow();
        QuizDialog quizDialog = new QuizDialog(student, currentLesson, currentCourse, courseService);
        updateLessonsTable();
        lessonsTable.setRowSelectionInterval(selectedLessonRow, selectedLessonRow);
    }
}
