package org.example;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatIntelliJLaf());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        JFrame frame = new MainPanel();
    }
}