package org.example;

public class QuizAttempt {
    private final String lessonId, courseId;
    private final int score, attemptNumber;
    private final int[] answers;
    private final boolean passed;


    public QuizAttempt(String lessonId, String courseId, int score, int[] answers, int attemptNumber){
        this.lessonId = lessonId;
        this.courseId = courseId;
        this.score = score;
        this.answers = answers;
        this.attemptNumber = attemptNumber;
        passed = score * 1.0 / answers.length >= 0.7;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public int getScore() {
        return score;
    }

    public String getCourseId() {
        return courseId;
    }

    public int[] getAnswers() {
        return answers;
    }

    public String getLessonId() {
        return lessonId;
    }
    public boolean isPassed(){ return passed;}

}
