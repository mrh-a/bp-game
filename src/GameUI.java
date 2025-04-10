import model.*;
import enums.Status;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import static enums.Status.*;

public class GameUI extends JFrame {
    private Game game;
    private JLabel instructionLabel;
    private JLabel turnLabel;
    private Map<Position, MapCellButton> player1Buttons = new HashMap<>();
    private Map<Position, MapCellButton> player2Buttons = new HashMap<>();
    private Player currentPlacingPlayer;
    private int currentShipIndex;
    private Position startPosition;
    private Position endPosition;

    public GameUI(Game game) {
        super("Game");
        this.game = game;

        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        JLabel playerNameLabel = new JLabel(game.getPlayer1().getName() + " vs " + game.getPlayer2().getName());
        playerNameLabel.setHorizontalAlignment(JLabel.CENTER);
        topPanel.add(playerNameLabel);

        instructionLabel = new JLabel();
        instructionLabel.setHorizontalAlignment(JLabel.CENTER);
        topPanel.add(instructionLabel);

        turnLabel = new JLabel("Current Turn: " + game.getCurrentPlayer().getName());
        turnLabel.setHorizontalAlignment(JLabel.CENTER);
        topPanel.add(turnLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel mapsPanel = new JPanel(new GridLayout(1, 2, 80, 0));

        JPanel player1MapPanel = new JPanel(new BorderLayout());
        player1MapPanel.add(new JLabel(game.getPlayer1().getName() + "'s Map", SwingConstants.CENTER), BorderLayout.NORTH);
        JPanel mapPanel1 = createMapPanel(game.getPlayerMap1(), player1Buttons);
        player1MapPanel.add(mapPanel1, BorderLayout.CENTER);

        JPanel player2MapPanel = new JPanel(new BorderLayout());
        player2MapPanel.add(new JLabel(game.getPlayer2().getName() + "'s Map", SwingConstants.CENTER), BorderLayout.NORTH);
        JPanel mapPanel2 = createMapPanel(game.getPlayerMap2(), player2Buttons);
        player2MapPanel.add(mapPanel2, BorderLayout.CENTER);

        mapsPanel.add(player1MapPanel);
        mapsPanel.add(player2MapPanel);
        add(mapsPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setVisible(true);

        // Start the ship placement phase
        setupShipPlacement();
    }

    private JPanel createMapPanel(PlayerMap playerMap, Map<Position, MapCellButton> buttonMap) {
        int buttonSize = 50;
        int gap = 2;

        JPanel mapPanel = new JPanel(new GridLayout(Game.MAP_HEIGHT, Game.MAP_WIDTH, gap, gap));
        for (int i = 0; i < Game.MAP_HEIGHT; i++) {
            for (int j = 0; j < Game.MAP_WIDTH; j++) {
                Position pos = new Position(i, j);
                MapCellButton button = new MapCellButton(pos, playerMap);
                buttonMap.put(pos, button);
                mapPanel.add(button);
                button.addActionListener(new MapCellButtonListener());
            }
        }

        int mapWidth = Game.MAP_WIDTH * buttonSize + (Game.MAP_WIDTH - 1) * gap;
        int mapHeight = Game.MAP_HEIGHT * buttonSize + (Game.MAP_HEIGHT - 1) * gap;
        mapPanel.setPreferredSize(new Dimension(mapWidth, mapHeight));

        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapperPanel.add(mapPanel);

        return wrapperPanel;
    }

    private void setupShipPlacement() {
        currentPlacingPlayer = game.getPlayer1();
        currentShipIndex = 0;
        startPosition = null;
        endPosition = null;
        displayInstruction();
    }

    private void displayInstruction() {
        if (currentPlacingPlayer != null && currentShipIndex < currentPlacingPlayer.getShips().size()) {
            String shipName = currentPlacingPlayer.getShips().get(currentShipIndex).getName();
            int shipSize = currentPlacingPlayer.getShips().get(currentShipIndex).getSize();
            instructionLabel.setText(currentPlacingPlayer.getName() + ", place your " + shipName + " (size " + shipSize + ")");
        } else {
            instructionLabel.setText("All ships placed. Game starts with " + game.getCurrentPlayer().getName() + "'s turn.");
        }
    }

    // Custom button class to store position and map reference
    class MapCellButton extends JButton {
        private Position position;
        private PlayerMap map;

        public MapCellButton(Position pos, PlayerMap map) {
            this.position = pos;
            this.map = map;
            setText(map.getCellAt(pos.getX(), pos.getY()).getStatus().toString());
        }

        public Position getPosition() {
            return position;
        }

        public PlayerMap getMap() {
            return map;
        }
    }

    class MapCellButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            MapCellButton button = (MapCellButton) e.getSource();
            Position clickedPos = button.getPosition();
            PlayerMap clickedMap = button.getMap();
            String clickedStatus = button.getText();

            if(game.isInPlayingPhase()){
                handleGamePhase(currentPlacingPlayer, clickedMap,clickedStatus, button);
            }
            // Ensure the click is on the correct player's map
            else if (clickedMap == getCurrentPlayerMap()) {
                handleShipPlacement(button, clickedPos);
            }

            if (game.isInPlayingPhase()) {
                checkForWinner();
            }

        }

        private void handleGamePhase(Player currentPlayer, PlayerMap clickedMap, String clickedStatus, MapCellButton button){
            System.out.println("--current palyer--" +  currentPlayer.getName());
            if(clickedMap == getOtherPlayerMap()){
                Status status;
                status = Status.valueOf(clickedStatus);
                switch (status){
                    case S:
                        button.setText(H.toString());
                        break;
                    case H:
                        JOptionPane.showMessageDialog(GameUI.this, "You already selected this cell!");
                        break;
                    case M:
                        JOptionPane.showMessageDialog(GameUI.this, "You already selected this cell!");
                        break;
                    case W:
                        button.setText(M.toString());
                        switchCurrentPlayer();
                        break;
                }
            }
        }

        private PlayerMap getCurrentPlayerMap() {
            return (currentPlacingPlayer == game.getPlayer1()) ? game.getPlayerMap1() : game.getPlayerMap2();
        }

        private PlayerMap getOtherPlayerMap() {
            return (currentPlacingPlayer == game.getPlayer1()) ? game.getPlayerMap2() : game.getPlayerMap1();
        }

        private void switchCurrentPlayer() {
            if(currentPlacingPlayer == game.getPlayer1()) {
                currentPlacingPlayer = game.getPlayer2();
                turnLabel.setText("Current Turn: " + currentPlacingPlayer.getName());
            }else {
                currentPlacingPlayer = game.getPlayer1();
                turnLabel.setText("Current Turn: " + currentPlacingPlayer.getName());
            }
        }

        private void handleShipPlacement(MapCellButton button, Position clickedPos) {
            if (startPosition == null) {
                startPosition = clickedPos;
                button.setBackground(Color.YELLOW); // Highlight the starting cell
            } else if (endPosition == null) {
                endPosition = clickedPos;
                placeShip();
            }
        }

        private void placeShip() {
            boolean success = game.placeShipForPlayer(currentPlacingPlayer, currentShipIndex,
                    startPosition.getX(), startPosition.getY(),
                    endPosition.getX(), endPosition.getY());

            if (success) {
                updateShipUI();
                moveToNextShipOrPhase();
            } else {
                JOptionPane.showMessageDialog(GameUI.this, "Invalid placement. Try again.");
                resetPlacementUI();
            }
        }

        private void updateShipUI() {
            java.util.List<MapCell> shipCells = currentPlacingPlayer.getShips().get(currentShipIndex).getCells();
            Map<Position, MapCellButton> buttonMap = getCurrentButtonMap();

            for (MapCell cell : shipCells) {
                Position pos = cell.getPosition();
                MapCellButton shipButton = buttonMap.get(pos);
                if (shipButton != null) {
                    shipButton.setText("S");
                    shipButton.setBackground(null);
                }
            }
        }

        private void moveToNextShipOrPhase() {
            currentShipIndex++;
            if (currentShipIndex >= currentPlacingPlayer.getShips().size()) {
                if (currentPlacingPlayer == game.getPlayer1()) {
                    switchCurrentPlayer();
                    currentShipIndex = 0;
                } else {
                    game.setInPlayingPhase(true);
                    switchCurrentPlayer();
                }
            }
            displayInstruction();
            startPosition = null;
            endPosition = null;
        }

        private void resetPlacementUI() {
            getCurrentButtonMap().get(startPosition).setBackground(null);
            startPosition = null;
            endPosition = null;
        }

        private Map<Position, MapCellButton> getCurrentButtonMap() {
            return (currentPlacingPlayer == game.getPlayer1()) ? player1Buttons : player2Buttons;
        }


        // New method to check for a winner
        private void checkForWinner() {
            boolean player1Lost = true;
            boolean player2Lost = true;

            // Check if all of Player 1's ships are hit
            for (MapCellButton button : player1Buttons.values()) {
                if (button.getText().equals(S.toString())) {
                    player1Lost = false; // Found an unhit ship cell
                    break;
                }
            }

            // Check if all of Player 2's ships are hit
            for (MapCellButton button : player2Buttons.values()) {
                if (button.getText().equals(S.toString())) {
                    player2Lost = false; // Found an unhit ship cell
                    break;
                }
            }

            // Determine the winner
            if (player1Lost && !player2Lost) {
                JOptionPane.showMessageDialog(GameUI.this, game.getPlayer2().getName() + " winner," + " All of " +
                        game.getPlayer1().getName() + " ships sunk.");
                disableAllButtons(); // End the game
            } else if (player2Lost && !player1Lost) {
                JOptionPane.showMessageDialog(GameUI.this, game.getPlayer1().getName() + " winner," + " All of " +
                        game.getPlayer2().getName() + " ships sunk.");
                disableAllButtons(); // End the game
            }
            // If both are true or both are false, the game continues
        }

        // Helper method to disable all buttons when the game ends
        private void disableAllButtons() {
            for (MapCellButton button : player1Buttons.values()) {
                button.setEnabled(false);
            }
            for (MapCellButton button : player2Buttons.values()) {
                button.setEnabled(false);
            }
        }

    }

}