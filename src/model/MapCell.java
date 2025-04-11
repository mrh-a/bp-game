package model;

import enums.Status;

import java.io.Serializable;

public class MapCell implements Serializable {
    private static final long serialVersionUID = 1L;

    private Position position;
    private Status status;

    public MapCell(Position pos, Status s) {
        this.position = pos;
        this.status = s;
    }

    public Position getPosition() {
        return position;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
