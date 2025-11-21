package org.example;

import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private String lessonId;
    private String title;
    private String content;
    private List<String> resources;
    private Quiz quiz;

    public Lesson(String lessonId, String title, String content, List<String> resources) {
        this.lessonId = lessonId;
        this.title = title;
        this.content = content;
        this.resources = resources;
    }


    public String getLessonId() {
        return lessonId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<String> getResources() {
        return resources;
    }

    void setTitle(String title) {
        this.title = title;}

    void setContent(String content) {
        this.content = content;
    }
    void setResources(List<String> resources) {
        this.resources = resources;
    }

    void addResource(String resource) {
        this.resources.add(resource);
    }
    boolean removeResource(String resource) {
        for (String res : resources) {
            if (res.equals(resource)) {
                resources.remove(res);
                return true;
            }
        }
        return false;
    }
    public Quiz getQuiz() {
        return quiz;
    }
    public void updateQuiz(String title, ArrayList<String> questions, ArrayList<String[]> options, ArrayList<Integer> answers) {
        if(this.quiz == null) this.quiz = new Quiz(title, this.lessonId, questions, options, answers);
        else{
            this.quiz.setTitle(title);
            this.quiz.setQuestions(questions, options, answers);
        }

    }
}




