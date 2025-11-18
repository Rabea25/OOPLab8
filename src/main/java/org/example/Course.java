package org.example;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String description;
    private String title;
    private String courseId;
    private String instructorId;
    private List<Lesson> lessons;
    private List<String> enrolledStudents;

    public Course(String courseId, String title, String description, String instructorId) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        this.lessons = new ArrayList<>();
        this.enrolledStudents = new ArrayList<>();
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Lesson[] getLessons() {
        return lessons.toArray(new Lesson[0]);
    }

    public List<String> getEnrolledStudents() {
        return enrolledStudents;
    }
    public String getInstructorId() {
        return instructorId;
    }
    public void enrollStudent(String studentID) {
        enrolledStudents.add(studentID);
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    Lesson getLessonById(String lessonId) {
        for (Lesson l : lessons) {
            if (l.getLessonId().equals(lessonId)) {
                return l;
            }
        }return null;
    }
    public boolean editLesson(Lesson updLesson){
        Lesson lesson = getLessonById(updLesson.getLessonId());
        if(lesson == null) return false;
        lessons.remove(lesson);
        lessons.add(updLesson);
        return true;
    }
    boolean removeLessonById(String lessonId) {
        for (Lesson l : lessons) {
            if (l.getLessonId().equals(lessonId)) {
                lessons.remove(l);
                return true;
            }
        }
        return false;
    }

    public void removeStudent(String studentID) {
        enrolledStudents.remove(studentID);
    }

    boolean hasLesson(String lessonId) {
        for (Lesson l : lessons) {
            if (l.getLessonId().equals(lessonId)) {
                return true;
            }
        } return false;
    }

    boolean hasStudent(String studentID) {
        for (String s : enrolledStudents) {
            if (s.equals(studentID)) {
                return true;
            }
        } return false;
    }

}