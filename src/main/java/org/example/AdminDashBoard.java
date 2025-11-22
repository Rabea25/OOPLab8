package org.example;

import javax.swing.*;
import java.util.List;
public class AdminDashBoard {
    private JPanel adminRootPanel;
    private JTable pendingTable;
    private JButton approveButton;
    private JButton rejectButton;
    private JButton logoutButton;
    private CourseService courseService;
    private Admin adminuser;
    private UserService userService;
    private MainPanel mainPanel;

    public AdminDashBoard(Admin adminuser, CourseService courseService , UserService userService){
        this.adminuser = adminuser;
        this.courseService = courseService;
        this.userService = userService;

        pendingTable.getSelectionModel().addListSelectionListener(e -> showSelectedCourseDetails());

        loadPendingCourses();
        setUpButtons();

    }

    private void showSelectedCourseDetails() {
        int row = pendingTable.getSelectedRow();
        if (row == -1) {return;}
         String courseId = pendingTable.getValueAt(row , 0).toString();
        Course course = courseService.getCourseById(courseId);
        if (course != null) {
            JOptionPane.showMessageDialog(adminRootPanel, "Course ID: " + course.getCourseId() + "\n" + "Title: " + course.getTitle() +  "\n" + "Description: " + course.getDescription() + "\n" + "Instructor ID: " + course.getInstructorId() + "\n" + "Status: : "+ course.getApprovalStatus(), "Course Details", JOptionPane.INFORMATION_MESSAGE);
        }}

    private void setUpButtons(){
  approveButton.addActionListener(e-> {
      int row = pendingTable.getSelectedRow();
      if (row == -1) {
          JOptionPane.showMessageDialog(adminRootPanel, "Please select a course to approve.", "No course ", JOptionPane.WARNING_MESSAGE);
                return;}
      int confirm = JOptionPane.showConfirmDialog(adminRootPanel, "Are you sure you want to approve this course?", "Confirm Approval", JOptionPane.YES_NO_OPTION);
        if(confirm ==JOptionPane.NO_OPTION) {return;}
      String courseId = pendingTable.getValueAt(row , 0).toString();
      adminuser.approveCourse(courseId , courseService);
      JOptionPane.showMessageDialog(adminRootPanel, "Course approved successfully.", "GREAT!", JOptionPane.INFORMATION_MESSAGE );
      loadPendingCourses();
  });

        rejectButton.addActionListener(e-> {
            int row = pendingTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(adminRootPanel, "Please select a course to approve.", "No course ", JOptionPane.WARNING_MESSAGE);
                return;}
            int confirm = JOptionPane.showConfirmDialog(adminRootPanel, "Are you sure you want to reject this course?", "Confirm rejection", JOptionPane.YES_NO_OPTION);
            if(confirm ==JOptionPane.NO_OPTION) {return;}
            String courseId = pendingTable.getValueAt(row , 0).toString();
            adminuser.rejectCourse(courseId , courseService);
            JOptionPane.showMessageDialog(adminRootPanel, "Course Rejected.", "MMM", JOptionPane.INFORMATION_MESSAGE );
            loadPendingCourses();
        });
        logoutButton.addActionListener(e-> {
            mainPanel.logout();
        });

    }
 private void  loadPendingCourses(){

        List<Course> pendingCourses = adminuser.getPendingCourses(courseService);
        String[][] tabledata = new String[pendingCourses.size() ][3];

        for (int  i = 0; i <pendingCourses.size(); i++ ){
            Course course = pendingCourses.get(i);
            tabledata[i][0] = course.getCourseId();
            tabledata[i][1] = course.getTitle();
            tabledata[i][2] = course.getInstructorId();

            pendingTable.setModel(new javax.swing.table.DefaultTableModel(
                    tabledata,
                    new String [] {"Course ID", "Title", "Instructor ID"}
            ));
     }}


}
