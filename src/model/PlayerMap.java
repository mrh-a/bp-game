package model;

import java.util.ArrayList;

public class PlayerMap {
    private ArrayList<MapCell> mapCells;

    void PlayerMap(ArrayList<MapCell> cell ){
        this.mapCells = cell;
    }

    public ArrayList<MapCell> getMapCells() {
        return mapCells;
    }

    public void setMapCells(ArrayList<MapCell> mapCells) {
        this.mapCells = mapCells;
    }
}
