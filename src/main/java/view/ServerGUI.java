package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ServerGUI extends JFrame {
    private JButton startButton;
    private JButton stopButton;
    private JLabel statusLabel;
    private JTextArea logArea;
    private boolean isServerRunning = false;
    private final Color ACCENT_COLOR = new Color(82, 82, 82);
    
    public ServerGUI() {
        setTitle("Server Control");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);

        // Clean white main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(25, 25));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Minimal header
        JLabel titleLabel = new JLabel("Server");
        titleLabel.setFont(new Font("Inter", Font.PLAIN, 28));
        titleLabel.setForeground(ACCENT_COLOR);
        
        // Status indicator
        statusLabel = new JLabel("inactive");
        statusLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        statusLabel.setForeground(Color.GRAY);

        // Control buttons
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        controlPanel.setBackground(Color.WHITE);
        
        startButton = createMinimalButton("Start");
        stopButton = createMinimalButton("Stop");
        stopButton.setEnabled(false);
        
        controlPanel.add(startButton);
        controlPanel.add(stopButton);

        // Header panel combining title and controls
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(statusLabel, BorderLayout.CENTER);
        headerPanel.add(controlPanel, BorderLayout.SOUTH);

        // Minimal log area
        logArea = new JTextArea();
        logArea.setFont(new Font("Inter", Font.PLAIN, 13));
        logArea.setBackground(new Color(250, 250, 250));
        logArea.setForeground(ACCENT_COLOR);
        logArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        logArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240)));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());
    }

    private JButton createMinimalButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Inter", Font.PLAIN, 14));
        button.setForeground(ACCENT_COLOR);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));
        button.setPreferredSize(new Dimension(80, 32));
        button.setFocusPainted(false);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(250, 250, 250));
                }
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });
        
        return button;
    }

    private void startServer() {
        try {
            MainServer.main(new String[]{});
            isServerRunning = true;
            updateServerStatus();
            logMessage("Server started");
        } catch (Exception e) {
            logMessage("Failed to start server");
        }
    }

    private void stopServer() {
        try {
            MainServer.stopServer();
            isServerRunning = false;
            updateServerStatus();
            logMessage("Server stopped");
        } catch (Exception e) {
            logMessage("Failed to stop server");
        }
    }

    private void updateServerStatus() {
        if (isServerRunning) {
            statusLabel.setText("active");
            statusLabel.setForeground(new Color(76, 175, 80));
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        } else {
            statusLabel.setText("inactive");
            statusLabel.setForeground(Color.GRAY);
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }

    private void logMessage(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        logArea.append(String.format("%s  %s%n", timestamp, message));
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            ServerGUI gui = new ServerGUI();
            gui.setVisible(true);
        });
    }
}
