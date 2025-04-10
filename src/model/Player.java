package model;

import java.util.ArrayList;

public class Player {
    private String name;
    private boolean isPlayerTurn;
    private ArrayList<Ship> ships;

    public Player(String name) {
        this.name = name;
        this.isPlayerTurn = false;
        this.ships = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }

    public void setPlayerTurn(boolean playerTurn) {
        isPlayerTurn = playerTurn;
    }

    public ArrayList<Ship> getShips() {
        return ships;
    }

    public void setShips(ArrayList<Ship> ships) {
        this.ships = ships;
    }
}