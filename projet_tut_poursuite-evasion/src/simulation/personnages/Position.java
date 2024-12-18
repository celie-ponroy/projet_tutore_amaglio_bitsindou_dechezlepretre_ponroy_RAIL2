package simulation.personnages;


import simulation.Deplacement;

import java.util.Objects;

public class Position {


    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public void deplacement(Deplacement d) {
        switch (d){
            case BAS:
                this.y += 1;
                break;
            case HAUT:
                this.y -= 1;
                break;
            case GAUCHE:
                this.x -= 1;
                break;
            case DROITE:
                this.x += 1;
                break;
            case DIAG_HAUT_GAUCHE:
                this.y -= 1;
                this.x -= 1;
                break;
            case DIAG_HAUT_DROITE:
                this.y -= 1;
                this.x += 1;
                break;
            case DIAG_BAS_GAUCHE:
                this.y += 1;
                this.x -= 1;
                break;
            case DIAG_BAS_DROITE:
                this.y += 1;
                this.x += 1;
                break;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "("+x+";"+y+")";
    }

}
