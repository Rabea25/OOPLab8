package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CertificatePanel extends JDialog{
    private JPanel root;
    private JTable certTable;
    private JButton backButton;

    private Student currentStudent;
    private DefaultTableModel tableModel;

    public CertificatePanel(Student student)
    {
        this.currentStudent = student;

        loadTableData();
        DoubleClickListener();

        backButton.addActionListener(e -> this.dispose());

        this.setModal(true);
        this.setContentPane(root);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setTitle("Your Earned Certificates");
        this.setVisible(true);
    }

    private void loadTableData()
    {
        String[] cols = {"Course ID", "Issue Date", "Certificate ID"};
        ArrayList<Certificates> certificates = currentStudent.getEarnedCertificates();
        System.out.println("Earned :" + certificates.size());

        Object[][] data = new Object[certificates.size()][3];

        for(int i = 0; i<certificates.size(); i++)
        {
            Certificates cert = certificates.get(i);
            data[i][0] = cert.getCourseID();
            data[i][1] = cert.getIssueDate().toString();
            data[i][2] = cert.getCertificateID();
        }

        certTable.setModel(new DefaultTableModel(data, cols){
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        });
    }

    private void DoubleClickListener()
    {
        certTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
                {
                    int row = certTable.rowAtPoint(e.getPoint());
                    if(row != -1) { viewDownload(row);  }
                }
            }
        });
    }

    private void viewDownload(int selectedRow)
    {
        if(currentStudent.getEarnedCertificates().isEmpty())
            return;

        try
        {
            Certificates selectedCert = currentStudent.getEarnedCertificates().get(selectedRow);
            String certificateCont = selectedCert.getCertificateContent();
            String courseID = selectedCert.getCourseID();

            Object[] options = {"View", "Download", "Cancel"};

            int choice = JOptionPane.showOptionDialog(
                    root,
                    "Select your option for this course: " + courseID,
                    "Certificate Choice",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == JOptionPane.YES_OPTION) {
                viewCertificate(certificateCont, courseID);
            } else if (choice == JOptionPane.NO_OPTION) {
                downloadCertificate(certificateCont, courseID);
            }
        } catch (IndexOutOfBoundsException e)
        {
            JOptionPane.showMessageDialog(root, "Unable to retrieve certificate details.", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewCertificate(String content, String courseID)
    {
        JTextArea contentArea = new JTextArea(content);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(root, scrollPane,"Certificate Content (" + courseID + ")", JOptionPane.PLAIN_MESSAGE);
    }

    private void downloadCertificate(String content, String courseID)
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save the certificate");

        String defaultFileName = String.format("%s_%s_Certificate.json", currentStudent.getUserId(), courseID);
        fileChooser.setSelectedFile(new File(defaultFileName));

        int userSelection = fileChooser.showSaveDialog(root);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getName().toLowerCase().endsWith(".json")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".json");
            }

            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(content);
                JOptionPane.showMessageDialog(root,
                        "Certificate successfully downloaded to:\n" + fileToSave.getAbsolutePath(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(root,
                        "Error saving file: " + ex.getMessage(),
                        "Download Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
