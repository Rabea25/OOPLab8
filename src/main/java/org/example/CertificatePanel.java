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
    private JPanel rootPanel;
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
    }

    private void loadTableData()
    {
        String[] cols = {"Course ID", "Issue Date", "Certificate ID"};
        ArrayList<Certificates> certificates = currentStudent.getEarnedCertificates();
        Object[][] data = new Object[certificates.size()][3];

        for(int i = 0; i<certificates.size(); i++)
        {
            Certificates cert = certificates.get(i);
            data[i][0] = cert.getCourseID();
            data[i][1] = cert.getIssueDate().toString();
            data[i][2] = cert.getCertificateID();
        }

        tableModel = new DefaultTableModel(data, cols)
        {
            public boolean isCellEditable(int row, int column) {return false;}
        };

        certTable.setModel(tableModel);
        certTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        if(currentStudent.getEarnedCertificates().isEmpty()) return;

        try
        {
            Certificates selectedCert = currentStudent.getEarnedCertificates().get(selectedRow);
            String certificateCont = selectedCert.getCertificateContent();
            String courseID = selectedCert.getCourseID();

            Object[] options = {"View", "Download", "Cancel"};

            int choice = JOptionPane.showOptionDialog(
                    rootPanel,
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
            JOptionPane.showMessageDialog(rootPanel, "Unable to retrieve certificate details.", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewCertificate(String content, String courseID)
    {

    }

    private void downloadCertificate(String content, String courseID)
    {

    }
}
