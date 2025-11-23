package org.example;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class Student extends User{
    private ArrayList<String> enrolledCourses;
    private Map<String, ArrayList<String>> progress;
    private ArrayList<QuizAttempt> quizAttempts;
    @Expose
    private ArrayList<Certificates> earnedCertificates = new ArrayList<>(); //certification addition

    public Student(String userId, String role, String username, String email, String passwordHash) {
        super(userId, role, username, email, passwordHash);
        this.progress = new java.util.HashMap<>();
        this.quizAttempts = new ArrayList<>();
        this.enrolledCourses = new ArrayList<>();
    }

    public void enrollCourse(String courseId) {
        enrolledCourses.add(courseId);
        progress.put(courseId, new ArrayList<>());
    }
    public void completeLesson(String courseId, String lessonId) {
        ArrayList<String> completedLessons = progress.get(courseId);
        if (completedLessons != null && !completedLessons.contains(lessonId)) {
            completedLessons.add(lessonId);
        }
    }
    public String[] getEnrolledCourses() {
        return enrolledCourses.toArray(new String[0]);
    }
    public void removeCourse(String courseId) {
        enrolledCourses.remove(courseId);
        progress.remove(courseId);
    }
    public String[] getProgress(String courseId) {
        ArrayList<String> completedLessons = progress.get(courseId);
        if (completedLessons != null) {
            return completedLessons.toArray(new String[0]);
        } else {
            return new String[0];
        }
    }
    public void addQuizAttempt(QuizAttempt attempt) {
        quizAttempts.add(attempt);
    }
    public ArrayList<QuizAttempt> getQuizAttemptsbyLessonId(String lessonId) {
        ArrayList<QuizAttempt> attemptsForLesson = new ArrayList<>();
        for (QuizAttempt attempt : quizAttempts) {
            if (attempt.getLessonId().equals(lessonId)) {
                attemptsForLesson.add(attempt);
            }
        }
        attemptsForLesson.sort(Comparator.comparingInt(QuizAttempt::getAttemptNumber));
        return attemptsForLesson;
    }
    public void removeQuizAttemptsByLessonId(String lessonId){
        ArrayList<QuizAttempt> e = getQuizAttemptsbyLessonId(lessonId);
        for(QuizAttempt qa : e){
            quizAttempts.remove(qa);
        }
    }

    public ArrayList<Certificates> getEarnedCertificates () {return earnedCertificates; }

    public void addCertificate(Certificates certificate)
    {
        if(certificate != null) this.earnedCertificates.add(certificate);
    }
    public void removeCertificate(String courseId)
    {
        Certificates toRemove = null;
        for(Certificates certificate : this.earnedCertificates)
        {
            if(certificate.getCourseID().equals(courseId))
            {
                toRemove = certificate;
                break;
            }
        }
        if(toRemove != null) this.earnedCertificates.remove(toRemove);
    }

}
