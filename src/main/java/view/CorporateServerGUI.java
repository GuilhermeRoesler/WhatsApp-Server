package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.general.*;

public class CorporateServerGUI extends JFrame {
    private JButton startButton;
    private JButton stopButton;
    private JLabel statusLabel;
    private JTextArea logArea;
    private JPanel statsPanel;
    private DefaultPieDataset dataset;
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private boolean isServerRunning = false;
    private int connectedUsers = 0;
    private Timer statsTimer;

    public CorporateServerGUI() {
        setTitle("WhatsApp Enterprise Server Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main container with corporate blue background
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout(10, 10));
        mainContainer.setBackground(new Color(240, 244, 248));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        
        // Control Panel
        JPanel controlPanel = createControlPanel();

        // Statistics Panel
        statsPanel = createStatsPanel();

        // Server Log Panel
        JPanel logPanel = createLogPanel();

        // Layout Assembly
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setOpaque(false);
        leftPanel.add(controlPanel, BorderLayout.NORTH);
        leftPanel.add(logPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setOpaque(false);
        rightPanel.add(statsPanel, BorderLayout.CENTER);

        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(leftPanel, BorderLayout.CENTER);
        mainContainer.add(rightPanel, BorderLayout.EAST);

        add(mainContainer);

        initializeStatisticsTimer();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("Enterprise Server Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        return headerPanel;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        startButton = createCorporateButton("Start Server", new Color(33, 150, 243));
        stopButton = createCorporateButton("Stop Server", new Color(229, 57, 53));
        stopButton.setEnabled(false);

        statusLabel = new JLabel("◉ Server Status: Offline");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(new Color(158, 158, 158));

        controlPanel.add(statusLabel);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);

        return controlPanel;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new BorderLayout(10, 10));
        statsPanel.setPreferredSize(new Dimension(300, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Create pie chart for server statistics
        dataset = new DefaultPieDataset();
        dataset.setValue("Active", 0);
        dataset.setValue("Inactive", 100);

        chart = ChartFactory.createPieChart(
            "Server Load",
            dataset,
            true,
            true,
            false
        );

        // Customize chart appearance
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Active", new Color(33, 150, 243));
        plot.setSectionPaint("Inactive", new Color(189, 189, 189));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);

        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(280, 280));

        statsPanel.add(chartPanel, BorderLayout.CENTER);

        return statsPanel;
    }

    private JPanel createLogPanel() {
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(Color.WHITE);
        logPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(new Color(250, 250, 250));
        logArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(null);

        JLabel logLabel = new JLabel("Server Log");
        logLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        logPanel.add(logLabel, BorderLayout.NORTH);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        return logPanel;
    }

    private JButton createCorporateButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        if (text.equals("Start Server")) {
            button.addActionListener(e -> startServer());
        } else {
            button.addActionListener(e -> stopServer());
        }

        return button;
    }

    private void initializeStatisticsTimer() {
        statsTimer = new Timer(5000, e -> updateStatistics());
        statsTimer.start();
    }

    private void updateStatistics() {
        if (isServerRunning) {
            connectedUsers = (int) (Math.random() * 100);
            dataset.setValue("Active", connectedUsers);
            dataset.setValue("Inactive", 100 - connectedUsers);
        }
    }

    private void startServer() {
        try {
            String[] args = new String[]{};
            MainServer.main(args);
            isServerRunning = true;
            updateServerStatus();
            logMessage("Server started successfully");
        } catch (Exception e) {
            logMessage("Failed to start server: " + e.getMessage());
        }
    }

    private void stopServer() {
        try {
            MainServer.stopServer();
            isServerRunning = false;
            updateServerStatus();
            logMessage("Server stopped successfully");
        } catch (Exception e) {
            logMessage("Failed to stop server: " + e.getMessage());
        }
    }

    private void updateServerStatus() {
        if (isServerRunning) {
            statusLabel.setText("◉ Server Status: Online");
            statusLabel.setForeground(new Color(76, 175, 80));
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        } else {
            statusLabel.setText("◉ Server Status: Offline");
            statusLabel.setForeground(new Color(158, 158, 158));
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            dataset.setValue("Active", 0);
            dataset.setValue("Inactive", 100);
        }
    }

    private void logMessage(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logArea.append(String.format("[%s] %s%n", timestamp, message));
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            CorporateServerGUI gui = new CorporateServerGUI();
            gui.setVisible(true);
        });
    }
}