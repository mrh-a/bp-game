package model;

import enums.Status;

public class MapCell {
    private Position position;
    private Status status;

    public MapCell(Position pos, Status s){
        this.position = pos;
        this.status = s;
    }

}
