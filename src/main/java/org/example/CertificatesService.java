package org.example;

import java.time.LocalDate;
import java.util.*;
import java.util.UUID;

public class CertificatesService {

    public Certificates checkAndIssueCertificate(Student student, Course course)
    {
        if(isAlreadyIssued(student, course.getCourseId())) { return null;}

        if(verifyCourseCompletion(student, course))
        {
            String certID = "CERT-" + UUID.randomUUID().toString().substring(0,8); //aashan a genrate random ID
            String content = generateCertificateContent(student, course, certID);

            Certificates newCertificate = new Certificates(certID, student.getUserId(), course.getCourseId(), content);

            student.addCertificate(newCertificate);

            List<User> allUsers = JsonDatabaseManager.loadUsers();
            for(int i=0; i <allUsers.size(); i++)
            {
                if (allUsers.get(i).getUserId().equals(student.getUserId()))
                {
                    allUsers.set(i,student);
                    break;
                }
            }
            JsonDatabaseManager.writeUsers(allUsers);

            return newCertificate;
        }
        return null;
    }

    private boolean verifyCourseCompletion(Student student, Course course)
    {
        for(Lesson lesson: course.getLessons())
        {
            if(lesson.getQuiz() != null) {

                String lessonID = lesson.getLessonId();
                ArrayList<QuizAttempt> attempts = student.getQuizAttemptsbyLessonId(lessonID);
                if (!hasStudentPassedLessonQuiz(attempts)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasStudentPassedLessonQuiz(ArrayList<QuizAttempt> attempts)
    {
        if(attempts == null || attempts.isEmpty()) return false;

        for(QuizAttempt attempt : attempts)
        {
            if(attempt.isPassed()) return true;
        }
        return false;
    }

    private static String generateCertificateContent(Student student, Course course, String certID)
    {
        return String.format(
                "{\n" +
                        "  \"certificateID\": \"%s\",\n" + "  \"recipientName\": \"%s\",\n" +
                        "  \"courseTitle\": \"%s\",\n" +
                        "  \"issueDate\": \"%s\"\n" +
                        "}",
                certID,
                student.getUsername(),
                course.getTitle(),
                LocalDate.now().toString()
        );
    }

    private boolean isAlreadyIssued(Student student, String courseID)
    {
        ArrayList<Certificates> earned = student.getEarnedCertificates();
        for (Certificates cert : earned)
        {
            if(cert.getCourseID().equals(courseID)) return true;
        }
        return false;
    }


}
