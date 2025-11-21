package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

public class EditLessonDialog extends JDialog{
    private JPanel root;
    private JTextField titleField;
    private JTextField contentField;
    private JLabel courseLabel;
    private JLabel lessonLabel;
    private JButton saveButton;
    private JButton cancelButton;
    private JTextField resourceField;
    private JTextField qTitleField;
    private JButton nextButton;
    private JButton prevButton;
    private JTextField questionField;
    private JRadioButton choice1RadioButton;
    private JTextField choice1Field;
    private JRadioButton choice2RadioButton;
    private JRadioButton choice3RadioButton;
    private JRadioButton choice4RadioButton;
    private JTextField choice2Field;
    private JTextField choice3Field;
    private JTextField choice4Field;
    private JLabel questionNoLabel;
    private boolean saved = false;
    private int currentQuestionIndex = 0;
    private ArrayList<String> questions = new ArrayList<>();
    private ArrayList<String[]> options = new ArrayList<>();
    private ArrayList<Integer> answers = new ArrayList<>();
    private ButtonGroup choices;
    private Quiz quiz;
    public EditLessonDialog(Lesson lesson, CourseService courseService, Course course){

        titleField.setText(lesson.getTitle());
        contentField.setText(lesson.getContent());
        String resources = String.join(",", lesson.getResources());
        resourceField.setText(resources);
        lessonLabel.setText(lesson.getLessonId());
        courseLabel.setText(course.getCourseId());

        quiz = lesson.getQuiz();

        questions = quiz.getQuestions();
        options = quiz.getOptions();
        answers = quiz.getAnswers();

        choices = new ButtonGroup();
        choices.add(choice1RadioButton);
        choices.add(choice2RadioButton);
        choices.add(choice3RadioButton);
        choices.add(choice4RadioButton);
        choices.setSelected(choice1RadioButton.getModel(), true);

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title  = titleField.getText().trim();
                String content = contentField.getText().trim();
                String[] res = resourceField.getText().trim().split(",");

                Lesson b = courseService.getLessonByTitle(course.getCourseId(), title);

                if (!Validations.isNonEmpty(title) || !Validations.isValidTitle(title) || (b != null && !b.getLessonId().equals(lesson.getLessonId()))) {
                    JOptionPane.showMessageDialog(root,"Title cannot be empty and must be at least 4 characters, and must be unique","Invalid Title", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!Validations.isNonEmpty(content) || !Validations.isValidDescription(content)) {
                    JOptionPane.showMessageDialog(root,"content cannot be empty and must be at least 10 characters.","Invalid Description", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if(courseService.editLesson(course.getCourseId(), lesson.getLessonId(), title, content, res)) {
                    JOptionPane.showMessageDialog(root, "Lesson saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    dispose();
                }
                else {
                    JOptionPane.showMessageDialog(root, "Failed to edit lesson.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        questionNoLabel.setText("Question 1");
        prevButton.addActionListener(e -> {
            if(currentQuestionIndex == 0) return;
            if(saveCurrentQuestion("rev")) {
                currentQuestionIndex--;
                questionNoLabel.setText("Question "+(currentQuestionIndex+1));
                updateQuestionFields();
            }
        });

        nextButton.addActionListener(e -> {
            //if(currentQuestionIndex == questions.size()) return;
            if(saveCurrentQuestion("fwd")){
                currentQuestionIndex++;
                questionNoLabel.setText("Question "+(currentQuestionIndex+1));
                updateQuestionFields();
            }
        });


        updateQuestionFields();

        this.setContentPane(root);
        this.setModal(true);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle("Edit Lesson - " + lesson.getTitle());
        this.setVisible(true);

    }

    public boolean saveCurrentQuestion(String dir){
        String[] opts = new String[4];
        opts[0] = choice1Field.getText().trim();
        opts[1] = choice2Field.getText().trim();
        opts[2] = choice3Field.getText().trim();
        opts[3] = choice4Field.getText().trim();

        String qtitle = questionField.getText().trim();
        if(Objects.equals(dir, "rev") && currentQuestionIndex==questions.size() && qtitle.isEmpty() && opts[0].isEmpty() && opts[1].isEmpty() && opts[2].isEmpty() && opts[3].isEmpty()){
            return true;
        }
        if(((Objects.equals(dir, "rev") && currentQuestionIndex<questions.size()) || (Objects.equals(dir, "fwd"))) && (opts[0].isEmpty() || opts[1].isEmpty() || opts[2].isEmpty() || opts[3].isEmpty() || qtitle.isEmpty())){
            JOptionPane.showMessageDialog(root, "All question fields must be not empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int selectedAnswer = 0;
        if(choice2RadioButton.isSelected()) selectedAnswer = 1;
        else if(choice3RadioButton.isSelected()) selectedAnswer = 2;
        else if(choice4RadioButton.isSelected()) selectedAnswer = 3;

        if(currentQuestionIndex < questions.size()){
            questions.set(currentQuestionIndex, questionField.getText().trim());
            options.set(currentQuestionIndex, opts);
            answers.set(currentQuestionIndex, selectedAnswer);
        }
        else{
            questions.add(questionField.getText().trim());
            options.add(opts);
            answers.add(selectedAnswer);
        }

        return true;
    }

    public void updateQuestionFields(){
        if(currentQuestionIndex == 0) prevButton.setEnabled(false);
        else prevButton.setEnabled(true);
        if(currentQuestionIndex < questions.size()){
            questionField.setText(questions.get(currentQuestionIndex));
            choice1Field.setText(options.get(currentQuestionIndex)[0]);
            choice2Field.setText(options.get(currentQuestionIndex)[1]);
            choice3Field.setText(options.get(currentQuestionIndex)[2]);
            choice4Field.setText(options.get(currentQuestionIndex)[3]);
            int ans = answers.get(currentQuestionIndex);
            if(ans == 0) choices.setSelected(choice1RadioButton.getModel(), true);
            else if(ans == 1) choices.setSelected(choice2RadioButton.getModel(), true);
            else if(ans == 2) choices.setSelected(choice3RadioButton.getModel(), true);
            else choices.setSelected(choice4RadioButton.getModel(), true);

            //nextButton.setEnabled(true);
        }
        else{
            //nextButton.setEnabled(false);
            questionField.setText("");
            choice1Field.setText("");
            choice2Field.setText("");
            choice3Field.setText("");
            choice4Field.setText("");
            choices.setSelected(choice1RadioButton.getModel(), true);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
