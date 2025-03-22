import model.Game;
import model.Player;

import javax.swing.*;
import java.awt.*;

public class StartForm extends JFrame {
    public StartForm() {
        super("Start Form");

        setLayout(new BorderLayout());


        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel player1Label = new JLabel("Player 1:");
        JTextField player1Field = new JTextField(15);
        JLabel player2Label = new JLabel("Player 2:");
        JTextField player2Field = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(player1Label, gbc);

        gbc.gridx = 1;
        formPanel.add(player1Field, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(player2Label, gbc);

        gbc.gridx = 1;
        formPanel.add(player2Field, gbc);

        JButton startButton = new JButton("Start");
        JButton loadButton = new JButton("Load");

        Dimension buttonSize = new Dimension(100, 40);
        startButton.setPreferredSize(buttonSize);
        loadButton.setPreferredSize(buttonSize);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(startButton);
        buttonPanel.add(loadButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);


        startButton.addActionListener(e -> {
            String p1Name = player1Field.getText();
            String p2Name = player2Field.getText();

            Player p1 = new Player(p1Name);
            Player p2 = new Player(p2Name);

            Game game = new Game(p1, p2);

            new GameUI(game);
            dispose();

            JOptionPane.showMessageDialog(this,
                    "Have fun!");
        });
    }

}
