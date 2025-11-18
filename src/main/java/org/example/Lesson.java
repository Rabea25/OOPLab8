package org.example;

import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private String lessonId;
    private String title;
    private String content;
    private List<String> resources;

    public Lesson(String lessonId, String title, String content) {
        this.lessonId = lessonId;
        this.title = title;
        this.content = content;
        this.resources = new ArrayList<>();
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
        }return false;}
}




