package org.example;

import java.time.LocalDate;

public class Certificates {
    private final String certificateID;
    private final String studentID;
    private final String courseID;
    private final String issueDate;
    private final String certificateContent;

    public Certificates(String certificateID, String studentID, String courseID, String certificateContent)
    {
        this.certificateID = certificateID;
        this.studentID = studentID;
        this.courseID = courseID;
        this.issueDate = LocalDate.now().toString();
        this.certificateContent = certificateContent;
    }

    public String getCertificateID() { return certificateID; }
    public String getStudentID() {return studentID; }
    public String getCourseID() { return courseID; }
    public String getIssueDate() { return issueDate; }
    public String getCertificateContent() { return certificateContent; }

}
