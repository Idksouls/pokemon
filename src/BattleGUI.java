import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BattleGUI extends JFrame {
    private int spaceBarCount = 0;
    private boolean isRunning = false;
    private JLabel label;
    private JLabel timerLabel;
    private double multiplier = 1.0; // Default multiplier

    public BattleGUI() {
        setTitle("Battle GUI");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        label = new JLabel("Press Space Bar!", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.PLAIN, 24));
        add(label, BorderLayout.CENTER);

        timerLabel = new JLabel("Time left: 3 seconds", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        add(timerLabel, BorderLayout.NORTH);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isRunning && e.getKeyCode() == KeyEvent.VK_SPACE) {
                    spaceBarCount++;
                    label.setText("Count: " + spaceBarCount);
                }
            }
        });

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        // Timer to start the spam counting after 3 seconds
        Timer countdownTimer = new Timer(1000, new ActionListener() {
            private int timeLeft = 3;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeLeft > 0) {
                    timerLabel.setText("Time left: " + timeLeft + " seconds");
                    timeLeft--;
                } else {
                    timerLabel.setText("Start spamming now!");
                    isRunning = true;
                    ((Timer) e.getSource()).stop();

                    // Start the spam counting timer
                    new Timer(3000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            isRunning = false;
                            ((Timer) e.getSource()).stop();
                            multiplier = calculateMultiplier();
                            JOptionPane.showMessageDialog(null, "Time's up! Multiplier: " + multiplier);
                            synchronized (BattleGUI.this) {
                                BattleGUI.this.notify();
                            }
                            dispose(); // Close the window
                        }
                    }).start();
                }
            }
        });
        countdownTimer.start();
    }

    private double calculateMultiplier() {
        if (spaceBarCount >= 25) {
            return 1.25;
        } else {
            return 1.0 + Math.min(spaceBarCount / 100.0, 0.25);
        }
    }

    public double getMultiplier() {
        return multiplier;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                BattleGUI gui = new BattleGUI();
                gui.setVisible(true);
                gui.toFront();
                gui.requestFocus();
                gui.setAlwaysOnTop(true);
            }
        });
    }
}
