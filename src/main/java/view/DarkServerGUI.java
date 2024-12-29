package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

import utils.Constants;

public class DarkServerGUI extends JFrame {
    private JButton startButton;
    private JButton stopButton;
    private JLabel statusLabel;
    private JTextArea logArea;
    private boolean isServerRunning = false;

    public DarkServerGUI() {
        setTitle("WhatsApp Server Control");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(new Color(18, 18, 18));

        // Main panel with dark theme
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create dark gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(23, 23, 23),
                    0, getHeight(), new Color(32, 32, 32)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Add subtle grid pattern
                g2d.setColor(new Color(45, 45, 45, 50));
                int spacing = 20;
                for (int i = 0; i < getWidth(); i += spacing) {
                    g2d.drawLine(i, 0, i, getHeight());
                }
                for (int i = 0; i < getHeight(); i += spacing) {
                    g2d.drawLine(0, i, getWidth(), i);
                }
            }
        };
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Header with neon effect
        JPanel headerPanel = createGlassPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        JLabel titleLabel = new JLabel("SERVER CONTROL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 255, 255));
        headerPanel.add(titleLabel);

        // Control panel with glass effect
        JPanel controlPanel = createGlassPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        // Create neon-styled buttons
        startButton = createNeonButton("START", new Color(0, 255, 170));
        stopButton = createNeonButton("STOP", new Color(255, 50, 50));
        stopButton.setEnabled(false);

        // Status label with neon glow
        statusLabel = new JLabel("● OFFLINE");
        statusLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        statusLabel.setForeground(new Color(255, 50, 50));

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(statusLabel);

        // Log area with custom styling
        logArea = new JTextArea();
        logArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        logArea.setBackground(new Color(28, 28, 28));
        logArea.setForeground(new Color(200, 200, 200));
        logArea.setCaretColor(new Color(0, 255, 170));
        logArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 40)));
        scrollPane.getViewport().setBackground(new Color(28, 28, 28));

        // Layout assembly
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        // Add button listeners
        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());
    }

    private JPanel createGlassPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Glass effect
                g2d.setColor(new Color(255, 255, 255, 15));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Glass border
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private JButton createNeonButton(String text, Color neonColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Button background
                if (getModel().isPressed()) {
                    g2d.setColor(neonColor.darker().darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(neonColor.darker());
                } else {
                    g2d.setColor(new Color(40, 40, 40));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Neon border
                g2d.setColor(neonColor);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 10, 10);
                
                // Text
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.setColor(neonColor);
                g2d.drawString(text, 
                    (getWidth() - fm.stringWidth(text)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        
        button.setPreferredSize(new Dimension(120, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private void startServer() {
        try {
            String[] args = new String[]{};
            MainServer.main(args);
            MainServer.setServerGUI(this);
            isServerRunning = true;
            updateServerStatus();
            logMessage("Server initialized and running at port 8080");
        } catch (Exception e) {
            logMessage("Error starting server: " + e.getMessage());
        }
    }

    private void stopServer() {
        try {
            MainServer.stopServer();
            isServerRunning = false;
            updateServerStatus();
            logMessage("Server shutdown complete");
        } catch (Exception e) {
            logMessage("Error stopping server: " + e.getMessage());
        }
    }

    private void updateServerStatus() {
        if (isServerRunning) {
            statusLabel.setText("● ONLINE");
            statusLabel.setForeground(new Color(0, 255, 170));
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        } else {
            statusLabel.setText("● OFFLINE");
            statusLabel.setForeground(new Color(255, 50, 50));
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }

    public void logMessage(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
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
            DarkServerGUI gui = new DarkServerGUI();
            gui.setVisible(true);
            gui.setIconImage(Constants.ICON);
        });
    }
}
