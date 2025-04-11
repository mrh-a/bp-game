import model.*;
import enums.Status;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static enums.Status.*;

public class GameUI extends JFrame implements Serializable {
    private Game game;
    private JLabel instructionLabel;
    private JLabel turnLabel;
    private Map<Position, MapCellButton> player1Buttons = new HashMap<>();
    private Map<Position, MapCellButton> player2Buttons = new HashMap<>();

    // These fields track the ship-placement progress.
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

        // When loading the game, resume from ship placement progress if not yet in playing phase.
        if (game.isInPlayingPhase()) {
            startGamePhase();
        } else {
            // If the game is in ship placement phase, resume where it left off
            currentPlacingPlayer = game.getCurrentPlayer();
            currentShipIndex = game.getCurrentShipIndex();
            setupShipPlacement();
        }

        updateButtonColors();

        // Save game state on window close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Game.saveGame(game); // Save the game state when the window is closing
            }
        });
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

    // Resume the ship placement phase from saved state.
    private void setupShipPlacement() {
        currentPlacingPlayer = game.getCurrentPlayer();
        currentShipIndex = game.getCurrentShipIndex();
        startPosition = null;
        endPosition = null;
        displayInstruction();
    }

    // When the game is already in the playing phase, adjust the UI accordingly.
    private void startGamePhase() {
        instructionLabel.setText("Game is now in progress!");
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

    // Custom button class to store position and map reference.
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

            if (game.isInPlayingPhase()) {
                handleGamePhase(currentPlacingPlayer, clickedMap, clickedStatus, button);
            } else if (clickedMap == getCurrentPlayerMap()) {
                handleShipPlacement(button, clickedPos);
            }

            if (game.isInPlayingPhase()) {
                checkForWinner();
            }
        }

        private void handleGamePhase(Player currentPlayer, PlayerMap clickedMap, String clickedStatus, MapCellButton button) {
            // Check if the clicked map is the other player's map
            if (clickedMap == getOtherPlayerMap()) {
                Status status = Status.valueOf(clickedStatus);

                // Perform actions based on the current cell's status
                switch (status) {
                    case S:  // Ship cell
                        button.setText(H.toString()); // Mark it as hit
                        button.setBackground(Color.RED); // Optionally change background color for better visual feedback
                        updateCellStatus(clickedMap, button.getPosition(), Status.H);  // Update the actual map cell status
                        break;

                    case H: // Already hit
                    case M: // Already missed
                        JOptionPane.showMessageDialog(GameUI.this, "You already selected this cell!");
                        break;

                    case W: // Water cell
                        button.setText(M.toString()); // Mark it as a miss
                        button.setBackground(Color.BLUE); // Optionally change background color for better visual feedback
                        updateCellStatus(clickedMap, button.getPosition(), Status.M);  // Update the actual map cell status
                        switchCurrentPlayer(); // Switch to the next player
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
            game.toggleTurn();
            currentPlacingPlayer = game.getCurrentPlayer();
            turnLabel.setText("Current Turn: " + currentPlacingPlayer.getName());
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
                    shipButton.setText("S"); // Ship cells are marked with "S"
                    shipButton.setBackground(Color.GRAY); // Optionally change background color for better visual feedback
                    updateCellStatus(getCurrentPlayerMap(), pos, Status.S); // Update the map cell status to S (Ship)
                }
            }
        }

        private void updateCellStatus(PlayerMap playerMap, Position position, Status newStatus) {
            // Retrieve the map cell at the given position
            MapCell cell = playerMap.getCellAt(position.getX(), position.getY());

            if (cell != null) {
                // Update the cell's status with the new status (H or M)
                cell.setStatus(newStatus);
            }
        }

        private void moveToNextShipOrPhase() {
            currentShipIndex++;
            if (currentShipIndex >= currentPlacingPlayer.getShips().size()) {
                if (currentPlacingPlayer == game.getPlayer1()) {
                    switchCurrentPlayer();
                    currentShipIndex = 0;
                    game.setCurrentShipIndex(0); // Update saved state
                } else {
                    game.setInPlayingPhase(true); // Switch to playing phase
                    switchCurrentPlayer();
                }
            } else {
                game.setCurrentShipIndex(currentShipIndex); // Update saved state
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

            for (MapCellButton button : player1Buttons.values()) {
                if (button.getText().equals(S.toString())) {
                    player1Lost = false;
                    break;
                }
            }

            for (MapCellButton button : player2Buttons.values()) {
                if (button.getText().equals(S.toString())) {
                    player2Lost = false;
                    break;
                }
            }

            if (player1Lost && !player2Lost) {
                JOptionPane.showMessageDialog(GameUI.this, game.getPlayer2().getName() + " wins! All of " +
                        game.getPlayer1().getName() + "'s ships are sunk.");
                disableAllButtons(); // End the game
            } else if (player2Lost && !player1Lost) {
                JOptionPane.showMessageDialog(GameUI.this, game.getPlayer1().getName() + " wins! All of " +
                        game.getPlayer2().getName() + "'s ships are sunk.");
                disableAllButtons(); // End the game
            }
        }

        private void disableAllButtons() {
            for (MapCellButton button : player1Buttons.values()) {
                button.setEnabled(false);
            }
            for (MapCellButton button : player2Buttons.values()) {
                button.setEnabled(false);
            }
        }

    }

    private void updateButtonColors() {
        // Iterate through each button for both players and update its background color
        for (Map.Entry<Position, MapCellButton> entry : player1Buttons.entrySet()) {
            MapCellButton button = entry.getValue();
            Position position = button.getPosition();
            MapCell cell = game.getPlayerMap1().getCellAt(position.getX(), position.getY());
            updateButtonColor(button, cell.getStatus());
        }

        for (Map.Entry<Position, MapCellButton> entry : player2Buttons.entrySet()) {
            MapCellButton button = entry.getValue();
            Position position = button.getPosition();
            MapCell cell = game.getPlayerMap2().getCellAt(position.getX(), position.getY());
            updateButtonColor(button, cell.getStatus());
        }
    }

    private void updateButtonColor(MapCellButton button, Status status) {
        // Update the button background color based on the status of the MapCell
        switch (status) {
            case S:  // Ship
                button.setBackground(Color.GRAY);  // Set background for ship cells (optional)
                break;
            case H:  // Hit
                button.setBackground(Color.RED);   // Set background for hit cells
                break;
            case M:  // Miss
                button.setBackground(Color.BLUE);  // Set background for miss cells
                break;
            case W:  // Water
                button.setBackground(null);        // Set background for water cells (no color)
                break;
        }
    }
}