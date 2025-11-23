package org.example;

import java.util.ArrayList;
import java.util.Objects;

public class Quiz {
    private String title;
    private String lessonId;

    private ArrayList<String> questions;
    private ArrayList<String[]> options;
    private ArrayList<Integer> answers;

    public Quiz(String title, String lessonId, ArrayList<String> questions, ArrayList<String[]> options, ArrayList<Integer> answers){
        this.title = title;
        this.lessonId = lessonId;
        this.questions = questions;
        this.options = options;
        this.answers = answers;
    }

    public ArrayList<String> getQuestions(){
        return new ArrayList<>(questions);
    }
    public ArrayList<String[]> getOptions(){
        return new ArrayList<>(options);
    }
    public ArrayList<Integer> getAnswers(){
        return new ArrayList<>(answers);
    }

    public void setQuestions(ArrayList<String> questions, ArrayList<String[]> options, ArrayList<Integer> answers){
        this.questions = questions;
        this.options = options;
        this.answers = answers;
    }

    public int getNumberOfQuestions(){
        return questions.size();
    }
    public void addQuestion(String q, String[] choices, int ans){
        questions.add(q);
        options.add(choices);
        answers.add(ans);
    }

    public void removeQuestion(int index){
        questions.remove(index);
        options.remove(index);
        answers.remove(index);
    }


    public void editQuestion(int index, String q, String[] choices, int ans){
        questions.set(index, q);
        options.set(index, choices);
        answers.set(index, ans);
    }

    public String getTitle(){
        return title;
    }
    public String setTitle(String title){
        this.title = title;
        return title;
    }
    public String getLessonId(){return lessonId;}
    public int getQuizGrade(int[] userAnswers){
        int x = 0;
        for(int i=0; i<answers.size(); i++) x += (Objects.equals(answers.get(i), userAnswers[i])) ? 1 : 0;
        return x;
    }
    public ArrayList<Integer> getQuizcorrection(int[] userAnswers){
        ArrayList<Integer> correction = new ArrayList<>();
        for(int i=0; i<answers.size(); i++) correction.add( (Objects.equals(answers.get(i), userAnswers[i])) ? 1 : 0);
        return correction;
    }

    public boolean passed(int score){
        return score*1.0 >= (questions.size()*0.7);
    }


}
