package model;

import enums.Status;

import java.io.*;
import java.util.ArrayList;

public class Game implements Serializable {
    private static final long serialVersionUID = 1L;
    private Player player1;
    private Player player2;
    private int currentTurn;
    private PlayerMap playerMap1;
    private PlayerMap playerMap2;
    private boolean isInPlayingPhase;
    private int currentShipIndex;

    public static final int MAP_WIDTH = 6;
    public static final int MAP_HEIGHT = 8;

    public Game(Player player1 , Player player2){
        this.player1 = player1;
        this.player2 = player2;
        this.isInPlayingPhase = false;
        this.currentTurn = 1;
        this.currentShipIndex = 0;

        this.playerMap1 = initializeMap(new PlayerMap());
        this.playerMap2 = initializeMap(new PlayerMap());

        setupShips();
    }

    public int getCurrentShipIndex() {
        return currentShipIndex;
    }

    public void setCurrentShipIndex(int currentShipIndex) {
        this.currentShipIndex = currentShipIndex;
    }

    public Player getPlayer1() {
        return this.player1;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public Player getCurrentPlayer() {
        return (currentTurn == 1) ? player1 : player2;
    }

    public void toggleTurn() {
        currentTurn = (currentTurn == 1) ? 2 : 1;
    }

    public PlayerMap getPlayerMap1() {
        return playerMap1;
    }

    public PlayerMap getPlayerMap2() {
        return playerMap2;
    }

    private PlayerMap initializeMap(PlayerMap map) {
        ArrayList<MapCell> initialCells = new ArrayList<MapCell>();

        for (int i = 0; i < MAP_HEIGHT; i++) {
            for (int j = 0; j < MAP_WIDTH; j++) {
                Position pos = new Position(i, j);
                MapCell newCell = new MapCell(pos, Status.W);
                initialCells.add(newCell);
            }
        }
        map.setMapCells(initialCells);
        return map;
    }

    public boolean placeShipForPlayer(Player player, int shipIndex, int startX, int startY, int endX, int endY) {
        PlayerMap map = (player == player1) ? playerMap1 : playerMap2;
        Ship ship = player.getShips().get(shipIndex);
        int size = ship.getSize();

        boolean isHorizontal = startX == endX;
        boolean isVertical = startY == endY;

        if (!isHorizontal && !isVertical) {
            return false; // Placement must be horizontal or vertical
        }

        int length = isHorizontal ? Math.abs(endY - startY) + 1 : Math.abs(endX - startX) + 1;
        if (length != size) {
            return false; // Length must match the ship's size
        }

        int minX = Math.min(startX, endX);
        int minY = Math.min(startY, endY);
        int maxX = Math.max(startX, endX);
        int maxY = Math.max(startY, endY);

        if (minX < 0 || maxX >= MAP_HEIGHT || minY < 0 || maxY >= MAP_WIDTH) {
            return false; // Out of bounds
        }

        for (int i = 0; i < size; i++) {
            int x = isHorizontal ? startX : minX + i;
            int y = isHorizontal ? minY + i : startY;
            MapCell cell = map.getCellAt(x, y);
            if (cell == null || cell.getStatus() != Status.W) {
                return false; // Cell is not water (already occupied or invalid)
            }
        }

        for (int i = 0; i < size; i++) {
            int x = isHorizontal ? startX : minX + i;
            int y = isHorizontal ? minY + i : startY;
            MapCell cell = map.getCellAt(x, y);
            cell.setStatus(Status.S);
            ship.addCell(cell);
        }

        return true; // Ship placement successful
    }

    private void setupShips() {
        createShipsForPlayer(player1);
        createShipsForPlayer(player2);
    }

    private void createShipsForPlayer(Player player) {
        ArrayList<Ship> ships = new ArrayList<>();
        ships.add(new Ship(4, "Ship 4"));
        ships.add(new Ship(3, "Ship 3-1"));
//        ships.add(new Ship(3, "Ship 3-2"));
//        ships.add(new Ship(2, "Ship 2-1"));
//        ships.add(new Ship(2, "Ship 2-2"));
//        ships.add(new Ship(2, "Ship 2-3"));
//        ships.add(new Ship(1, "Ship 1-1"));
//        ships.add(new Ship(1, "Ship 1-2"));
//        ships.add(new Ship(1, "Ship 1-3"));
//        ships.add(new Ship(1, "Ship 1-4"));

        player.setShips(ships);
    }

    public boolean isInPlayingPhase() {
        return isInPlayingPhase;
    }

    public void setInPlayingPhase(boolean inPlayingPhase) {
        isInPlayingPhase = inPlayingPhase;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public void setCurrentPlayer(Player player) {
        if (player == player1) {
            currentTurn = 1;
        } else {
            currentTurn = 2;
        }
    }

    public static void saveGame(Game game) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("gameSave.dat"))) {
            out.writeObject(game);
            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Game loadGame() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("gameSave.dat"))) {
            Game game = (Game) in.readObject();

            // Ensure that the current turn and current player are properly set.
            if (game.getCurrentTurn() == 1) {
                game.setCurrentPlayer(game.getPlayer1());
            } else {
                game.setCurrentPlayer(game.getPlayer2());
            }

            // Restore map cell statuses for both players
            restoreMapCellStatuses(game.getPlayerMap1());
            restoreMapCellStatuses(game.getPlayerMap2());

            return game;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null; // Return null if loading fails
        }
    }

    private static void restoreMapCellStatuses(PlayerMap playerMap) {
        // Ensure the map cells have the correct statuses after loading
        ArrayList<MapCell> mapCells = playerMap.getMapCells();
        for (MapCell cell : mapCells) {
            // Check the status and set it accordingly (S, M, H, W)
            // This assumes that the statuses of cells are stored during saving and are being correctly deserialized.
            Status cellStatus = cell.getStatus();
            // Any custom logic can be added here to reset or validate the status if needed
        }
    }
}
