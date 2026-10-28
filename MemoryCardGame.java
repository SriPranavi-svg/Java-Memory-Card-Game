import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MemoryCardGame extends JFrame implements ActionListener {

    private JButton[] cards = new JButton[12];

    private String[] symbols = {
        "🐱", "🐱", "🐶", "🐶",
        "🍎", "🍎", "⭐", "⭐",
        "🌙", "🌙", "🌻", "🌻"
    };

    private boolean[] matched = new boolean[12];

    private JButton firstCard = null;
    private JButton secondCard = null;

    private int firstIndex = -1;
    private int secondIndex = -1;
    private int moves = 0;
    private int matches = 0;
    private int seconds = 0;

    private JLabel movesLabel;
    private JLabel timerLabel;
    private JLabel statusLabel;
    private JButton restartButton;

    private javax.swing.Timer timer;

    public MemoryCardGame() {
        setTitle("Java Memory Card Game");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 245, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
            15, 15, 15, 15
        ));

        // Heading
        JLabel title = new JLabel(
            "MEMORY CARD GAME", JLabel.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(new Color(40, 60, 120));
        mainPanel.add(title, BorderLayout.NORTH);

        // Center card grid
        JPanel cardPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        cardPanel.setBackground(new Color(240, 245, 255));

        for (int i = 0; i < cards.length; i++) {
            cards[i] = new JButton("?");
            cards[i].setFont(new Font("SansSerif", Font.BOLD, 28));
            cards[i].setBackground(new Color(80, 110, 180));
            cards[i].setForeground(Color.WHITE);
            cards[i].setFocusPainted(false);
            cards[i].addActionListener(this);
            cards[i].setActionCommand(String.valueOf(i));

            cardPanel.add(cards[i]);
        }

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        bottomPanel.setBackground(new Color(240, 245, 255));

        movesLabel = new JLabel("Moves: 0", JLabel.CENTER);
        movesLabel.setFont(new Font("Arial", Font.BOLD, 16));

        timerLabel = new JLabel("Time: 0 seconds", JLabel.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));

        statusLabel = new JLabel(
            "Find all matching pairs!", JLabel.CENTER
        );
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        restartButton = new JButton("Restart Game");
        restartButton.addActionListener(e -> restartGame());

        bottomPanel.add(movesLabel);
        bottomPanel.add(timerLabel);
        bottomPanel.add(statusLabel);

        JPanel southPanel = new JPanel(new BorderLayout(5, 5));
        southPanel.setBackground(new Color(240, 245, 255));
        southPanel.add(bottomPanel, BorderLayout.CENTER);
        southPanel.add(restartButton, BorderLayout.SOUTH);

        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Shuffle cards before starting
        shuffleCards();

        // Start timer
        timer = new javax.swing.Timer(1000, e -> {
            seconds++;
            timerLabel.setText("Time: " + seconds + " seconds");
        });
        timer.start();

        setVisible(true);
    }

    // Shuffle the card symbols
    private void shuffleCards() {
        Collections.shuffle(Arrays.asList(symbols));
    }

    // Handle card clicks
    @Override
    public void actionPerformed(ActionEvent e) {
        int index = Integer.parseInt(e.getActionCommand());

        // Ignore already matched or currently open cards
        if (matched[index]
                || index == firstIndex
                || secondCard != null) {
            return;
        }

        cards[index].setText(symbols[index]);
        cards[index].setBackground(new Color(255, 220, 120));

        if (firstCard == null) {
            firstCard = cards[index];
            firstIndex = index;
            statusLabel.setText("Choose the second card.");
        } else {
            secondCard = cards[index];
            secondIndex = index;
            moves++;

            movesLabel.setText("Moves: " + moves);

            // Check if cards match
            if (symbols[firstIndex].equals(symbols[secondIndex])) {
                matched[firstIndex] = true;
                matched[secondIndex] = true;

                firstCard.setBackground(new Color(100, 190, 120));
                secondCard.setBackground(new Color(100, 190, 120));

                matches++;
                statusLabel.setText(
                    "Correct match! Pairs: " + matches + "/6"
                );

                firstCard = null;
                secondCard = null;
                firstIndex = -1;
                secondIndex = -1;

                // Check if all pairs are found
                if (matches == 6) {
                    timer.stop();

                    JOptionPane.showMessageDialog(
                        this,
                        "Congratulations!\n"
                        + "You found all pairs.\n"
                        + "Moves: " + moves + "\n"
                        + "Time: " + seconds + " seconds",
                        "You Win!",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    statusLabel.setText("You completed the game!");
                }
            } else {
                statusLabel.setText("Not a match. Try again.");

                // Hide unmatched cards after a short delay
                javax.swing.Timer delay = new javax.swing.Timer(800, event -> {
                    firstCard.setText("?");
                    secondCard.setText("?");

                    firstCard.setBackground(
                        new Color(80, 110, 180)
                    );
                    secondCard.setBackground(
                        new Color(80, 110, 180)
                    );

                    firstCard = null;
                    secondCard = null;
                    firstIndex = -1;
                    secondIndex = -1;
                });

                delay.setRepeats(false);
                delay.start();
            }
        }
    }

    // Restart the game
    private void restartGame() {
        if (timer != null) {
            timer.stop();
        }

        moves = 0;
        matches = 0;
        seconds = 0;

        firstCard = null;
        secondCard = null;
        firstIndex = -1;
        secondIndex = -1;

        Arrays.fill(matched, false);

        // Restore original pairs
        symbols = new String[] {
            "🐱", "🐱", "🐶", "🐶",
            "🍎", "🍎", "⭐", "⭐",
            "🌙", "🌙", "🌻", "🌻"
        };

        shuffleCards();

        for (JButton card : cards) {
            card.setText("?");
            card.setBackground(new Color(80, 110, 180));
            card.setEnabled(true);
        }

        movesLabel.setText("Moves: 0");
        timerLabel.setText("Time: 0 seconds");
        statusLabel.setText("Find all matching pairs!");

        timer = new javax.swing.Timer(1000, e -> {
            seconds++;
            timerLabel.setText("Time: " + seconds + " seconds");
        });

        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MemoryCardGame());
    }
}
