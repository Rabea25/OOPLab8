package org.example;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
public class AdminDashboard extends JPanel{
    private JPanel root;
    private JTabbedPane tabbedPane;
    private JLabel homeLabel;
    private JTable pendingTable;
    private JButton approveButton;
    private JButton rejectButton;
    private JTable otherTable;
    private JButton logoutButton;
    private JLabel adminLabel;
    private JButton viewCourseDetailsButton;
    private CourseService courseService;
    private Admin adminuser;
    private UserService userService;
    private MainPanel mainPanel;

    public AdminDashboard(MainPanel mainPanel){

        this.setLayout(new BorderLayout());
        this.add(root, BorderLayout.CENTER);

        this.mainPanel = mainPanel;
        this.adminuser = (Admin) mainPanel.getCurrentUser();
        this.courseService = mainPanel.getCourseService();
        this.userService = mainPanel.getUserService();

        adminLabel.setText(adminuser.getUsername());
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateTab();
            }
        });
        updateTab();
        //pendingTable.getSelectionModel().addListSelectionListener(e -> showSelectedCourseDetails());
        setUpButtons();

    }

    private void showSelectedCourseDetails() {
        int row = pendingTable.getSelectedRow();
        if (row == -1) {return;}
         String courseId = pendingTable.getValueAt(row , 0).toString();
        Course course = courseService.getCourseById(courseId);
        if (course != null) {
            JOptionPane.showMessageDialog(root, "Course ID: " + course.getCourseId() + "\n" + "Title: " + course.getTitle() +  "\n" + "Description: " + course.getDescription() + "\n" + "Instructor ID: " + course.getInstructorId() + "\n" + "Status: : "+ course.getApprovalStatus(), "Course Details", JOptionPane.INFORMATION_MESSAGE);
        }}

    private void setUpButtons(){
        approveButton.addActionListener(e-> {
            int row = pendingTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(root, "Please select a course to approve.", "No course ", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(root, "Are you sure you want to approve this course?", "Confirm Approval", JOptionPane.YES_NO_OPTION);
            if(confirm ==JOptionPane.NO_OPTION) return;
            String courseId = pendingTable.getValueAt(row , 0).toString();
            courseService.approveCourse(courseId);
            JOptionPane.showMessageDialog(root, "Course approved successfully.", "GREAT!", JOptionPane.INFORMATION_MESSAGE );
            loadPendingCourses();
        });

        rejectButton.addActionListener(e-> {
            int row = pendingTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(root, "Please select a course to approve.", "No course ", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(root, "Are you sure you want to reject this course?", "Confirm rejection", JOptionPane.YES_NO_OPTION);
            if(confirm ==JOptionPane.NO_OPTION) {return;}
            String courseId = pendingTable.getValueAt(row , 0).toString();
            adminuser.rejectCourse(courseId , courseService);
            JOptionPane.showMessageDialog(root, "Course Rejected.", "MMM", JOptionPane.INFORMATION_MESSAGE );
            loadPendingCourses();
        });
        viewCourseDetailsButton.addActionListener(e -> showSelectedCourseDetails());
        logoutButton.addActionListener(e-> {
            mainPanel.logout();
        });

    }
 private void loadPendingCourses(){
    List<Course> pendingCourses = courseService.getPendingCourses();
    String[] columns =  {"Course ID", "Title", "Instructor ID"};
    String[][] tableData = new String[pendingCourses.size()][3];

    for(int i=0; i<pendingCourses.size(); i++){
        Course course = pendingCourses.get(i);
        tableData[i][0] = course.getCourseId();
        tableData[i][1] = course.getTitle();
        tableData[i][2] = course.getInstructorId();
    }

    pendingTable.setModel(new DefaultTableModel(tableData, columns){
     @Override
     public boolean isCellEditable(int row, int column) {return false;}
    });

    if(!pendingCourses.isEmpty()) pendingTable.setRowSelectionInterval(0,0);

    }

    public void loadOtherCourses(){
        List<Course> otherCourses = courseService.getOtherCourses();

        String[] columns =  {"Course ID", "Title", "Instructor ID", "Status"};
        String[][] tableData = new String[otherCourses.size()][4];

        for(int i=0; i<otherCourses.size(); i++){
            Course course = otherCourses.get(i);
            tableData[i][0] = course.getCourseId();
            tableData[i][1] = course.getTitle();
            tableData[i][2] = course.getInstructorId();
            tableData[i][3] = course.getApprovalStatus().toString();
        }

        otherTable.setModel(new DefaultTableModel(tableData, columns){
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        });



    }

    public void updateTab(){
        int curTab = tabbedPane.getSelectedIndex();
        if(curTab==0){
            homeLabel.setText("Welcome "+adminuser.getUsername()+"! You have "+courseService.getPendingCourses().size()+" pending courses!");
        }
        else if(curTab==1){
            loadPendingCourses();
        }
        else{
            loadOtherCourses();
        }
    }
}
