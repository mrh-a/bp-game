package model;

import java.util.ArrayList;

public class Ship {
    private int size;
    private String name;
    private ArrayList<ShipStatus> shipStatus;

    void Ship(int size, String name, ArrayList<ShipStatus> shipStatus) {
        this.size = size;
        this.name = name;
        this.shipStatus = shipStatus;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ShipStatus> getShipStatus() {
        return shipStatus;
    }

    public void setShipStatus(ArrayList<ShipStatus> shipStatus) {
        this.shipStatus = shipStatus;
    }
}
