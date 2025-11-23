package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AttemptDialog extends JDialog{
    private JLabel statusLabel;
    private JLabel questionStatusLabel;
    private JButton nextButton;
    private JButton prevButton;
    private JPanel root;
    private JLabel questionNoLabel;
    private JButton returnButton;
    private JLabel questionLabel;
    private JRadioButton choice1RadioButton;
    private JLabel o1Label;
    private JRadioButton choice2RadioButton;
    private JLabel o2Label;
    private JRadioButton choice3RadioButton;
    private JLabel o3Label;
    private JRadioButton choice4RadioButton;
    private JLabel o4Label;
    private ButtonGroup choices;
    private ArrayList<String> questions;
    private ArrayList<String[]> options;
    private int[] studentAnswers;
    private Quiz quiz;
    private QuizAttempt attempt;
    private int currentQuestionIndex = 0;
    private ArrayList<Integer> correction;

    public AttemptDialog(Quiz quiz, QuizAttempt attempt){
        this.attempt = attempt;
        this.quiz = quiz;

        questions = quiz.getQuestions();
        options = quiz.getOptions();
        studentAnswers = attempt.getAnswers();
        correction = quiz.getQuizcorrection(studentAnswers);

        int score = quiz.getQuizGrade(studentAnswers);
        boolean passed = quiz.passed(score);

        statusLabel.setVisible(true);
        if(passed) statusLabel.setText("Quiz Passed. Your score is "+score+"/"+questions.size());
        else statusLabel.setText("Quiz Failed. Your score is "+score+"/"+questions.size());
        questionStatusLabel.setVisible(true);

        choices = new ButtonGroup();
        choices.add(choice1RadioButton);
        choices.add(choice2RadioButton);
        choices.add(choice3RadioButton);
        choices.add(choice4RadioButton);
        choices.clearSelection();
        choice1RadioButton.setEnabled(false);
        choice2RadioButton.setEnabled(false);
        choice3RadioButton.setEnabled(false);
        choice4RadioButton.setEnabled(false);

        prevButton.addActionListener(e -> {
            currentQuestionIndex--;
            updateQuestionCorrection();
        });
        nextButton.addActionListener(e -> {
            currentQuestionIndex++;
            updateQuestionCorrection();
        });

        returnButton.addActionListener(e -> this.dispose());

        updateQuestionCorrection();

        this.setContentPane(root);
        this.setModal(true);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle("Quiz Review");
        this.setVisible(true);


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
