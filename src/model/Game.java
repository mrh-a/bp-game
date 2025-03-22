package model;

import enums.Status;

import java.util.ArrayList;

public class Game {
    private Player player1;
    private Player player2;
    private int currentTurn;
    private PlayerMap playerMap1;
    private PlayerMap playerMap2;

    static final int MAP_WIDTH = 6;
    static final int MAP_HEIGHT = 8;

    public Game(Player player1 , Player player2){
        this.player1 = player1;
        this.player2 = player2;
        this.currentTurn = 1;


        this.playerMap1 = initializeMap(new PlayerMap());
        this.playerMap2 = initializeMap(new PlayerMap());
    }

    public Player getPlayer1() {
        return this.player1;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public void switchTurn() {
        currentTurn = (currentTurn == 1) ? 2 : 1;
    }

    public Player getCurrentPlayer() {
        return (currentTurn == 1) ? player1 : player2;
    }

    private PlayerMap initializeMap(PlayerMap map) {

        ArrayList<MapCell> initialCells = new  ArrayList<MapCell>();

        for (int i = 0; i < MAP_HEIGHT; i++) {
            for (int j = 0; j < MAP_WIDTH; j++) {
                Position pos = new Position(i,j);
                MapCell newCell = new MapCell(pos, Status.W);
                initialCells.add(newCell);
            }
        }
        map.setMapCells(initialCells);
        return  map;
    }
}

