package org.example;

import java.time.LocalDate;
import java.util.*;
import java.util.UUID;

public class CertificatesService {

    public static Certificates checkAndIssueCertificate(Student student, Course course, UserService userService)
    {
        if(isAlreadyIssued(student, course.getCourseId())) { return null;}

        String certID = "CERT-" + UUID.randomUUID().toString().substring(0,8); //aashan a genrate random ID
        String content = generateCertificateContent(student, course, certID);

        Certificates newCertificate = new Certificates(certID, student.getUserId(), course.getCourseId(), content);

        student.addCertificate(newCertificate);
        userService.updateUser(student);

        return newCertificate;
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
                        "  \"certificateID\": \"%s\",\n" +
                        "  \"recipientName\": \"%s\",\n" +
                        "  \"courseTitle\": \"%s\",\n" +
                        "  \"issueDate\": \"%s\"\n" +
                        "}",
                certID,
                student.getUsername(),
                course.getTitle(),
                LocalDate.now().toString()
        );
    }

    private static boolean isAlreadyIssued(Student student, String courseID)
    {
        ArrayList<Certificates> earned = student.getEarnedCertificates();
        for (Certificates cert : earned)
        {
            if(cert.getCourseID().equals(courseID)) return true;
        }
        return false;
    }


}
