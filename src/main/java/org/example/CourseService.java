package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class CourseService {

    private List<Course> courses;
    private UserService userService;

    public CourseService(UserService userService) {
        this.userService = userService;
        this.courses = JsonDatabaseManager.loadCourses();
    }

    private void saveCourses() {
        JsonDatabaseManager.writeCourses(courses);
    }

    public Course[] getAllCourses() {
        return courses.toArray(new Course[0]);
    }

    public Course getCourseById(String courseId) {
        for (Course c : courses) {
            if (c.getCourseId().equals(courseId))
                return c;
        }
        return null;
    }

    public Course[] getCoursesByInstructor(String instructorID) {
        List<Course> AllCourses = new ArrayList<>();
        for (Course c : courses) {
            if (c.getInstructorId().equals(instructorID))
                AllCourses.add(c);
        }
        return AllCourses.toArray(new Course[0]);
    }

    public List<Course> getCoursesForStudent(String studentId) {
        User student = userService.getUserById(studentId);
        List<Course> result = new ArrayList<>();
        if (student == null || !(student instanceof Student)) {
            return result;
        }

        Student stud = (Student) student;
        String[] enrolledCourses = stud.getEnrolledCourses();
        for (String courseId : enrolledCourses) {
            Course course = getCourseById(courseId);
            if (course != null) {
                result.add(course);
            }
        }
        return result;
    }


    public Course createCourse(String title, String description, String instructorId) {
        String courseID = GenerateID.generateCourseId();
        Course newCourse = new Course(courseID, title, description, instructorId);
        courses.add(newCourse);
        Instructor instructor = (Instructor) userService.getUserById(instructorId);
        instructor.addCourse(courseID);
        userService.updateUser(instructor);
        saveCourses();
        return newCourse;
    }

    public boolean editCourse(String courseId, String newT, String newDes) {
        for (Course c : courses) {
            if (c.getCourseId().equals(courseId)) {
                c.setTitle(newT);
                c.setDescription(newDes);

                saveCourses();
                return true;
            }
        }
        return false;
    }

    public boolean deleteCourse(String courseId) {
        Course c = getCourseById(courseId);
        if (c == null) return false;

        Instructor instructor = (Instructor) userService.getUserById(c.getInstructorId());
        instructor.removeCourse(courseId);
        userService.updateUser(instructor);

        List<String> enrolledStudents = c.getEnrolledStudents();
        for(String sId : enrolledStudents){
            Student s = (Student) userService.getUserById(sId);
            s.removeCourse(courseId);
            userService.updateUser(s);
        }

        courses.remove(c);
        saveCourses();

        return true;
    }

    public boolean addLesson(String courseId, String title, String content, List<String> resources) {
        Course c = getCourseById(courseId);
        if(c == null) return  false;


        String lessonId = GenerateID.generateLessonId();
        Lesson newLesson = new Lesson(lessonId, title, content);
        newLesson.setResources(resources);
        c.addLesson(newLesson);
        saveCourses();

        return true;
    }

    public boolean editLesson(String courseId, String lessonId, String newT, String newC) {
        Course c = getCourseById(courseId);
        if(c == null) return false;
        Lesson l = c.getLessonById(lessonId);
        if(l == null) return false;

        l.setTitle(newT);
        l.setContent(newC);
        c.editLesson(l);
        saveCourses();

        return true;

    }

    public boolean deleteLesson(String courseId, String lessonId) {
        Course c = getCourseById(courseId);
        if(c == null) return false;

        return c.removeLessonById(lessonId);
    }

    public Lesson[] getLessons(String courseId) {
        Course c = getCourseById(courseId);
        if(c == null) return null;
        return c.getLessons();
    }


    public Lesson getLesson(String courseId, String lessonId) {
        Course c = getCourseById(courseId);
        if(c == null) return null;
        return c.getLessonById(lessonId);
    }

    public boolean enrollStudent(String courseId, String studentId) {

        User user = userService.getUserById(studentId);
        if (!(user instanceof Student)) {
            return false;
        }
        Student student = (Student) user;
        Course course = getCourseById(courseId);
        if (course == null) {
            return false;
        }
        student.enrollCourse(courseId);
        course.enrollStudent(studentId);
        userService.updateUser(student);
        saveCourses();

        return true;
    }

    public boolean addCourse(Course course) {
        courses.add(course);
        saveCourses();
        return true;
    }

    public boolean unenrollStudent(String courseId, String studentId) {
        User user = userService.getUserById(studentId);
        if (!(user instanceof Student)) {
            return false;
        }
        Student student = (Student) user;
        Course course = getCourseById(courseId);
        if(course == null) {
            return false;
        }

        course.removeStudent(studentId);
        student.removeCourse(courseId);
        userService.updateUser(student);
        saveCourses();
        return true;
    }

    public List<String> getEnrolledStudents(String courseId) {
        Course course = getCourseById(courseId);
        if (course == null) {
            return null;
        }
        return course.getEnrolledStudents();
    }

    public boolean addCourse(String title, String description, String instructorId) {
        User user = userService.getUserById(instructorId);
        if (user == null || !(user instanceof Instructor)) {
            return false;
        }
        String courseId = GenerateID.generateCourseId();

        Course newCourse = new Course(courseId, title, description, instructorId);
        courses.add(newCourse);
        saveCourses();
        return true;
    }


    public boolean markLessonCompleted(String courseId, String studentId, String lessonId) {

        User user = userService.getUserById(studentId);
        if (user == null || !(user instanceof Student)) {
            return false;
        }

        Student student = (Student) user;
        student.completeLesson(courseId, lessonId);
        userService.updateUser(student);

        return true;
    }


    public boolean isLessonCompleted(String courseId, String studentId, String lessonId) {
        User u = userService.getUserById(studentId);
        if (u == null || !(u instanceof Student)) {
            return false;
        }

        Student student = (Student) u;
        String[] completedLessons = student.getProgress(courseId);

        for(String lId : completedLessons) {
            if (lId.equals(lessonId)) {
                return true;
            }
        }
        return false ;
    }
    public List<String> getCompletedLessons(String courseId, String studentId) {
        User u = userService.getUserById(studentId);
        if (u == null || !(u instanceof Student)) {
            return new ArrayList<>();
        }

        Student student = (Student) u;
        String[] completedLessons = student.getProgress(courseId);
        return Arrays.asList(completedLessons);
    }

}

