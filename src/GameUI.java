import model.Game;
import javax.swing.*;
import java.awt.*;

class GameUI extends JFrame {
    private Game game;
    private JLabel scoreboardLabel;
    private JLabel turnLabel;

    public GameUI(Game game) {
        super("Game");

        this.game = game;

        JLabel playerNameLabel = new JLabel(game.getPlayer1().getName() + " " + game.getPlayer2().getName());

        setLayout(new BorderLayout());
        add(playerNameLabel, BorderLayout.NORTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
