package model;

public class Player {
    private String name;
    private boolean isPlayerTurn;

    public Player (String name) {
        this.name = name;
        this.isPlayerTurn = false;
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
}
