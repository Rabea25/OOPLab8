package org.example;

import java.util.*;

public class Analytics {
    private final UserService userService;
    private final CourseService courseService;

    public Analytics(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    public double getAverageQuizScore(String courseId, String lessonId) {
        List<String> enrolledStudents = courseService.getEnrolledStudents(courseId);
        if (enrolledStudents == null || enrolledStudents.isEmpty()) {
            return 0.0;
        }

        int totalScore = 0;
        int attemptCount = 0;

        for (String studentId : enrolledStudents) {
            Student student = (Student) userService.getUserById(studentId);
            if (student != null) {
                ArrayList<QuizAttempt> attempts = student.getQuizAttemptsbyLessonId(lessonId);
                for (QuizAttempt attempt : attempts) {
                    totalScore = totalScore + attempt.getScore();
                    attemptCount = attemptCount + 1;
                }
            }
        }

        if (attemptCount > 0) {
            return (double) totalScore / attemptCount;
        } else {
            return 0.0;
        }
    }

    public double getCourseCompletionPercentage(String courseId) {
        List<String> enrolledStudents = courseService.getEnrolledStudents(courseId);
        if (enrolledStudents == null || enrolledStudents.isEmpty()) {
            return 0.0;
        }

        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return 0.0;
        }

        int totalLessons = course.getLessons().length;
        if (totalLessons == 0) {
            return 0.0;
        }

        int totalCompletedLessons = 0;
        for (String studentId : enrolledStudents) {
            Student student = (Student) userService.getUserById(studentId);
            if (student != null) {
                totalCompletedLessons = totalCompletedLessons + student.getProgress(courseId).length;
            }
        }

        int total = totalLessons * enrolledStudents.size();
        return ((double) totalCompletedLessons / total) * 100;
    }

    public int getCompletionCount(String courseId, String lessonId) {
        List<String> enrolledStudents = courseService.getEnrolledStudents(courseId);
        if (enrolledStudents == null) {
            return 0;
        }

        int count = 0;
        for (String studentId : enrolledStudents) {
            Student student = (Student) userService.getUserById(studentId);
            if (student != null) {
                String[] completedLessons = student.getProgress(courseId);
                for (String completedLessonId : completedLessons) {
                    if (completedLessonId.equals(lessonId)) {
                        count = count +1;
                        break;
                    }
                }
            }
        }
        return count;
    }

    public double getStudentAverageScore(String studentId, String courseId) {
        Student student = (Student) userService.getUserById(studentId);
        if (student == null) {
            return 0.0;
        }

        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return 0.0;
        }

        int totalScore = 0;
        int attemptCount = 0;
        Lesson[] lessons = course.getLessons();
        for (Lesson lesson : lessons) {
            ArrayList<QuizAttempt> attempts = student.getQuizAttemptsbyLessonId(lesson.getLessonId());
            for (QuizAttempt attempt : attempts) {
                if (attempt.getCourseId().equals(courseId)) {
                    totalScore = totalScore + attempt.getScore();
                    attemptCount = attemptCount + 1;
                }
            }
        }

        if (attemptCount > 0){
            return (double)totalScore /attemptCount;
        } else {
            return 0.0;
        }
    }

    public double getStudentCompletionPercentage(String studentId, String courseId) {
        Student student = (Student) userService.getUserById(studentId);
        if (student == null) {
            return 0.0;
        }

        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return 0.0;
        }

        int totalLessons = course.getLessons().length;
        if (totalLessons == 0) {
            return 0.0;
        }

        int completedLessons = student.getProgress(courseId).length;
        return ((double) completedLessons / totalLessons) * 100;
    }
}