package org.example;

public class Validations {
    public static boolean validateEmail(String email){
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        email = email.trim();
        String pattern = "^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$"; //pattern bt check en kolo characters aw dot aw dash w keda abl w b3d el @ w ends with . ay haga b2a min 2
        return email.matches(pattern);
    }

    public static boolean isNonEmpty(String text){
        return text != null && !text.trim().isEmpty();
    }

    public static boolean isValidTitle(String title){
        return title != null && !title.trim().isEmpty() && title.length() >= 4;
    }

    public static boolean isValidDescription(String description){
        return description != null && !description.trim().isEmpty() && description.length() >= 10;
    }

    public static boolean isValidContent(String content){
        return content != null && !content.trim().isEmpty() && content.length() >= 10;
    }

    public static boolean validatePassword(String pass){
        if (pass == null || pass.trim().isEmpty() || pass.length() < 4) {
            return false;
        }
        boolean upper = false;
        boolean lower = false;
        boolean digit = false;
        char[] password =  pass.toCharArray();
        for (char c : password ) {
            if (Character.isUpperCase(c)) {
                upper = true;
            }
            if (Character.isLowerCase(c)) {
                lower = true;
            }
            if (Character.isDigit(c)) {
                digit = true;
            }
            if (upper && lower && digit){
                break;
            }
        }
        return upper && lower && digit;
    }
}