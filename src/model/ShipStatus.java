package model;

public class ShipStatus {
    private int position;
    private boolean isSinked;

    void ShipStatus(int position){
        this.position = position;
        this.isSinked = false;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isSinked() {
        return isSinked;
    }

    public void setSinked(boolean sinked) {
        isSinked = sinked;
    }
}
