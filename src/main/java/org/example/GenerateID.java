package org.example;

import java.util.List;

public class GenerateID {
    public static String generateStudentId(){
        List<User> students = JsonDatabaseManager.loadUsers();
        int max = 0;
        for (User u : students){
            if (u instanceof Student) {
                String ID = u.getUserId();
                int number = extractNumber(ID);
                if (number > max){
                    max = number;
                }
            }
        }
        return "S" + (max + 1);
    }

    public static String generateInstructorId(){
        List<User> instructor = JsonDatabaseManager.loadUsers();
        int max = 0;
        for (User u : instructor){
            if (u instanceof Instructor) {
                String ID = u.getUserId();
                int number = extractNumber(ID);
                if (number > max){
                    max = number;
                }
            }
        }
        return "I" + (max + 1);
    }

    public static String generateCourseId(){
        List<Course> courses = JsonDatabaseManager.loadCourses();
        int max = 0;
        for (Course cs : courses) {
                String ID = cs.getCourseId();
                int number = extractNumber(ID);
                if (number > max){
                    max = number;
                }
        }
        return "C" + (max + 1);
    }

    public static String generateLessonId(){
        List<Course> courses = JsonDatabaseManager.loadCourses();
        int max = 0;
        for (Course cs : courses){
                Lesson[] lessons = cs.getLessons();
                for (Lesson l : lessons) {
                    String ID = l.getLessonId();
                    int number = extractNumber(ID);
                    if (number > max){
                        max = number;
                    }
                }
        }
        return "L" + (max + 1);
    }

    private static int extractNumber(String id) {
        try {
            String numberPart = id.substring(1);
            return Integer.parseInt(numberPart);
        } catch (NumberFormatException boudy) {
            return 0;
        }
    }
}
