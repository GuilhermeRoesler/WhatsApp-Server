package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MinimalistServerGUI extends JFrame {
    private JButton startButton;
    private JButton stopButton;
    private JLabel statusLabel;
    private JTextArea logArea;
    private boolean isServerRunning = false;
    
    public MinimalistServerGUI() {
        // Configure window
        setTitle("WhatsApp Server Control Panel");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(25, 150, 100),
                        0, getHeight(), new Color(25, 100, 150));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        
        // Create control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setOpaque(false);
        
        // Create buttons with modern look
        startButton = createStyledButton("Start Server", new Color(46, 204, 113));
        stopButton = createStyledButton("Stop Server", new Color(231, 76, 60));
        stopButton.setEnabled(false);
        
        // Create status label
        statusLabel = new JLabel("Server Status: Stopped");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.WHITE);
        
        // Create log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Server Log"));
        
        // Add components
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(statusLabel);
        
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Add button listeners
        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 40));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void startServer() {
        try {
            String[] args = new String[]{};
            MainServer.main(args);
            isServerRunning = true;
            updateServerStatus();
            logArea.append("[" + java.time.LocalDateTime.now() + "] Server started successfully\n");
        } catch (Exception e) {
            logArea.append("[" + java.time.LocalDateTime.now() + "] Failed to start server: " + e.getMessage() + "\n");
        }
    }
    
    private void stopServer() {
        try {
            // Add server shutdown logic here
            MainServer.stopServer();
            isServerRunning = false;
            updateServerStatus();
            logArea.append("[" + java.time.LocalDateTime.now() + "] Server stopped successfully\n");
        } catch (Exception e) {
            logArea.append("[" + java.time.LocalDateTime.now() + "] Failed to stop server: " + e.getMessage() + "\n");
        }
    }
    
    private void updateServerStatus() {
        if (isServerRunning) {
            statusLabel.setText("Server Status: Running");
            statusLabel.setForeground(new Color(46, 204, 113));
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        } else {
            statusLabel.setText("Server Status: Stopped");
            statusLabel.setForeground(Color.WHITE);
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            MinimalistServerGUI gui = new MinimalistServerGUI();
            gui.setVisible(true);
        });
    }
}