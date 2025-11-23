package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuizDialog extends JDialog{
    private JPanel root;
    private JLabel titleLabel;
    private JButton nextButton;
    private JButton prevButton;
    private JLabel questionNoLabel;
    private JRadioButton choice1RadioButton;
    private JRadioButton choice2RadioButton;
    private JRadioButton choice3RadioButton;
    private JRadioButton choice4RadioButton;
    private JLabel questionLabel;
    private JLabel o1Label;
    private JLabel o2Label;
    private JLabel o3Label;
    private JLabel o4Label;
    private JButton submitButton;
    private JLabel statusLabel;
    private JLabel questionStatusLabel;
    private Quiz quiz;
    private ButtonGroup choices;
    private ArrayList<String> questions;
    private ArrayList<String[]> options;
    private int[] studentAnswers;
    private final Student student;
    private final Lesson lesson;
    private final Course course;
    private final CourseService courseService;
    private int currentQuestionIndex;
    private int answered = 0;
    private ArrayList<Integer> correction;
    public QuizDialog(Student student, Lesson lesson, Course course, CourseService courseService){
        this.student = student;
        this.lesson = lesson;
        this.course = course;
        this.courseService = courseService;
        statusLabel.setVisible(false);
        questionStatusLabel.setVisible(false);
        titleLabel.setText("Course "+course.getCourseId()+" : "+lesson.getLessonId()+" Quiz");
        quiz = lesson.getQuiz();

        questions = quiz.getQuestions();
        options = quiz.getOptions();
        studentAnswers = new int[questions.size()];
        Arrays.fill(studentAnswers, -1);

        choices = new ButtonGroup();
        choices.add(choice1RadioButton);
        choices.add(choice2RadioButton);
        choices.add(choice3RadioButton);
        choices.add(choice4RadioButton);
        choices.clearSelection();

        prevButton.addActionListener(e -> {
            currentQuestionIndex--;
            updateCurrentQuestion();
        });
        nextButton.addActionListener(e -> {
            currentQuestionIndex++;
            updateCurrentQuestion();
        });

        submitButton.addActionListener(e -> submit());

        choice1RadioButton.addActionListener(e -> saveCurrentAnswer());
        choice2RadioButton.addActionListener(e -> saveCurrentAnswer());
        choice3RadioButton.addActionListener(e -> saveCurrentAnswer());
        choice4RadioButton.addActionListener(e -> saveCurrentAnswer());

        updateCurrentQuestion();

        this.setContentPane(root);
        this.setModal(true);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle("Quiz");
        this.setVisible(true);
    }

    public void updateCurrentQuestion(){
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);

        if(currentQuestionIndex == 0) prevButton.setEnabled(false);
        if(currentQuestionIndex == questions.size()-1) nextButton.setEnabled(false);

        questionNoLabel.setText("Question "+(currentQuestionIndex+1));
        questionLabel.setText(questions.get(currentQuestionIndex));
        o1Label.setText(options.get(currentQuestionIndex)[0]);
        o2Label.setText(options.get(currentQuestionIndex)[1]);
        o3Label.setText(options.get(currentQuestionIndex)[2]);
        o4Label.setText(options.get(currentQuestionIndex)[3]);

        int x = studentAnswers[currentQuestionIndex];
        if(x==0) choices.setSelected(choice1RadioButton.getModel(), true);
        else if(x==1) choices.setSelected(choice2RadioButton.getModel(), true);
        else if(x==2) choices.setSelected(choice3RadioButton.getModel(), true);
        else if(x==3) choices.setSelected(choice4RadioButton.getModel(), true);
        else choices.clearSelection();
    }
    public void saveCurrentAnswer(){
        if(choices.getSelection() == null) return;
        int idx = 0;
        if(choice2RadioButton.isSelected()) idx = 1;
        else if(choice3RadioButton.isSelected()) idx = 2;
        else if(choice4RadioButton.isSelected()) idx = 3;

        System.out.println("Saving answer "+idx+" for question "+currentQuestionIndex);

        if(studentAnswers[currentQuestionIndex]==-1) answered++;
        studentAnswers[currentQuestionIndex] = idx;
    }

    private void submit(){
        if(answered != questions.size()){
            JOptionPane.showMessageDialog(root, "You must answer all questions!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        correction = quiz.getQuizcorrection(studentAnswers);
        int score = quiz.getQuizGrade(studentAnswers);
        boolean passed = quiz.passed(score);

        statusLabel.setVisible(true);
        if(passed) statusLabel.setText("Quiz Passed. Your score is "+score+"/"+questions.size());
        else statusLabel.setText("Quiz Failed. Your score is "+score+"/"+questions.size());
        questionStatusLabel.setVisible(true);
        this.pack();
        this.setLocationRelativeTo(null);
        prevButton.removeActionListener(prevButton.getActionListeners()[0]);
        nextButton.removeActionListener(nextButton.getActionListeners()[0]);

        choice1RadioButton.setEnabled(false);
        choice2RadioButton.setEnabled(false);
        choice3RadioButton.setEnabled(false);
        choice4RadioButton.setEnabled(false);

        submitButton.setText("Save and return");
        submitButton.removeActionListener(submitButton.getActionListeners()[0]);
        submitButton.addActionListener(e -> this.dispose());

        courseService.attemptQuiz(student, lesson, course, studentAnswers);

        prevButton.addActionListener(e -> {
            currentQuestionIndex--;
            updateQuestionCorrection();
        });
        nextButton.addActionListener(e -> {
            currentQuestionIndex++;
            updateQuestionCorrection();
        });

        currentQuestionIndex = 0;

        updateQuestionCorrection();



    }

    public void updateQuestionCorrection(){
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);

        if(currentQuestionIndex == 0) prevButton.setEnabled(false);
        if(currentQuestionIndex == questions.size()-1) nextButton.setEnabled(false);
        questionNoLabel.setText("Question "+(currentQuestionIndex+1));
        questionLabel.setText(questions.get(currentQuestionIndex));

        o1Label.setText(options.get(currentQuestionIndex)[0]);
        o2Label.setText(options.get(currentQuestionIndex)[1]);
        o3Label.setText(options.get(currentQuestionIndex)[2]);
        o4Label.setText(options.get(currentQuestionIndex)[3]);

        int correctAnswer = quiz.getAnswers().get(currentQuestionIndex);
        int studentAnswer = studentAnswers[currentQuestionIndex];

        System.out.println("Correct: "+correctAnswer+" Student: "+studentAnswer);

        if(studentAnswer==0) choice1RadioButton.setSelected(true);
        else if(studentAnswer==1) choice2RadioButton.setSelected(true);
        else if(studentAnswer==2) choice3RadioButton.setSelected(true);
        else choice4RadioButton.setSelected(true);

        if(correctAnswer==0) o1Label.setForeground(Color.GREEN);
        else o1Label.setForeground(Color.BLACK);
        if(correctAnswer==1) o2Label.setForeground(Color.GREEN);
        else o2Label.setForeground(Color.BLACK);
        if(correctAnswer==2) o3Label.setForeground(Color.GREEN);
        else o3Label.setForeground(Color.BLACK);
        if(correctAnswer==3) o4Label.setForeground(Color.GREEN);
        else o4Label.setForeground(Color.BLACK);

        if(correctAnswer != studentAnswer){
            questionStatusLabel.setText("Incorrect");
            questionStatusLabel.setForeground(Color.RED);
            if(studentAnswer==0) o1Label.setForeground(Color.RED);
            else if(studentAnswer==1) o2Label.setForeground(Color.RED);
            else if(studentAnswer==2) o3Label.setForeground(Color.RED);
            else o4Label.setForeground(Color.RED);
        }
        else{
            questionStatusLabel.setText("Correct");
            questionStatusLabel.setForeground(Color.GREEN);
        }
    }
}
