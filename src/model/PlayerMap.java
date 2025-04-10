package model;

import enums.Status;

import java.util.ArrayList;

public class PlayerMap {
    private ArrayList<MapCell> mapCells;

    public PlayerMap() {

    }

    public ArrayList<MapCell> getMapCells() {
        return mapCells;
    }

    public void setMapCells(ArrayList<MapCell> mapCells) {
        this.mapCells = mapCells;
    }

    public MapCell getCellAt(int x, int y) {
        if (x < 0 || x >= Game.MAP_HEIGHT || y < 0 || y >= Game.MAP_WIDTH) {
            return null;
        }
        int index = x * Game.MAP_WIDTH + y;
        return mapCells.get(index);
    }

    public boolean placeShip(Ship ship, int startX, int startY, int endX, int endY) {
        int size = ship.getSize();
        boolean isHorizontal = startX == endX;
        boolean isVertical = startY == endY;

        // Validate direction and size
        if (!isHorizontal && !isVertical) {
            return false; // Must be horizontal or vertical
        }
        int length = isHorizontal ? Math.abs(endY - startY) + 1 : Math.abs(endX - startX) + 1;
        if (length != size) {
            return false; // Distance between start and end must match ship size
        }

        // Determine direction and bounds
        int minX = Math.min(startX, endX);
        int minY = Math.min(startY, endY);
        int maxX = Math.max(startX, endX);
        int maxY = Math.max(startY, endY);

        // Check bounds
        if (minX < 0 || maxX >= Game.MAP_HEIGHT || minY < 0 || maxY >= Game.MAP_WIDTH) {
            return false; // Out of bounds
        }

        // Check for overlap
        for (int i = 0; i < size; i++) {
            int x = isHorizontal ? startX : minX + i;
            int y = isHorizontal ? minY + i : startY;
            MapCell cell = getCellAt(x, y);
            if (cell == null || cell.getStatus() != Status.W) {
                return false; // Cell is not water or invalid
            }
        }

        // Place the ship
        for (int i = 0; i < size; i++) {
            int x = isHorizontal ? startX : minX + i;
            int y = isHorizontal ? minY + i : startY;
            MapCell cell = getCellAt(x, y);
            cell.setStatus(Status.S);
            System.out.println(cell.getPosition().getX() + " " + cell.getPosition().getY());
            ship.addCell(cell);
        }
        return true;
    }
}