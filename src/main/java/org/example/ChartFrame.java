package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ChartFrame extends JFrame {
    private ArrayList<String> labels;
    private ArrayList<Double> values;
    private String chartTitle;
    private String xLabel;
    private String yLabel;

    public ChartFrame(ArrayList<String> labels, ArrayList<Double> values, String title, String xLabel, String yLabel) {
        this.labels = labels;
        this.values = values;
        this.chartTitle = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.setTitle(title);
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        ChartPanel chartPanel = new ChartPanel();
        this.add(chartPanel);
        this.setVisible(true);
    }

    class ChartPanel extends JPanel {
        private final int PADDING = 60;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D chart = (Graphics2D) g;
            chart.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (labels == null || labels.isEmpty() || values == null || values.isEmpty()) {
                chart.drawString("No data to display", getWidth() / 2 - 50, getHeight() / 2);
                return;
            }

            int width = getWidth() - 2 * PADDING;
            int height = getHeight() - 2 * PADDING;
            double maxValue = 0;
            for (Double value : values) {
                if (value > maxValue) {
                    maxValue = value;
                }
            }
            if (maxValue == 0) {
                maxValue = 1;
            }
            chart.setFont(new Font("Times New Roman", Font.BOLD, 18));
            FontMetrics titleMetrics = chart.getFontMetrics();
            int titleWidth = titleMetrics.stringWidth(chartTitle);
            chart.drawString(chartTitle, (getWidth() - titleWidth) / 2, PADDING / 2);
            chart.setColor(Color.BLACK);
            chart.drawLine(PADDING, getHeight() - PADDING, getWidth() - PADDING, getHeight() - PADDING);
            chart.drawLine(PADDING, PADDING, PADDING, getHeight() - PADDING);

            chart.setFont(new Font("Times New Roman", Font.PLAIN, 12));
            chart.drawString(yLabel, 10, PADDING + height / 2);
            FontMetrics fm = chart.getFontMetrics();
            int xLabelWidth = fm.stringWidth(xLabel);
            chart.drawString(xLabel, PADDING + width / 2 - xLabelWidth / 2, getHeight() - 10);

            int barCount = labels.size();
            int totalBarSpace = width / barCount;
            int barWidth = totalBarSpace * 2 / 3;
            int gap = totalBarSpace / 3;
            for (int i = 0; i < labels.size(); i++) {
                String label = labels.get(i);
                Double value = values.get(i);
                int x = PADDING + i * totalBarSpace + gap / 2;
                int barHeight = (int) ((value / maxValue) * height);
                int y = getHeight() - PADDING - barHeight;
                chart.setColor(new Color(100, 149, 237));
                chart.fillRect(x, y, barWidth, barHeight);
                chart.setColor(Color.BLACK);
                chart.drawRect(x, y, barWidth, barHeight);

                chart.setFont(new Font("Times New Roman", Font.BOLD, 10));
                String valueText = String.format("%.1f", value);
                int valueWidth = chart.getFontMetrics().stringWidth(valueText);
                chart.drawString(valueText, x + (barWidth - valueWidth) / 2, y - 5);

                chart.setFont(new Font("Times New Roman", Font.PLAIN, 9));
                String displayLabel = label;
                if (label.length() > 12) {
                    displayLabel = label.substring(0, 9) + "...";
                }
                int labelWidth = chart.getFontMetrics().stringWidth(displayLabel);
                chart.drawString(displayLabel, x + (barWidth - labelWidth) / 2, getHeight() - PADDING + 15);
            }

            chart.setFont(new Font("Times New Roman", Font.PLAIN, 10));
            int numberOfMarkers = 5;
            for (int i = 0; i <= numberOfMarkers; i++) {
                double markerValue = (maxValue / numberOfMarkers) * i;
                int yPos = getHeight() - PADDING - (int) ((markerValue / maxValue) * height);
                chart.drawLine(PADDING - 5, yPos, PADDING, yPos);
                String markerText = String.format("%.0f", markerValue);
                chart.drawString(markerText, PADDING - 35, yPos + 5);
            }
        }
    }
}