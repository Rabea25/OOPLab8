package org.example;

import java.util.ArrayList;
import java.util.Map;

public class Student extends User{
    ArrayList<String> enrolledCourses = new ArrayList<>();
    Map<String, ArrayList<String>> progress;
    ArrayList<QuizAttempt> quizAttempts = new ArrayList<>();
    public Student(String userId, String role, String username, String email, String passwordHash) {
        super(userId, role, username, email, passwordHash);
        progress = new java.util.HashMap<>();
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
        return attemptsForLesson;
    }


}
