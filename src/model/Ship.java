package model;

import enums.Status;

import java.io.Serializable;
import java.util.ArrayList;

public class Ship implements Serializable {
    private int size;
    private String name;
    private ArrayList<MapCell> cells;

    public Ship(int size, String name) {
        this.size = size;
        this.name = name;
        this.cells = new ArrayList<>();
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public ArrayList<MapCell> getCells() {
        return cells;
    }

    public void addCell(MapCell cell) {
        cells.add(cell);
    }

    public boolean isSunk() {
        for (MapCell cell : cells) {
            if (cell.getStatus() != Status.H) {
                return false;
            }
        }
        return true;
    }
}