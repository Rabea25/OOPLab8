package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CourseService {

    private final List<Course> courses;
    private final UserService userService;

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

    public ArrayList<Course> getCoursesByInstructorId(String instructorID) {
        ArrayList<Course> AllCourses = new ArrayList<>();
        for (Course c : courses) {
            if (c.getInstructorId().equals(instructorID))
                AllCourses.add(c);
        }
        return AllCourses;
    }

    public ArrayList<Course> getCoursesByStudentId(String studentId) {
        User user = userService.getUserById(studentId);
        ArrayList<Course> result = new ArrayList<>();
        if (user == null || !(user instanceof Student student)) {
            return result;
        }

        String[] enrolledCourses = student.getEnrolledCourses();
        for (String courseId : enrolledCourses) {
            Course course = getCourseById(courseId);
            if (course != null) {
                result.add(course);
            }
        }
        return result;
    }

    public Course getCourseByTitle(String title) {
        for (Course c : courses) {
            if (c.getTitle().equalsIgnoreCase(title))
                return c;
        }
        return null;
    }

    public Course createCourse(String title, String description, String instructorId) {
        User user = userService.getUserById(instructorId);
        if (user == null || !(user instanceof Instructor instructor)) return null;

        String courseID = Utilities.generateCourseId();
        Course newCourse = new Course(courseID, title, description, instructorId);

        instructor.addCourse(courseID);

        courses.add(newCourse);
        saveCourses();
        userService.updateUser(instructor);

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

    public boolean removeCourse(String courseId) {
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

    public void updateCourse(Course updatedCourse) {
        removeCourse(updatedCourse.getCourseId());
        courses.add(updatedCourse);
        saveCourses();
    }

    public Lesson getLessonByTitle(String courseId, String lessonTitle) {
        Course c = getCourseById(courseId);
        if(c == null) return null;

        for(Lesson l : c.getLessons()) {
            if(l.getTitle().equalsIgnoreCase(lessonTitle))
                return l;
        }
        return null;
    }

    public String addLesson(String courseId, String title, String content, List<String> resources) {
        Course c = getCourseById(courseId);
        if(c == null) return null;


        String lessonId = Utilities.generateLessonId();
        Lesson newLesson = new Lesson(lessonId, title, content, resources);
        c.addLesson(newLesson);
        saveCourses();

        return lessonId;
    }

    public String addLesson(String courseId, String title, String content, List<String> resources, String qtitle, ArrayList<String> questions, ArrayList<String[]> options, ArrayList<Integer> answers){
        Course c = getCourseById(courseId);
        if(c == null) return null;


        String lessonId = Utilities.generateLessonId();
        Lesson newLesson = new Lesson(lessonId, title, content, resources);
        newLesson.updateQuiz(qtitle, questions, options, answers);
        c.addLesson(newLesson);
        saveCourses();

        return lessonId;
    }


    public boolean editLesson(String courseId, String lessonId, String newT, String newC, String[] newR, String qtitle, ArrayList<String> questions, ArrayList<String[]> options, ArrayList<Integer> answers) {
        Course c = getCourseById(courseId);
        if(c == null) return false;

        Lesson l = c.getLessonById(lessonId);
        if(l == null) return false;

        deleteQuizRecords(courseId, lessonId);
        l.setTitle(newT);
        l.setContent(newC);
        l.setResources(Arrays.asList(newR));
        l.updateQuiz(qtitle, questions, options, answers);
        c.editLesson(l);
        saveCourses();

        return true;

    }

    public void deleteQuizRecords(String courseId, String lessonId){
        Course c = getCourseById(courseId);
        if(c==null) return;

        Lesson lesson = getLessonById(courseId, lessonId);
        for(String studentId : c.getEnrolledStudents()){
            ((Student) userService.getUserById(studentId)).removeQuizAttemptsByLessonId(lessonId);
        }
        userService.saveUsers();
    }

    public boolean removeLesson(String courseId, String lessonId) {
        Course c = getCourseById(courseId);
        if(c == null) return false;

        return c.removeLessonById(lessonId);
    }

    public ArrayList<Lesson> getLessons(String courseId) {
        Course c = getCourseById(courseId);
        if(c == null) return null;
        return new ArrayList<>(Arrays.asList(c.getLessons()));
    }


    public Lesson getLessonById(String courseId, String lessonId) {
        Course c = getCourseById(courseId);
        if(c == null) return null;
        return c.getLessonById(lessonId);
    }



    public boolean enrollStudent(String courseId, String studentId) {

        User user = userService.getUserById(studentId);
        if (!(user instanceof Student student)) {
            return false;
        }
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

    public boolean unenrollStudent(String courseId, String studentId) {
        User user = userService.getUserById(studentId);
        if (!(user instanceof Student student)) {
            return false;
        }
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

    public boolean completeLesson(String courseId, String studentId, String lessonId) {

        User user = userService.getUserById(studentId);
        if (user == null || !(user instanceof Student student)) {
            return false;
        }

        student.completeLesson(courseId, lessonId);
        userService.updateUser(student);

        return true;
    }



    public ArrayList<Lesson> getCompletedLessons(String userId, String courseId) {
        User user = userService.getUserById(userId);
        if(!(user instanceof Student student)) return null;

        String[] completedLessonIds = student.getProgress(courseId);
        ArrayList<Lesson> completedLessons = new ArrayList<>();

        for(String lessonId : completedLessonIds){
            Lesson lesson = getLessonById(courseId, lessonId);
            if(lesson != null) completedLessons.add(lesson);
        }

        return completedLessons;
    }

    public Quiz getQuiz(String lessonId, String courseId){
        Lesson lesson = getLessonById(courseId, lessonId);
        return lesson.getQuiz();
    }

    public void attemptQuiz(Student student, Lesson lesson, Course course, int[] answers){
        Quiz quiz = lesson.getQuiz();
        int attemptNumber = student.getQuizAttemptsbyLessonId(lesson.getLessonId()).size();
        QuizAttempt qa = new QuizAttempt(lesson.getLessonId(), course.getCourseId(), quiz.getQuizGrade(answers), answers, attemptNumber);
        student.addQuizAttempt(qa);
        if(qa.isPassed()){
            student.completeLesson(course.getCourseId(), lesson.getLessonId());
        }
        userService.updateUser(student);

    }

}

