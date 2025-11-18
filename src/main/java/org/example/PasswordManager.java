package org.example;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordManager {
    public static String hashing(String password) {
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = hasher.digest(password.getBytes());
            StringBuilder hashedPassword = new StringBuilder();
            for (byte b : hashedBytes){
                hashedPassword.append(String.format("%02x", b));
            }
            return hashedPassword.toString();
        } catch (NoSuchAlgorithmException bdbd ){
            throw new RuntimeException();
        }
    }

    public static boolean checkPassword(String password, String storedPassword) {
        String hashedPassword = hashing(password);
        return storedPassword.equals(hashedPassword);
    }
}
